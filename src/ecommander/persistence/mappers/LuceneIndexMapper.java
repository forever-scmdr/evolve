package ecommander.persistence.mappers;

import ecommander.controllers.AppContext;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
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
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
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
	private IndexWriter writer = null;
	private IndexReader reader = null;
	private HashMap<String, Parser> tikaParsers = new HashMap<>();
	private int concurrentWritersCount = 0;
	private int countProcessed = 0; // Количество проиндексированных айтемов
	
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
			if (writer != null)
				writer.close();
			IndexWriterConfig config = new IndexWriterConfig(getAnalyzer())
					.setOpenMode(OpenMode.CREATE_OR_APPEND)
					.setRAMBufferSizeMB(5)
					.setMaxBufferedDocs(200)
					.setRAMPerThreadHardLimitMB(2);
			writer = new IndexWriter(directory, config);
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
			writer.close();
			writer = null;
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
		if (writer != null)
			writer.close();
		writer = null;
		refreshReader();
	}

	public synchronized void close() throws IOException {
		if (writer != null)
			writer.close();
		if (reader != null)
			reader.close();
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
		if (reader ==  null) {
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
	public synchronized void insertItem(Item item) throws IOException, SAXException, TikaException {
		// Ссылки не добавлять в индекс
		if (!item.getItemType().isFulltextSearchable())
			return;
		try {
			startUpdate();
			Document itemDoc = new Document();
			// Устанавливается ID айтема. Строковое значение, т.к. для удаления и обновления нужно исиользовать Term,
			// который поддерживает только строковые значения
			itemDoc.add(new StringField(I_ID, item.getId() + "", Store.YES));
			// Заполняются все типы айтема (иерархия типов айтема)
			Set<String> itemPreds = ItemTypeRegistry.getItemPredecessorsExt(item.getTypeName());
			for (String pred : itemPreds) {
				itemDoc.add(new IntPoint(I_TYPE_ID, ItemTypeRegistry.getItemTypeId(pred)));
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
						createParameterField(param, ((SingleParameter) item.getParameter(param.getId())).outputValue(), itemDoc, ftParam,
								needIncrement);
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
		} finally {
			finishUpdate();
		}
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
	private void createParameterField(ParameterDescription param, String value, Document luceneDoc, String luceneParamName,
			boolean needIncrement) throws IOException, SAXException, TikaException {
		if (StringUtils.isBlank(value))
			return;
		if (needIncrement)
			luceneDoc.add(new TextField(luceneParamName, new PositionIncrementTokenStream(10)));
		TextField field;
		if (param.needFulltextParsing() && tikaParsers.containsKey(param.getFulltextParser())) {
			InputStream input = IOUtils.toInputStream(value, "UTF-8");
			ContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			tikaParsers.get(param.getFulltextParser()).parse(input, handler, metadata, new ParseContext());
			field = new TextField(luceneParamName, handler.toString(), Store.NO);
		} else {
			field = new TextField(luceneParamName, value, Store.NO);
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
	 * @param queries
	 * @param filter
	 * @param maxResults
	 * @param threshold - часть рейтинга первого места поиска, результаты с рейтингом ниже которой считаются нерелевантными
	 * @return
	 * @throws IOException
	 */
	public synchronized Long[] getItems(List<Query> queries, Query filter, int maxResults, float threshold)
			throws IOException {
		if (getReader() == null)
			return new Long[0];
		Timer.getTimer().start(Timer.LOAD_LUCENE_ITEMS);
		IndexSearcher search = new IndexSearcher(getReader());
		LinkedHashSet<Long> result = new LinkedHashSet<>();
		float globalMaxScore = -1f;
		float minScore = -1f;
		boolean isFinished = false;
		// Ограничение выдачи по релевантности:
		// документы, которые набрали менее 33% очков лучшего результата отбрасываются
		if (threshold < 0f) 
			threshold = 0.33f;
		
		// Все запросы в порядке появления в массиве
		for (Query query : queries) {
			TopDocs td;
			if (filter != null) {
				BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
				boolQuery.add(query, BooleanClause.Occur.MUST);
				boolQuery.add(filter, BooleanClause.Occur.FILTER);
				query = boolQuery.build();
			}
			td = search.search(query, maxResults);
			float scoreQuotient = 1f;
			// Инициализация максиального количества очков и коэффициента пересчета
			if (td.scoreDocs.length > 0) {
				if (globalMaxScore <= 0)
					globalMaxScore = td.scoreDocs[0].score;
				if (minScore > 0)
					scoreQuotient = minScore / td.scoreDocs[0].score;
			}
			// Итерация по результатам поиска
			for (ScoreDoc scoreDoc : td.scoreDocs) {
				// нормализованный счет (score)
				float normalScore = scoreDoc.score * scoreQuotient;
				if (globalMaxScore * threshold > normalScore) {
					isFinished = true;
					break;
				}
				Document doc = search.doc(scoreDoc.doc);
				Long itemId = Long.parseLong(doc.get(I_ID));
				result.add(itemId);
				ServerLogger.debug("\t\t---------\n" + search.explain(query, scoreDoc.doc));
			}
			
			if (td.scoreDocs.length > 0)
				minScore = td.scoreDocs[td.scoreDocs.length - 1].score * scoreQuotient;
			
			if (isFinished) break;
		}
		Timer.getTimer().stop(Timer.LOAD_LUCENE_ITEMS);
		return result.toArray(new Long[result.size()]);
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
						items = loadByTypeId(itemDesc.getTypeId(), LIMIT, startFrom);
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
	 * Загрузить все айтемы определенного типа с ограничением по количеству и ID больше или равным определенному
	 * @param itemId
	 * @param limit
	 * @param startFromId
	 * @return
	 * @throws Exception
	 */
	private ArrayList<Item> loadByTypeId(int itemId, int limit, long startFromId) throws Exception {
		ArrayList<Item> result = new ArrayList<>();
		// Полиморфная загрузка
		TemplateQuery select = new TemplateQuery("Select items for indexing");
		select.SELECT("*").FROM(ITEM_TBL).WHERE().col(I_TYPE_ID).int_(itemId).AND()
				.col(I_ID, ">=").long_(startFromId).ORDER_BY(I_ID).LIMIT(limit);
		try (Connection conn = MysqlConnector.getConnection();
		     PreparedStatement pstmt = select.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			// Создание айтемов
			while (rs.next()) {
				result.add(ItemMapper.buildItem(rs, ItemTypeRegistry.getPrimaryAssoc().getId(), 0L));
			}
		}
		return result;
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
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @param threshold - часть (дробь меньше 1) от рейтинга первого места поиска, ниже которой результаты поиска начинают отбрасыватся (для релевантности)
	 * @return
	 * @throws IOException
	 */
	public Long[] getItems(Query query, Query filter, int maxResults, float threshold) throws IOException {
		return getItems(Collections.singletonList(query), filter, maxResults, threshold);
	}

	/**
	 * Найти ID айтемов, которые соответствуют запросу Lucene
	 * @param query - запрос
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @return
	 * @throws IOException
	 */
	public Long[] getItems(Query query, Query filter, int maxResults) throws IOException {
		return getItems(query, filter, maxResults, -1);
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
