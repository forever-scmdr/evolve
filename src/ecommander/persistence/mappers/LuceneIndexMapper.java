package ecommander.persistence.mappers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;

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
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.MultipleParameter;
import ecommander.model.ParameterDescription;
import ecommander.model.SingleParameter;

public class LuceneIndexMapper {
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

		public PositionIncrementTokenStream(final int positionIncrement) {
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
	
	private static HashMap<String, Analyzer> analyzers = new HashMap<String, Analyzer>();
	static {
		analyzers.put("default", new StandardAnalyzer());
		analyzers.put("ru", new RussianAnalyzer());
		analyzers.put("en", new EnglishAnalyzer());
	}
	private static Analyzer currentAnalyzer = null;
	
	public static final String HTML = "html";
	
	private static LuceneIndexMapper singleton;
	
	private FSDirectory directory;
	private IndexWriter writer;
	private SearcherManager sm;
	private HashMap<String, Parser> tikaParsers = new HashMap<String, Parser>();
	private int countProcessed = 0; // Количество проиндексированных айтемов
	
	private LuceneIndexMapper() throws IOException {
		directory = NIOFSDirectory.open(new File(AppContext.getLuceneIndexPath()));
		tikaParsers.put(HTML, new HtmlParser());
	}
	
	private static LuceneIndexMapper getSingleton() throws IOException {
		if (singleton == null)
			singleton = new LuceneIndexMapper();
		return singleton;
	}
	
	private static Analyzer getAnalyzer() {
		if (currentAnalyzer == null) {
			currentAnalyzer = analyzers.get(AppContext.getCurrentLocale().getLanguage());
			if (currentAnalyzer == null)
				currentAnalyzer = analyzers.get("default");
			}
		return currentAnalyzer;
	}
	
	private synchronized void commitWriterInt() throws IOException {
		if (writer != null) {
			writer.commit();
			if (sm != null)
				sm.maybeRefreshBlocking();
		}
	}
	
	private synchronized void rollbackWriterInt() throws IOException {
		if (writer != null) {
			writer.rollback();
			if (sm != null)
				sm.close();
			sm = null;
		}
	}
	/**
	 * Получить writer для записи в файл индекса
	 * @return
	 * @throws IOException
	 */
	private synchronized void ensureWriter() throws IOException {
		if (writer == null) {
			IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, getAnalyzer())
				.setOpenMode(OpenMode.CREATE_OR_APPEND)
				.setRAMBufferSizeMB(2)
				.setMaxBufferedDocs(200)
				.setMaxThreadStates(3)
				.setRAMPerThreadHardLimitMB(1);
//			if (IndexWriter.isLocked(directory)) {
//		        IndexWriter.unlock(directory);
//		    }
			writer = new IndexWriter(directory, config);
		}
	}
	
	private synchronized void closeWriterInt() throws IOException {
		if (writer != null)
			writer.close();
		writer = null;
		if (sm != null)
			sm.maybeRefreshBlocking();
	}
	/**
	 * Получить менеджер для чтения индекса
	 * @return
	 * @throws IOException
	 */
	private SearcherManager getReader() throws IOException {
		if (sm == null) {
			if (DirectoryReader.indexExists(directory)) {
				sm = new SearcherManager(directory, null);
			} else {
				return null;
			}
		}
		return sm;
	}
	
	private void closeReader() throws IOException {
		if (sm != null) {
			sm.close();
			sm = null;
		}
	}
	
	private synchronized void createNewIndex() throws IOException {
		ensureWriter();
		writer.deleteAll();
		writer.commit();
		writer.close();
		writer = null;
		closeReader();
	}
	
	private synchronized void insertItemInt(Item item, boolean needClose) throws IOException, SAXException, TikaException {
		// Ссылки не добавлять в индекс
		if (!item.getItemType().isFulltextSearchable())
			return;
		ensureWriter();
		Document itemDoc = new Document();
		// Устанавливается ID айтема
		itemDoc.add(new StringField(DBConstants.Item.ID, item.getId() + "", Store.YES));
		// Заполняются все типы айтема (иерархия типов айтема)
		Integer[] predIds = ItemTypeRegistry.getItemPredecessorsIds(item.getTypeId());
		for (Integer predId : predIds) {
			itemDoc.add(new StringField(DBConstants.Item.TYPE_ID, predId.toString(), Store.NO));
		}
		itemDoc.add(new StringField(DBConstants.Item.TYPE_ID, item.getTypeId() + "", Store.NO));
		// Заполняются все предшественники (в которые айтем вложен)
		String[] containerIds = StringUtils.split(item.getPredecessorsPath(), '/');
		for (String contId : containerIds) {
			itemDoc.add(new StringField(DBConstants.Item.DIRECT_PARENT_ID, contId, Store.YES));
		}
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
						itemDoc.add(new StringField(param.getName(), sp.outputValue(), Store.NO));
					}
				} else {
					itemDoc.add(new StringField(param.getName(), ((SingleParameter) item.getParameter(param.getId())).outputValue(), Store.NO));
				}
			}
		}
		// Добавление айтема в индекс
//		writer.deleteDocuments(new TermQuery(new Term(DBConstants.Item.ID, item.getId() + "")));
//		ServerLogger.debug(item.getId());
		writer.updateDocument(new Term(DBConstants.Item.ID, item.getId() + ""), itemDoc);
		if (needClose)
			closeWriter();
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
	private void createParameterField(ParameterDescription param, String value, Document luceneDoc, String luceneParamName,
			boolean needIncrement) throws IOException, SAXException, TikaException {
		if (StringUtils.isBlank(value))
			return;
		if (needIncrement)
			luceneDoc.add(new TextField(luceneParamName, new PositionIncrementTokenStream(10)));
		TextField field = null;
		if (param.needFulltextParsing() && tikaParsers.containsKey(param.getFulltextParser())) {
			InputStream input = IOUtils.toInputStream(value, "UTF-8");
			ContentHandler handler = new BodyContentHandler();
			Metadata metadata = new Metadata();
			tikaParsers.get(param.getFulltextParser()).parse(input, handler, metadata, new ParseContext());
			field = new TextField(luceneParamName, handler.toString(), Store.NO);
		} else {
			field = new TextField(luceneParamName, value, Store.NO);
		}
		if (param.isFulltextBoosted())
			field.setBoost(param.getFulltextBoost());
		luceneDoc.add(field);
	}
	
	private synchronized void updateItemInt(Item item, boolean needClose) throws IOException, SAXException, TikaException {
		// Ссылки не добавлять в индекс (и не дуалять соответственно)
		if (!item.getItemType().isFulltextSearchable())
			return;
		ensureWriter();
		writer.deleteDocuments(new Term(DBConstants.Item.ID, item.getId() + ""));
		insertItemInt(item, needClose);
	}
	
	private synchronized void deleteItemInt(Item item, boolean needClose) throws IOException {
		ensureWriter();
		Term[] deleteTerms = new Term[2];
		deleteTerms[0] = new Term(DBConstants.Item.DIRECT_PARENT_ID, item.getId() + "");
		deleteTerms[1] = new Term(DBConstants.Item.ID, item.getId() + "");
		writer.deleteDocuments(deleteTerms);
		if (needClose)
			closeWriter();
	}
	
	private synchronized void deleteSubitemsInt(Item item, boolean needClose) throws IOException {
		ensureWriter();
		Term deleteTerm = new Term(DBConstants.Item.DIRECT_PARENT_ID, item.getId() + "");
		writer.deleteDocuments(deleteTerm);
		if (needClose)
			closeWriter();
	}
	/**
	 * Загрузка по одному или нескольким запросам.
	 * Если запросов несколько, то они выполняются в порядке появления в массиве, первый запрос считается самым
	 * строгим и результаты по нему самыми релевантными, второй менее строгий, третий - еще менее и т. д.
	 * Максимальный score документов второго и последующих запросов равны минимальному score предыдущего запроса
	 * @param query
	 * @param filter
	 * @param maxResults
	 * @param threshold - часть рейтинга первого места поиска, результаты с рейтингом ниже которой считаются нерелевантными
	 * @param parents - все предки загруженных айтемов (ID => массив PARENT_ID)
	 * @return
	 * @throws IOException
	 */
	private synchronized Long[] getItemsInt(List<Query> queries, Filter filter, int maxResults, float threshold, HashMap<Long, Long[]> parents)
			throws IOException {
		SearcherManager reader = getReader();
		if (reader == null)
			return new Long[0];
		Timer.getTimer().start(Timer.LOAD_LUCENE_ITEMS);
		IndexSearcher search = reader.acquire();
		LinkedHashSet<Long> result = new LinkedHashSet<Long>();
		float globalMaxScore = -1f;
		float minScore = -1f;
		boolean isFinished = false;
		// Ограничение выдачи по релевантности:
		// документы, которые набрали менее 33% очков лучшего результата отбрасываются
		if (threshold < 0f) 
			threshold = 0.33f;
		
		// Все запросы в порядке появления в массиве
		for (Query query : queries) {
			TopDocs td = null;
			if (filter != null)
				td = search.search(query, filter, maxResults);
			else
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
				Long itemId = Long.parseLong(doc.get(DBConstants.Item.ID));
				result.add(itemId);
				// TODO <fix> удалить весь if
				if (parents != null) {
					String[] parentsStr = doc.getValues(DBConstants.Item.DIRECT_PARENT_ID);
					Long[] parentIds = new Long[parentsStr.length];
					for (int i = 0; i < parentsStr.length; i++) {
						parentIds[i] = Long.parseLong(parentsStr[i]);
					}
					parents.put(itemId, parentIds);
				}
				ServerLogger.debug("\t\t---------\n" + search.explain(query, scoreDoc.doc));
			}
			
			if (td.scoreDocs.length > 0)
				minScore = td.scoreDocs[td.scoreDocs.length - 1].score * scoreQuotient;
			
			if (isFinished) break;
		}
		reader.release(search);
		Timer.getTimer().stop(Timer.LOAD_LUCENE_ITEMS);
		return result.toArray(new Long[result.size()]);
	}
	
	private synchronized boolean reindexInt() throws Exception {
		final int LIMIT = 500;
		createNewIndex();
		countProcessed = 0;
		for (String itemName : ItemTypeRegistry.getItemNames()) {
			ItemType itemDesc = ItemTypeRegistry.getItemType(itemName);
			if (itemDesc.isFulltextSearchable()) {
				ArrayList<Item> items = null;
				long startFrom = 0;
				do {
					items = loadByTypeId(itemDesc.getTypeId(), LIMIT, startFrom);
					for (Item item : items) {
						updateItemInt(item, false);
					}
					if (items.size() > 0)
						startFrom = items.get(items.size() - 1).getId() + 1;
					countProcessed += items.size();
					ServerLogger.debug("Indexed: " + countProcessed + " items");
					commitWriterInt();
				} while (items.size() == LIMIT);
			}
		}
		closeWriterInt();
		return true;
	}
	/**
	 * Загрузить все айтемы определенного типа с ограничением по количеству и ID больше или равным определенному
	 * @param itemId
	 * @param limit
	 * @param startFromId
	 * @param context
	 * @return
	 * @throws Exception
	 */
	private static ArrayList<Item> loadByTypeId(int itemId, int limit, long startFromId) throws Exception {
		ArrayList<Item> result = new ArrayList<Item>();
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			// Полиморфная загрузка
			String sql 
				= "SELECT * FROM " + DBConstants.Item.TABLE
				+ " WHERE " + DBConstants.Item.TYPE_ID + " = " + itemId 
				+ " AND " + DBConstants.Item.ID + " >= " + startFromId 
				+ " ORDER BY " + DBConstants.Item.ID + " LIMIT " + limit;
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql.toString());
			// Создание айтемов
			while (rs.next()) {
				result.add(ItemMapper.buildItem(rs, DBConstants.Item.DIRECT_PARENT_ID));
			}
			rs.close();
			stmt.close();
			return result;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Подтвердить изменения. Изменения из буфера записываются в файлы индекса
	 * @throws IOException
	 */
	public static void commit() throws IOException {
		getSingleton().commitWriterInt();
	}
	/**
	 * Отменить изменения. Изменения не подтверждаются и writer закрывается
	 * @throws IOException
	 */
	public static void rollback() throws IOException {
		getSingleton().rollbackWriterInt();
	}
	/**
	 * writer закрывается, а открывается только после следующего запроса.
	 * @throws IOException
	 */
	public static void closeWriter() throws IOException {
		getSingleton().closeWriterInt();
	}
	/**
	 * Удалить все документы из индекса
	 * @throws IOException
	 */
	public static void deleteIndex() throws IOException {
		getSingleton().createNewIndex();
	}
	/**
	 * Добавить новый айтем в индекс
	 * @param item
	 * @throws IOException
	 * @throws TikaException 
	 * @throws SAXException 
	 */
	public static void insertItem(Item item, boolean... needClose) throws IOException, SAXException, TikaException {
		boolean doClose = true;
		if (needClose.length > 0)
			doClose = needClose[0];
		getSingleton().insertItemInt(item, doClose);
	}
	/**
	 * Обновить айтем, который уже присутствует в индеске.
	 * Если айтема еще нет в индексе, он добавляется
	 * @param item
	 * @throws IOException
	 * @throws TikaException 
	 * @throws SAXException 
	 */
	public static void updateItem(Item item, boolean... needClose) throws IOException, SAXException, TikaException {
		boolean doClose = true;
		if (needClose.length > 0)
			doClose = needClose[0];
		getSingleton().updateItemInt(item, doClose);
	}
	/**
	 * Удалить айтем и все вложенные в него айтемы из индекса
	 * Сабмит не вызывается. Его надо вызывать отдельно
	 * @param item
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public static void deleteItem(Item item, boolean... needClose) throws IOException, SAXException, TikaException {
		boolean doClose = true;
		if (needClose.length > 0)
			doClose = needClose[0];
		getSingleton().deleteItemInt(item, doClose);
	}
	/**
	 * Удалить все сабайтемы определенного айтема
	 * Сабмит не вызывается. Его надо вызывать отдельно
	 * @param item
	 * @throws IOException
	 * @throws SAXException
	 * @throws TikaException
	 */
	public static void deleteSubitems(Item item, boolean... needClose) throws IOException, SAXException, TikaException {
		boolean doClose = true;
		if (needClose.length > 0)
			doClose = needClose[0];
		getSingleton().deleteSubitemsInt(item, doClose);
	}
	/**
	 * Найти ID айтемов, которые соответствуют запросу Lucene
	 * @param query - запрос
	 * @param filter - фильтр, может быть null
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @param threshold - часть (дробь меньше 1) от рейтинга первого места поиска, ниже которой результаты поиска начинают отбрасыватся (для релевантности)
	 * @param parents - (необязательный параметр) все предки загруженных айтемов (ID => массив PARENT_ID)
	 * @return
	 * @throws IOException
	 */
	public static Long[] getItems(Query query, Filter filter, int maxResults, float threshold, HashMap<Long, Long[]> parents) throws IOException {
		return getSingleton().getItemsInt(Collections.singletonList(query), filter, maxResults, threshold, parents);
	}
	/**
	 * Найти ID айтемов, которые соответствуют нескольким запросам Lucene
	 * @param queries
	 * @param filter - фильтр, может быть null
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @param threshold - часть (дробь меньше 1) от рейтинга первого места поиска, ниже которой результаты поиска начинают отбрасыватся (для релевантности)
	 * @param parents - (необязательный параметр) все предки загруженных айтемов (ID => массив PARENT_ID)
	 * @return
	 * @throws IOException
	 */
	public static Long[] getItems(List<Query> queries, Filter filter, int maxResults, float threshold, HashMap<Long, Long[]> parents) throws IOException {
		return getSingleton().getItemsInt(queries, filter, maxResults, threshold, parents);
	}
	/**
	 * Найти ID айтемов, которые соответствуют запросу Lucene
	 * @param query - запрос
	 * @param filter - фильтр, может быть null
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @param threshold - часть (дробь меньше 1) от рейтинга первого места поиска, ниже которой результаты поиска начинают отбрасыватся (для релевантности)
	 * @return
	 * @throws IOException
	 */
	public static Long[] getItems(Query query, Filter filter, int maxResults, float threshold) throws IOException {
		return getSingleton().getItemsInt(Collections.singletonList(query), filter, maxResults, threshold, null);
	}
	/**
	 * Найти ID айтемов, которые соответствуют нескольким запросам Lucene
	 * @param queries
	 * @param filter - фильтр, может быть null
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @param threshold - часть (дробь меньше 1) от рейтинга первого места поиска, ниже которой результаты поиска начинают отбрасыватся (для релевантности)
	 * @return
	 * @throws IOException
	 */
	public static Long[] getItems(List<Query> queries, Filter filter, int maxResults, float threshold) throws IOException {
		return getSingleton().getItemsInt(queries, filter, maxResults, threshold, null);
	}
	/**
	 * Найти ID айтемов, которые соответствуют запросу Lucene
	 * @param query - запрос
	 * @param maxResults - максимальное число возвращаемых результатов
	 * @return
	 * @throws IOException
	 */
	public static Long[] getItems(Query query, int maxResults) throws IOException {
		return getItems(query, null, maxResults, -1);
	}
	/**
	 * Произвести переиндексацию всех айтемов
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public static boolean reindexAll() throws IOException, Exception {
		return getSingleton().reindexInt();
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
		LinkedHashSet<String> result = new LinkedHashSet<String>();
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
	public static int getCountProcessed() throws IOException {
		return getSingleton().countProcessed;
	}
}
