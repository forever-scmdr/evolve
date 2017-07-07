package ecommander.persistence.common;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.ServerLogger;
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

	/**
	 *
	 * @param name
	 * @return
	 */
	public final TemplateQuery removeSubquery(String name) {
		TemplateQuery toRemove = subqueries.get(name);
		if (toRemove != null) {
			subqueries.remove(name);
			queryParts.remove(toRemove);
		}
		return this;
	}

	public final TemplateQuery sql(String sql) {
		queryParts.add(new SqlQueryPart(sql));
		return this;
	}

	/**
	 * Создать часть SELECT <поле1, поле2, ...>
	 * @param columns
	 * @return
	 */
	public final TemplateQuery SELECT(Object... columns) {
		queryParts.add(new SqlQueryPart("SELECT " + StringUtils.join(columns, ", ")));
		return this;
	}

	/**
	 * Создать вызов встроенной функции (функции агрегации или простой функции)
	 * @param function
	 * @param args
	 * @return
	 */
	public final TemplateQuery FUNC(String function, String... args) {
		queryParts.add(new SqlQueryPart(" " + function + "(" + StringUtils.join(args, ", ") + ")"));
		return this;
	}

	/**
	 * Создать директиву FROM
	 * @param tableName
	 * @return
	 */
	public final TemplateQuery FROM(String...tableNames) {
		queryParts.add(new SqlQueryPart(" FROM " + StringUtils.join(tableNames, ", ")));
		return this;
	}

	public final TemplateQuery WHERE() {
		queryParts.add(new SqlQueryPart(" WHERE "));
		return this;
	}

	public final TemplateQuery AND() {
		queryParts.add(new SqlQueryPart(" AND "));
		return this;
	}

	public final TemplateQuery UNION_ALL() {
		queryParts.add(new SqlQueryPart(" UNION ALL "));
		return this;
	}

	public final TemplateQuery com() {
		queryParts.add(new SqlQueryPart(", "));
		return this;
	}

	public final TemplateQuery col(String column, String...sign) {
		queryParts.add(new SqlQueryPart(" " + column + (sign.length > 0 ? sign[0] : "=")));
		return this;
	}

	/**
	 * Аналогично col только с идущей спереди запятой (для второго и последующих значений в списке колонок)
	 * @param column
	 * @param sign
	 * @return
	 */
	public final TemplateQuery _col(String column, String...sign) {
		queryParts.add(new SqlQueryPart(", " + column + (sign.length > 0 ? sign[0] : "=")));
		return this;
	}

	public final TemplateQuery INSERT_INTO(String tableName, String...colNames) {
		StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName);
		if (colNames.length > 0) {
			sql.append(" (").append(StringUtils.join(colNames, ", ")).append(") ");
		}
		queryParts.add(new SqlQueryPart(sql.toString()));
		return this;
	}

	public final TemplateQuery DELETE_FROM_WHERE(String tableName) {
		queryParts.add(new SqlQueryPart("DELETE FROM " + tableName + " WHERE "));
		return this;
	}

	public final TemplateQuery UPDATE(String tableName) {
		queryParts.add(new SqlQueryPart("UPDATE " + tableName));
		return this;
	}

	public final TemplateQuery DELETE(String tableName) {
		queryParts.add(new SqlQueryPart("DELETE FROM " + tableName));
		return this;
	}

	public final TemplateQuery SET() {
		queryParts.add(new SqlQueryPart(" SET "));
		return this;
	}

	public final TemplateQuery ORDER_BY(String...colNames) {
		queryParts.add(new SqlQueryPart(" ORDER BY " + StringUtils.join(colNames, ',')));
		return this;
	}

	public final TemplateQuery LIMIT(int limit, int...startFrom) {
		StringBuilder sb = new StringBuilder(" LIMIT ");
		if (startFrom.length > 0)
			sb.append(startFrom[0]).append(',');
		sb.append(limit);
		return this;
	}

	public final TemplateQuery ON_DUPLICATE_KEY_UPDATE(String colName) {
		queryParts.add(new SqlQueryPart(" ON DUPLICATE KEY UPDATE " + colName + "="));
		return this;
	}
	/**
	 * Создать соединение с другой таблицей. Колонки, по которым происходит соединение,
	 * перечисляются парами, т. е. Т1.Кол1, Т2.Кол1, Т1.Кол2, Т2.Кол2 и т.д.
	 * @param tableName
	 * @param columnPairs
	 * @return
	 */
	public final TemplateQuery INNER_JOIN(String tableName, String... columnPairs) {
		if (columnPairs.length % 2 != 0 || columnPairs.length == 0)
			throw new IllegalArgumentException("There must be pairs of columns as JOIN conditions");
		StringBuilder sb = new StringBuilder(" INNER JOIN ");
		sb.append(tableName).append(" ON ");
		for (int i = 0; i < columnPairs.length; i += 2) {
			if (i > 0)
				sb.append(" AND ");
			sb.append(columnPairs[i]).append("=").append(columnPairs[i + 1]);
		}
		queryParts.add(new SqlQueryPart(sb));
		return this;
	}
	/**
	 * Добавляет пустой плейсхолдер для подзапроса
	 * @param name
	 * @return
	 */
	public final TemplateQuery subquery(String name) {
		queryParts.add(getOrCreateSubquery(name));
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
	public final TemplateQuery replace(TemplateQuery query) {
		this.name = query.name;
		this.queryParts = query.queryParts;
		this.subqueries = query.subqueries;
		return this;
	}

	public final boolean isEmpty() {
		return queryParts.size() == 0;
	}
	
	public final TemplateQuery byte_(byte value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery int_(int value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery long_(long value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery double_(double value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}

	public final TemplateQuery decimal(BigDecimal value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery string(String value) {
		queryParts.add(new ValueQueryPart(value));
		return this;
	}
	
	public final TemplateQuery stringArray(String[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}
	
	public final TemplateQuery longArray(Long[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}
	
	public final TemplateQuery doubleArray(Double[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}

	public final TemplateQuery decimalArray(BigDecimal[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}

	public final TemplateQuery intArray(Integer[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}

	public final TemplateQuery byteArray(Byte[] array) {
		queryParts.add(new ValueArrayQueryPart(array));
		return this;
	}

	public final TemplateQuery stringIN(String... values) {
		queryParts.add(new SqlQueryPart("("));
		queryParts.add(new ValueArrayQueryPart(values));
		queryParts.add(new SqlQueryPart(")"));
		return this;
	}

	public final TemplateQuery longIN(Long... values) {
		queryParts.add(new SqlQueryPart("("));
		queryParts.add(new ValueArrayQueryPart(values));
		queryParts.add(new SqlQueryPart(")"));
		return this;
	}

	public final TemplateQuery doubleIN(Double... values) {
		queryParts.add(new SqlQueryPart("("));
		queryParts.add(new ValueArrayQueryPart(values));
		queryParts.add(new SqlQueryPart(")"));
		return this;
	}

	public final TemplateQuery decimalIN(BigDecimal... values) {
		queryParts.add(new SqlQueryPart("("));
		queryParts.add(new ValueArrayQueryPart(values));
		queryParts.add(new SqlQueryPart(")"));
		return this;
	}

	public final TemplateQuery intIN(Integer... values) {
		queryParts.add(new SqlQueryPart("("));
		queryParts.add(new ValueArrayQueryPart(values));
		queryParts.add(new SqlQueryPart(")"));
		return this;
	}

	public final TemplateQuery byteArrayIN(Byte... values) {
		queryParts.add(new SqlQueryPart("("));
		queryParts.add(new ValueArrayQueryPart(values));
		queryParts.add(new SqlQueryPart(")"));
		return this;
	}

	public final PreparedStatement prepareQuery(Connection conn, boolean returnAutoGenerated) throws SQLException {
		StringBuilder query = new StringBuilder();
		appendForPrepared(query);
		ServerLogger.debug(toString());
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
		return getSimpleSql();
	}

	/**
	 * Развернутая запись запроса (все подзапросы и значения перечислены отдельно)
	 * @return
	 */
	public String explain() {
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
//		query.sql("SELECT * FROM ").subquery("<ANCESTOR>").sql(" WHERE ").int_(10).subquery("<CRIT>").sql(" LIMIT ").long_(1000);
//		query.getSubquery("<ANCESTOR>").sql(" inner select ").subquery("<TABLE>").sql(" inner sort ").string("STR_VAL").subquery("<SUBLIMIT>");
//		TemplateQuery sub = query.getSubquery("<ANCESTOR>").getSubquery("<TABLE>");
//		sub.sql(" INNER INNER SELECT ");
//		sub = query.getSubquery("<ANCESTOR>").getSubquery("<SUBLIMIT>");
//		sub.sql(" SUB SUBLIMIT ").longArray(new Long []{(long)5, (long)6, (long)7});
//		System.out.println(query);
//		System.out.println();
//		System.out.println();
//		StringBuilder sb = new StringBuilder();
//		query.appendSql(sb);
//		System.out.println(sb);
		
		// Тест 2
		
		String QUERY_SKELETON 
			= "SELECT CHILD.*<<PREDECESSOR_ID>>"
			+ " FROM " + DBConstants.ItemTbl.ITEM_TBL + " AS CHILD <<JOIN>>"
			+ " WHERE CHILD." + DBConstants.ItemTbl.I_SUPERTYPE + " IN <<POLYMORPHIC_TYPE_IDS>>"
			+ " <<PARENT_CONDITION>> <<USER_CONDITION>> <<FILTER_CONDITION>> "
			+ " ORDER BY <<SORTING>> CHILD." + DBConstants.ItemTbl.I_KEY
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
