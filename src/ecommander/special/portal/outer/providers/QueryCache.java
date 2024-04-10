package ecommander.special.portal.outer.providers;

import ecommander.fwk.MysqlConnector;
import ecommander.persistence.common.TemplateQuery;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryCache {
    private static final String TABLE = "special.query_cache";
    private static final String QUERY = "qc_query";
    private static final String XML = "qc_xml";
    private static final String IS_EXACT = "qc_is_exact";
    private static final String CREATED = "qc_created";
    private static final String UPDATED = "qc_updated";

    private static int CACHE_LIVE_HOURS = 24;

    public static class Cached {
        private String query;
        private String xml;
        private boolean isExact;
        private long created;
        private long updated;

        public Cached(String query, String xml, boolean isExact) {
            this.query = query;
            this.xml = xml;
            this.isExact = isExact;
        }

        public Cached(ResultSet rs) throws SQLException {
            this.query = rs.getString(QUERY);
            this.xml = rs.getString(XML);
            this.isExact = rs.getBoolean(IS_EXACT);
            this.created = rs.getLong(CREATED);
            this.updated = rs.getLong(UPDATED);
        }

        public String getQuery() {
            return query;
        }

        public String getXml() {
            return xml;
        }

        public boolean isExact() {
            return isExact;
        }

        public long getCreated() {
            return created;
        }

        public long getUpdated() {
            return updated;
        }
    }

    /**
     * Загрузить кеш с учетом времени хранения (не более 12 или 24 часов)
     * Если кеш не найден или он старый, возвращается null
     * @param query
     * @param isExact
     * @return
     * @throws Exception
     */
    public static Cached getCache(String query, boolean isExact) throws Exception {
        TemplateQuery select = new TemplateQuery("select query");
        DateTime oldestCreated = DateTime.now(DateTimeZone.UTC).minusHours(CACHE_LIVE_HOURS);
        select.SELECT("*").FROM(TABLE)
                .WHERE().col(QUERY).string(query)
                .AND().col(IS_EXACT).byte_(isExact ? (byte)1 : (byte)0);
        Cached cachedCache = null;
        try (Connection conn = MysqlConnector.getConnection();
             PreparedStatement pstmt = select.prepareQuery(conn)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                cachedCache = new Cached(rs);
            }
        }
        if (cachedCache == null || cachedCache.getCreated() < oldestCreated.getMillis()) {
            return null;
        }
        return cachedCache;
    }

    /**
     * Сохраняет новый кеш (или перезаписывает существующий, но на более актуальный)
     * @param query
     * @param xml
     * @param isExact
     * @throws SQLException
     * @throws NamingException
     */
    public static void saveNewCache(String query, String xml, boolean isExact) throws SQLException, NamingException {
        TemplateQuery insert = new TemplateQuery("insert");
        long nowMillis = DateTime.now(DateTimeZone.UTC).getMillis();
        insert.INSERT_INTO(TABLE, QUERY, XML, IS_EXACT, CREATED, UPDATED)
                .sql(" VALUES (")
                .string(query).sql(", ")
                .byte_(isExact ? (byte)1 : (byte)0).sql(", ")
                .string(xml).sql(", ")
                .long_(nowMillis).sql(", ")
                .long_(0)
                .sql(") ON DUPLICATE KEY UPDATE ")
                .col(XML).string(xml).sql(", ")
                .col(CREATED).long_(nowMillis).sql(", ")
                .col(UPDATED).long_(0);
        try (PreparedStatement pstmt = insert.prepareQuery(MysqlConnector.getConnection())) {
            pstmt.executeUpdate();
        }
    }

    /**
     * Количество обновляется только в запросах с полным совпадением
     * @param query
     * @param xml
     */
    public static void updateCacheQty(String query, String xml) throws SQLException, NamingException {
        TemplateQuery insert = new TemplateQuery("insert");
        long nowMillis = DateTime.now(DateTimeZone.UTC).getMillis();
        insert.INSERT_INTO(TABLE, QUERY, XML, IS_EXACT, CREATED, UPDATED)
                .sql(" VALUES (")
                .string(query).sql(", ")
                .byte_((byte)1).sql(", ")
                .string(xml).sql(", ")
                .long_(nowMillis).sql(", ")
                .long_(nowMillis)
                .sql(") ON DUPLICATE KEY UPDATE ")
                .col(XML).string(xml).sql(", ")
                .col(UPDATED).long_(nowMillis);
        try (PreparedStatement pstmt = insert.prepareQuery(MysqlConnector.getConnection())) {
            pstmt.executeUpdate();
        }
    }

    public static void setCacheLiveHours(int hours) {
        CACHE_LIVE_HOURS = hours;
    }
}
