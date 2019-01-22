package ecommander.persistence.mappers;

import ecommander.controllers.AppContext;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.*;
import ecommander.persistence.common.TemplateQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Перед добавлением айтема в индекс нужно открывать writer методом startUpdate, после обновления нужно закрывать
 * writer методом finishUdate
 *
 * Методы вставки, обновления и удаления айтемов в индекс сами вызывают startUpdate и finishUdate.
 * Таким образом, если внешний метод (из которого вызываются эти методы данного класса) не вызывает
 * startUpdate и finishUdate, закрытие райтера а обновление ридера происходит автоматически, никаких других
 * действий для этого не требуется.
 * Если закрытие райтера и обновление ридера нежелательно, т.к. обновляется множество айтемов (например, во время
 * интеграции), внешний метод, который вызывает методы обновления LuceneIndexMapper, должен сам вызывать
 * startUpdate и finishUdate. При этом счетчик одновременных обновлений будет больше 0, до последнего вызова
 * finishUdate в родительском методе закрытия райтера не произойдет, как не произойдет и обновления ридера.
 *
 * startUpdate и finishUdate должны вызываться в конструкции try - finally чтобы гарантировать корректный подсчет
 * одновременных обновлений и соответственно корректное закрытие райтера.
 */
public class LuceneIndexMapper implements DBConstants.ItemTbl {
	/**
	 * Класс для увеличения позиции токена в случае если он принадлежит другому значению множественого параметра
	 * По умолчанию множественные значения полей объединяются в одну строку с обычным увеличением позиции (на 1)
	 * В этом случае запросы, которые учитывают позицию, могут получать совпадения, когда разные слова запроса
	 * встречаются в разных значениях множественного параметра.
	 * @author E
	 *
	 */
	private static final class PositionIncrementTokenStream extends TokenStream {
		private boolean first = true;
		private PositionIncrementAttribute attribute;
		private final int positionIncrement;

		private PositionIncrementTokenStream(final int positionIncrement) {
			super();
			this.positionIncrement = positionIncrement;
			attribute = addAttribute(PositionIncrementAttribute.class);
		}

		@Override
		public boolean incrementToken() throws IOException {
			if (first) {
				first = false;
				attribute.setPositionIncrement(positionIncrement);
				return true;
			} else {
				return false;
			}
		}

		@Override
		public void reset() throws IOException {
			super.reset();
			first = true;
		}
	}

	private static final FieldType FULLTEXT_STORE_FIELD_TYPE = new FieldType();
	private static final FieldType POSITION_INCREMENT_FIELD_TYPE = new FieldType();
	static {
		FULLTEXT_STORE_FIELD_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		FULLTEXT_STORE_FIELD_TYPE.setStored(true);
		FULLTEXT_STORE_FIELD_TYPE.setTokenized(true);
		FULLTEXT_STORE_FIELD_TYPE.setStoreTermVectors(false);
		//FULLTEXT_STORE_FIELD_TYPE.setStoreTermVectorOffsets(true);
		//FULLTEXT_STORE_FIELD_TYPE.setStoreTermVectorPayloads(true);
		//FULLTEXT_STORE_FIELD_TYPE.setStoreTermVectorPositions(true);

		POSITION_INCREMENT_FIELD_TYPE.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
		POSITION_INCREMENT_FIELD_TYPE.setStored(false);
		POSITION_INCREMENT_FIELD_TYPE.setTokenized(true);
		POSITION_INCREMENT_FIELD_TYPE.setStoreTermVectors(false);
		//POSITION_INCREMENT_FIELD_TYPE.setStoreTermVectorOffsets(true);
		//POSITION_INCREMENT_FIELD_TYPE.setStoreTermVectorPayloads(true);
		//POSITION_INCREMENT_FIELD_TYPE.setStoreTermVectorPositions(true);
	}
	private static final PositionIncrementTokenStream TEN_SPACES_STREAM = new PositionIncrementTokenStream(10);
	private static final String TEN_SPACES_STRING = "          ";


	private static HashMap<String, Analyzer> analyzers = new HashMap<>();
	static {
		analyzers.put("default", new StandardAnalyzer());
		analyzers.put("ru", new RussianAnalyzer());
		analyzers.put("en", new EnglishAnalyzer());
	}
	private static Analyzer currentAnalyzer = null;
	
	public static final String HTML = "html";
	
	private static LuceneIndexMapper singleton;
	
	private FSDirectory directory;
	private volatile IndexWriter writer = null;
	private IndexReader reader = null;
	private HashMap<String, Parser> tikaParsers = new HashMap<>();
	private volatile int concurrentWritersCount = 0;
	private volatile int countProcessed = 0; // Количество проиндексированных айтемов
	
	private LuceneIndexMapper() throws IOException {
		directory = FSDirectory.open(Paths.get(AppContext.getLuceneIndexPath()));
		tikaParsers.put(HTML, new HtmlParser());
	}
	
	public static LuceneIndexMapper getSingleton() throws IOException {
		if (singleton == null)
			singleton = new LuceneIndexMapper();
		return singleton;
	}

	/**
	 * Должен вызываться перед процессом добавления айтемов в индекс (или удаления)
	 * @throws IOException
	 */
	public synchronized void startUpdate() throws IOException {
		if (concurrentWritersCount == 0) {
			try {
				closeWriter();
				IndexWriterConfig config = new IndexWriterConfig(getAnalyzer())
						.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND)
						.setRAMBufferSizeMB(5)
						.setMaxBufferedDocs(200)
						.setRAMPerThreadHardLimitMB(2);
				writer = new IndexWriter(directory, config);
			} catch (Exception e) {
				ServerLogger.error("LUCENE WRITER INIT", e);
				throw e;
			}
		}
		concurrentWritersCount++;
	}

	/**
	 * Подтвердить изменения. Изменения из буфера записываются в файлы индекса
	 * @throws IOException
	 */
	public synchronized void commit() throws IOException {
		if (concurrentWritersCount > 0 && writer != null)
			writer.commit();
		else
			throw new IllegalStateException("can not commit unopened index writer");
	}

	/**
	 * Отменить изменения. Изменения не подтверждаются и writer закрывается
	 * @throws IOException
	 */
	public synchronized void rollback() throws IOException {
		if (concurrentWritersCount > 0 && writer != null)
			writer.rollback();
		else
			throw new IllegalStateException("can not rollback unopened index writer");
	}

	/**
	 * Завершить обновление индекса.
	 * IndexWriter закрывается в случае если нет параллельно идущих незавершенных записей
	 */
	public synchronized void finishUpdate() throws IOException {
		if (concurrentWritersCount <= 0 || writer == null) {
			throw new IllegalStateException("can not finish updating unopened writer");
		}
		concurrentWritersCount--;
		if (concurrentWritersCount < 0)
			concurrentWritersCount = 0;
		if (concurrentWritersCount == 0) {
			writer.commit();
			closeWriter();
		}
		refreshReader();
	}

	/**
	 * Принудительно закрывает writer. Это аварийный метод, должен использоваться только
	 * в нештатных ситуациях.
	 * @throws IOException
	 */
	public synchronized void forceCloseWriter() throws IOException {
		concurrentWritersCount = 0;
		closeWriter();
		refreshReader();
	}

	private void closeWriter() throws IOException {
		closeReader();
		if (writer != null)
			writer.close();
		writer = null;
	}

	public synchronized void close() throws IOException {
		closeWriter();
		if (directory != null)
			directory.close();
	}

	private static Analyzer getAnalyzer() {
		if (currentAnalyzer == null) {
			currentAnalyzer = analyzers.get(AppContext.getCurrentLocale().getLanguage());
			if (currentAnalyzer == null)
				currentAnalyzer = analyzers.get("default");
			}
		return currentAnalyzer;
	}

	/**
	 * Создать или обновить reader после завершения записи в индекс
	 * @throws IOException
	 */
	private void refreshReader() throws IOException {
		if (!DirectoryReader.indexExists(directory)) {
			reader = null;
			return;
		}
		if (reader == null) {
			reader = DirectoryReader.open(directory);
		} else {
			DirectoryReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader);
			if (newReader != null)
				reader = newReader;
		}
	}

	/**
	 * Получить reader
	 * @return
	 * @throws IOException
	 */
	private IndexReader getReader() throws IOException {
		if (reader == null)
			refreshReader();
		return reader;
	}

	private void closeReader() throws IOException {
		if (reader != null) {
			reader.close();
			reader = null;
		}
	}
	
	private synchronized void createNewIndex() throws IOException {
		try {
			startUpdate();
			writer.deleteAll();
		} catch (Exception e) {
			ServerLogger.error("Unable to create new Lucene index", e);
		} finally {
			finishUpdate();
		}
		closeReader();
	}

	/**
	 * Добавить новый айтем в индекс
	 * @param item
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	private synchronized void insertItem(Item item) throws IOException, SAXException, TikaException {
		// Ссылки не добавлять в индекс
		if (!item.getItemType().isFulltextSearchable())
			return;
		Document itemDoc = new Document();
		// Устанавливается ID айтема. Строковое значение, т.к. для удаления и обновления нужно исиользовать Term,
		// который поддерживает только строковые значения
		itemDoc.add(new StringField(I_ID, item.getId() + "", Store.YES));
		// Заполняются все типы айтема (иерархия типов айтема)
		Set<String> itemPreds = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName());
		for (String pred : itemPreds) {
			itemDoc.add(new StringField(I_TYPE_ID, ItemTypeRegistry.getItemTypeId(pred) + "", Store.YES));
		}
		//		// Заполняются все предшественники (в которые айтем вложен)
		//		String[] containerIds = StringUtils.split(item.getPredecessorsPath(), '/');
		//		for (String contId : containerIds) {
		//			itemDoc.add(new StringField(DBConstants.Item.DIRECT_PARENT_ID, contId, Store.YES));
		//		}
		// Заполняются все индексируемые параметры
		// Заполнение полнотекстовых параметров

		for (String ftParam : item.getItemType().getFulltextParams()) {
			boolean needIncrement = false;
			for (ParameterDescription param : item.getItemType().getFulltextParameterList(ftParam)) {
				if (param.isMultiple()) {
					for (SingleParameter sp : ((MultipleParameter) item.getParameter(param.getId())).getValues()) {
						createParameterField(param, sp.outputValue(), itemDoc, ftParam, needIncrement);
						needIncrement = true;
					}
				} else {
					createParameterField(param, ((SingleParameter) item.getParameter(param.getId())).outputValue(),
							itemDoc, ftParam, needIncrement);
					needIncrement = true;
				}
			}
		}
		// Заполнение параметров для фильтрации
		for (ParameterDescription param : item.getItemType().getParameterList()) {
			if (param.isFulltextFilterable()) {
				if (param.isMultiple()) {
					for (SingleParameter sp : ((MultipleParameter) item.getParameter(param.getId())).getValues()) {
						DataTypeMapper.setLuceneItemDocField(param.getType(), itemDoc, param.getName(), sp.getValue());
					}
				} else {
					DataTypeMapper.setLuceneItemDocField(param.getType(), itemDoc, param.getName(),
							item.getParameter(param.getId()).getValue());
				}
			}
		}
		// Добавление айтема в индекс
		//		writer.deleteDocuments(new TermQuery(new Term(DBConstants.Item.ID, item.getId() + "")));
		//		ServerLogger.debug(item.getId());
		writer.updateDocument(new Term(I_ID, item.getId() + ""), itemDoc);
		//		writer.addDocument(itemDoc);
	}
	/**
	 * Добавить параметр в виде поля для документа Lucene.
	 * @param param
	 * @param value
	 * @param luceneDoc
	 * @param luceneParamName - название поля документа для этого параметра
	 * @param needIncrement - нужно ли отделение позиции этого значения от других значений (чтобы один запрос не находил смежные значения)
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	private void createParameterField(ParameterDescription param, String value, Document luceneDoc, String luceneParamName
			, boolean needIncrement) throws IOException, SAXException, TikaException {
		if (StringUtils.isBlank(value))
			return;
		if (needIncrement)
			//luceneDoc.add(new TextField(luceneParamName, new PositionIncrementTokenStream(10)));
			luceneDoc.add(new Field(luceneParamName, TEN_SPACES_STREAM, POSITION_INCREMENT_FIELD_TYPE));
		Field field;
		if (param.needFulltextParsing() && tikaParsers.containsKey(param.getFulltextParser())) {
			InputStream input = IOUtils.toInputStream(value, "UTF-8");
			ContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			tikaParsers.get(param.getFulltextParser()).parse(input, handler, metadata, new ParseContext());
			//field = new TextField(luceneParamName, handler.toString(), Store.YES);
			field = new Field(luceneParamName, handler.toString(), FULLTEXT_STORE_FIELD_TYPE);
		} else {
			//field = new TextField(luceneParamName, value, Store.YES);
			field = new Field(luceneParamName, value, FULLTEXT_STORE_FIELD_TYPE);
		}
		luceneDoc.add(field);
	}

	/**
	 * Обновить айтем, который уже присутствует в индеске.
	 * Если айтема еще нет в индексе, он добавляется
	 * @param item
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public synchronized void updateItem(Item item) throws IOException, SAXException, TikaException {
		// Ссылки не добавлять в индекс (и не дуалять соответственно)
		if (!item.getItemType().isFulltextSearchable())
			return;
		try {
			startUpdate();
			writer.deleteDocuments(new Term(I_ID, item.getId() + ""));
			insertItem(item);
		} finally {
			finishUpdate();
		}
	}

	/**
	 * Удалить айтем и все вложенные в него айтемы из индекса
	 * Сабмит не вызывается. Его надо вызывать отдельно
	 * @param itemIds
	 * @throws IOException
	 */
	public synchronized void deleteItem(Long... itemIds) throws IOException {
		try {
			startUpdate();
			Term[] deleteTerms = new Term[itemIds.length];
			for (int i = 0; i < itemIds.length; i++) {
				deleteTerms[i] = new Term(I_ID, itemIds[i] + "");
			}
			writer.deleteDocuments(deleteTerms);
		} finally {
			finishUpdate();
		}
	}

	/**
	 * Загрузка по одному или нескольким запросам.
	 * Если запросов несколько, то они выполняются в порядке появления в массиве, первый запрос считается самым
	 * строгим и результаты по нему самыми релевантными, второй менее строгий, третий - еще менее и т. д.
	 * Максимальный score документов второго и последующих запросов равны минимальному score предыдущего запроса
	 *
	 * Запросы сгруппированы. Следующая группа запросов выполняется только в случае если предыдущая группа не
	 * вернула результатов.
	 *
	 * @param queries
	 * @param filter
	 * @param paramNames - массив названий параметров, которые могут содержать искомый текст
	 * @param maxResults
	 * @param threshold - часть рейтинга первого места поиска, результаты с рейтингом ниже которой считаются нерелевантными
	 * @return
	 * @throws IOException
	 */
	public synchronized LinkedHashMap<Long, String> getItems(ArrayList<ArrayList<Query>> queries, Query filter, String[] paramNames,
	                                                         int maxResults, float threshold)
			throws IOException {
		if (getReader() == null)
			return new LinkedHashMap<>(0);
		Timer.getTimer().start(Timer.LOAD_LUCENE_ITEMS);
		IndexSearcher search = new IndexSearcher(getReader());
		LinkedHashMap<Long, String> result = new LinkedHashMap<>();
		float globalMaxScore = -1f;
		float minScore = -1f;
		boolean isFinished = false;
		// Ограничение выдачи по релевантности:
		// документы, которые набрали менее 33% очков лучшего результата отбрасываются
		if (threshold < 0f) 
			threshold = 0.05f;

		// Все группы запросов в порядке появления в массиве
		// Переход к следующей группе происходит только в случае, если предыдущая группа не дала результатов
		int totalDocs = 0;
		for (List<Query> group : queries) {
			for (Query query : group) {
				TopDocs foundDocs;
				if (filter != null) {
					BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
					boolQuery.add(query, BooleanClause.Occur.MUST);
					boolQuery.add(filter, BooleanClause.Occur.FILTER);
					query = boolQuery.build();
				}
				foundDocs = search.search(query, maxResults);
				float scoreQuotient = 1f;

				// Инициализация максиального количества очков и коэффициента пересчета
				if (foundDocs.scoreDocs.length > 0) {
					if (globalMaxScore <= 0)
						globalMaxScore = foundDocs.scoreDocs[0].score;
					if (minScore > 0)
						scoreQuotient = minScore / foundDocs.scoreDocs[0].score;
				}

				// Подготовка подсветки найденных результатов
				SimpleHTMLFormatter formatter = new SimpleHTMLFormatter(); //Uses HTML &lt;B&gt;&lt;/B&gt; tag to highlight the searched terms
				QueryScorer scorer = new QueryScorer(query); //It scores text fragments by the number of unique query terms found
				Highlighter highlighter = new Highlighter(formatter, scorer); //used to markup highlighted terms found in the best sections of a text
				Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, 40); //It breaks text up into same-size texts but does not split up spans
				highlighter.setTextFragmenter(fragmenter); //set fragmenter to highlighter

				// Итерация по результатам поиска
				for (ScoreDoc scoreDoc : foundDocs.scoreDocs) {

					// нормализованный счет (score)
					float normalScore = scoreDoc.score * scoreQuotient;
					if (globalMaxScore * threshold > normalScore) {
						isFinished = true;
						break;
					}
					Document doc = search.doc(scoreDoc.doc);

					// Подсветка найденных фрагментов
					//Fields docFields = getReader().getTermVectors(scoreDoc.doc);
					XmlDocumentBuilder highlighted = XmlDocumentBuilder.newDocPart();
					for (String paramName : paramNames) {
						//TokenStream stream = TokenSources.getTermVectorTokenStreamOrNull(paramName, docFields, -1);
						String text = StringUtils.join(doc.getValues(paramName), TEN_SPACES_STRING);
						String[] bestFragments = new String[0];
						try {
							//bestFragments = highlighter.getBestFragments(stream, text, 3);
							bestFragments = highlighter.getBestFragments(getAnalyzer(), paramName, text, 3);
						} catch (Exception e) {
							ServerLogger.warn("Lucene highlighter error", e);
						}
						for (String bestFragment : bestFragments) {
							highlighted.startElement("p", "name", paramName).addText(bestFragment).endElement();
						}
					}
					Long itemId = Long.parseLong(doc.get(I_ID));
					result.put(itemId, highlighted.toString());
					totalDocs++;
					ServerLogger.debug("\t\t---------\n" + search.explain(query, scoreDoc.doc));
				}

				if (foundDocs.scoreDocs.length > 0)
					minScore = foundDocs.scoreDocs[foundDocs.scoreDocs.length - 1].score * scoreQuotient;

				if (isFinished || totalDocs >= maxResults) break;
			}
			// Переход к следующей группе происходит только в случае, если предыдущая группа не дала результатов
			if (totalDocs > 0)
				break;
		}
		Timer.getTimer().stop(Timer.LOAD_LUCENE_ITEMS);
		return result;
	}

	/**
	 * Произвести переиндексацию всех айтемов
	 * @return
	 * @throws Exception
	 */
	public synchronized boolean reindexAll() throws Exception {
		final int LIMIT = 500;
		createNewIndex();
		try {
			startUpdate();
			countProcessed = 0;
			for (String itemName : ItemTypeRegistry.getItemNames()) {
				ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
				if (itemDesc.isFulltextSearchable()) {
					ArrayList<Item> items;
					long startFrom = 0;
					do {
						try (Connection conn = MysqlConnector.getConnection()) {
							items = ItemMapper.loadByTypeId(itemDesc.getTypeId(), LIMIT, startFrom, conn);
						}
						for (Item item : items) {
							updateItem(item);
						}
						if (items.size() > 0)
							startFrom = items.get(items.size() - 1).getId() + 1;
						countProcessed += items.size();
						ServerLogger.debug("Indexed: " + countProcessed + " items");
						commit();
					} while (items.size() == LIMIT);
				}
			}
			commit();
		} finally {
			finishUpdate();
		}
		return true;
	}
	/**
	 * Удалить все документы из индекса
	 * @throws IOException
	 */
	public void deleteIndex() throws IOException {
		createNewIndex();
	}

	/**
	 * Найти ID айтемов, которые соответствуют запросу Lucene
	 * @param query - запрос
	 * @param filter
	 * @param paramNames - массив названий параметров, которые могут содержать искомый текст
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @param threshold - часть (дробь меньше 1) от рейтинга первого места поиска, ниже которой результаты поиска начинают отбрасыватся (для релевантности)
	 * @return
	 * @throws IOException
	 */
	public LinkedHashMap<Long, String> getItems(Query query, Query filter, String[] paramNames, int maxResults, float threshold) throws IOException {
		ArrayList<Query> single = new ArrayList<>();
		single.add(query);
		ArrayList<ArrayList<Query>> singleArray = new ArrayList<>();
		singleArray.add(single);
		return getItems(singleArray, filter, paramNames, maxResults, threshold);
	}

	/**
	 * Найти ID айтемов, которые соответствуют запросу Lucene
	 * @param query - запрос
	 * @param filter
	 * @param paramNames - массив названий параметров, которые могут содержать искомый текст
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @return
	 * @throws IOException
	 */
	public LinkedHashMap<Long, String> getItems(Query query, Query filter, String[] paramNames, int maxResults) throws IOException {
		return getItems(query, filter, paramNames, maxResults, -1);
	}

	/**
	 * Создать парсер запросов
	 * @param field
	 * @return
	 */
	public static QueryParser createQueryParser(String field) {
		return new QueryParser(field, getAnalyzer());
	}
	/**
	 * Разобрать строку с помощью текущего анализатора
	 * @param string
	 * @return
	 */
	public static LinkedHashSet<String> tokenizeString(String string) {
		LinkedHashSet<String> result = new LinkedHashSet<>();
		try {
			TokenStream stream = getAnalyzer().tokenStream(null, string);
			stream.reset();
			while (stream.incrementToken()) {
				result.add(stream.getAttribute(CharTermAttribute.class).toString());
			}
			stream.end();
			stream.close();
		} catch (IOException e) {
			// not thrown b/c we're using a string reader...
			throw new RuntimeException(e);
		}
		return result;
	}
	/**
	 * Вернуть количество проиндексированных айтемов
	 * @return
	 * @throws IOException
	 */
	public int getCountProcessed() {
		return countProcessed;
	}
}
