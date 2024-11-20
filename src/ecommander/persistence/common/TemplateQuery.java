package ecommander.persistence.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.persistence.mappers.DBConstants;
/**
 * Последовательность частей запроса
 * Представляет как весь запрос с поддержкой шаблонов, так и его часть.
 * Как раз такие части и называются шаблонами. Они имеют название, и свою последовательность
 * 
 * TODO <enhance>
 * Шаблоны могут быть двух видов: локальные и глобальные. Локальные шаблоны доступны только из своего родительского шаблона
 * Глобальные шаблоны доступны из любого шаблона связанного набора шаблонов.
 * 
 * При добавлении шаблона или создании шаблона из строки локальным считается шаблон, если его имя заключено в <<>>
 * Глобальным считается шаблон, имя которого заключено в {{}}. Например {{PARENT_CRIT}} - глобальный, <<TABLE_LINK>> - локальный
 * 
 * @author EEEE
 *
 */
public class TemplateQuery implements QueryPart {
	private String name; // название шаблона, например, <<PREDECESSOR_ID>>
	private HashMap<String, TemplateQuery> subqueries; // Шаблоны по именам
	private ArrayList<QueryPart> queryParts; // Все части запроса, в том числе шаблоны (которые также есть и в templates)
	
	private TemplateQuery(TemplateQuery prototype) {
		this(prototype.name);
		for (String key : prototype.subqueries.keySet()) {
			subqueries.put(key, new TemplateQuery(prototype.subqueries.get(key)));
		}
		for (QueryPart part : prototype.queryParts) {
			if (part instanceof TemplateQuery) {
				queryParts.add(subqueries.get(((TemplateQuery)part).name));
			} else {
				QueryPart partClone = part.createClone();
				queryParts.add(partClone);
			}
		}
	}
	
	public TemplateQuery(String name) {
		this.name = name;
		queryParts = new ArrayList<QueryPart>();
		subqueries = new HashMap<String, TemplateQuery>();
	}
	/**
	 * !!! Вызывается автоматически !!!
	 * Можно использовать для того, чтобы шаблон записать в строку
	 */
	public void appendForPrepared(StringBuilder sql) {
		for (QueryPart part : queryParts) {
			part.appendForPrepared(sql);
		}
	}
	/**
	 * !!! Вызывается автоматически !!!
	 * Можно вызывать и просто вместо getSimpleSql()
	 */
	public void appendSimple(StringBuilder sql) {
		for (QueryPart part : queryParts) {
			part.appendSimple(sql);
		}
	}
	/**
	 * !!! Вызывается автоматически. Не использовать !!!
	 */
	public int setPrepared(PreparedStatement pstmt, int startIndex) throws SQLException {
		for (QueryPart part : queryParts) {
			startIndex = part.setPrepared(pstmt, startIndex);
		}
		return startIndex;
	}
	/**
	 * Получить подзапрос с заданным идентификатором.
	 * Если запрос изначально создан правильно, то всегда возаращается подзапрос, готовый к работе.
	 * Если возвращается null, значит данный запрос создан с ошибкой
	 * @param name
	 * @return
	 */
	public final TemplateQuery getSubquery(String name) {
		return subqueries.get(name);
	}
	/**
	 * Получить подзапрос с заданным идентификатором.
	 * В случае если такого запроса нет, он создается и возвращается
	 * @param name
	 * @return
	 */
	public final TemplateQuery getOrCreateSubquery(String name) {
		TemplateQuery query = subqueries.get(name);
		if (query == null) {
			query = new TemplateQuery(name);
			subqueries.put(name, query);
		}
		return query;
	}
	
	public final TemplateQuery sql(String sql) {
		queryParts.add(new SqlQueryPart(sql));
		return this;
	}
	/**
	 * Добавляет пустой плейсхолдер для подзапроса
	 * @param name
	 * @return
	 */
	public final TemplateQuery subquery(String name) {
		TemplateQuery template = subqueries.get(name);
		if (template == null) {
			template = new TemplateQuery(name);
			subqueries.put(name, template);
		}
		queryParts.add(template);
		return this;
	}
	/**
	 * Создает значение подзапроса на базе переданного шаблона
	 * @param template
	 * @return
	 */
	public final TemplateQuery createFromTemplate(String template) {
		TemplateQuery newSubquery = createFromString(template, name);
		replace(newSubquery);
		return newSubquery;
	}
	/**
	 * Полностью замещает текущий запрос другим запросом.
	 * Нужно для того, чтобы исходный объект оставался тем же, а содержимое полностью менялось
	 * @param query
	 */
	public final void replace(TemplateQuery query) {
		this.name = query.name;
		this.queryParts = query.queryParts;
		this.subqueries = query.subqueries;
	}

	public final boolean isEmpty() {
		return queryParts.size() == 0;
	}
	
	public final TemplateQuery setByte(byte value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery setInt(int value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery setLong(long value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery setDouble(double value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery setString(String value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery setStringArray(String[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}
	
	public final TemplateQuery setLongArray(Long[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}
	
	public final TemplateQuery setDoubleArray(Double[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}
	
	public final TemplateQuery setIntArray(Integer[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}
	
	public final PreparedStatement prepareQuery(Connection conn, boolean returnAutoGenerated) throws SQLException {
		StringBuilder query = new StringBuilder();
		appendForPrepared(query);
		ServerLogger.debug(toString() + "\n" + query);
		PreparedStatement pstmt = null;
		if (returnAutoGenerated)
			pstmt = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);
		else
			pstmt = conn.prepareStatement(query.toString());
		setPrepared(pstmt, 1);
		return pstmt;
	}

	public final PreparedStatement prepareQuery(Connection conn) throws SQLException {
		return prepareQuery(conn, false);
	}
	/**
	 * Вернуть строку в виде обычного SQL запроса (не PreparedStatement)
	 * @return
	 */
	public final String getSimpleSql() {
		StringBuilder sql = new StringBuilder();
		appendSimple(sql);
		return sql.toString();
	}
	
	public QueryPart createClone() {
		return new TemplateQuery(this);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(name + ": ");
		for (QueryPart part : queryParts) {
			if (part instanceof TemplateQuery)
				sb.append(((TemplateQuery)part).name);
			else
				sb.append(part.toString());
		}
		for (TemplateQuery subquery : subqueries.values()) {
			sb.append("\n").append(subquery.toString());
		}
		return sb.toString();
	}
	/**
	 * Создает запрос из строки. Каждую подстроку строки, заключенную в символы << и >> включает в запрос в виде шаблона
	 * Все остальные части строки включает в виде SQL частей
	 * @param template
	 * @return
	 */
	public static TemplateQuery createFromString(String template, String queryName) {
		TemplateQuery query = new TemplateQuery(queryName);
		String templateBuf = template;
		int start = templateBuf.indexOf("<<");
		while (start >= 0) {
			int end = templateBuf.indexOf(">>") + 2; // + 2 это длина строки ">>"
			String key = templateBuf.substring(start, end);
			String sql = templateBuf.substring(0, start);
			templateBuf = templateBuf.substring(end);
			if (!StringUtils.isBlank(sql))
				query.sql(sql);
			query.subquery(key);
			start = templateBuf.indexOf("<<");
		}
		if (!StringUtils.isBlank(templateBuf))
			query.sql(templateBuf);
		return query;
	}
	
	public static void main(String[] args) {
		
		// Тест 1
		
//		TemplateQuery query = new TemplateQuery("<MAIN>");
//		query.sql("SELECT * FROM ").subquery("<PARENT>").sql(" WHERE ").setInt(10).subquery("<CRIT>").sql(" LIMIT ").setLong(1000);
//		query.getSubquery("<PARENT>").sql(" inner select ").subquery("<TABLE>").sql(" inner sort ").setString("STR_VAL").subquery("<SUBLIMIT>");
//		TemplateQuery sub = query.getSubquery("<PARENT>").getSubquery("<TABLE>");
//		sub.sql(" INNER INNER SELECT ");
//		sub = query.getSubquery("<PARENT>").getSubquery("<SUBLIMIT>");
//		sub.sql(" SUB SUBLIMIT ").setLongArray(new Long []{(long)5, (long)6, (long)7});
//		System.out.println(query);
//		System.out.println();
//		System.out.println();
//		StringBuilder sb = new StringBuilder();
//		query.appendSql(sb);
//		System.out.println(sb);
		
		// Тест 2
		
		String QUERY_SKELETON 
			= "SELECT ITEM.*<<PREDECESSOR_ID>>"
			+ " FROM " + DBConstants.Item.TABLE + " AS ITEM <<JOIN>>"
			+ " WHERE ITEM." + DBConstants.Item.TYPE_ID + " IN <<POLYMORPHIC_TYPE_IDS>>"
			+ " <<PARENT_CONDITION>> <<USER_CONDITION>> <<FILTER_CONDITION>> "
			+ " ORDER BY <<SORTING>> ITEM." + DBConstants.Item.INDEX_WEIGHT
			+ " <<LIMIT>>";
		TemplateQuery query = createFromString(QUERY_SKELETON, "main");
		System.out.println(query);
		System.out.println();
		System.out.println();
		StringBuilder sb = new StringBuilder();
		query.appendForPrepared(sb);
		System.out.println(sb);
		
	}
}
