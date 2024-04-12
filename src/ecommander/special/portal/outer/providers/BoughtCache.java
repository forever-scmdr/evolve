package ecommander.special.portal.outer.providers;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.Pair;
import ecommander.fwk.Triple;
import ecommander.persistence.common.TemplateQuery;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Список заказанных товаров. Для каждого товара (который из корзины отправился в заказ) хранится
 * общее количество заказанных единиц всеми пользователями. Потом это количество отнимается от
 * количества, полученного с сайтов-провайдеров информации
 */
public class BoughtCache {
    private static final String TABLE = "special.order_cache";
    private static final String CODE_PROVIDER = "oc_code_provider";
    private static final String CODE = "oc_code";
    private static final String PROVIDER = "oc_provider";
    private static final String QTY_ORDERED = "oc_ordered_qty";
    private static final String UPDATED = "oc_updated";

    private static int CACHE_LIVE_HOURS = 48;

    public static class Cached {

        private String codeProvider;
        private String code;
        private String provider;
        private int qtyOrdered;
        private long updated;


        public Cached(ResultSet rs) throws SQLException {
            this.codeProvider = rs.getString(CODE_PROVIDER);
            this.code = rs.getString(CODE);
            this.provider = rs.getString(PROVIDER);
            this.qtyOrdered = rs.getInt(QTY_ORDERED);
            this.updated = rs.getLong(UPDATED);
        }

        public String getCodeProvider() {
            return codeProvider;
        }

        public String getCode() {
            return code;
        }

        public String getProvider() {
            return provider;
        }

        public int getQtyOrdered() {
            return qtyOrdered;
        }

        public long getUpdated() {
            return updated;
        }
    }

    /**
     * Загрузить кеш с учетом времени хранения (не более 12 или 24 часов)
     * Если кеш не найден или он старый, возвращается null
     * @param productCode
     * @param provider
     * @return
     * @throws Exception
     */
    public static Cached getCache(String productCode, String provider) throws Exception {
        ArrayList<Pair<String, String>> codeProviders = new ArrayList<>();
        codeProviders.add(new Pair<>(productCode, provider));
        ArrayList<Cached> cached = getCache(codeProviders);
        if (cached.size() == 0)
            return null;
        return cached.get(0);
    }

    /**
     * Загрузить кеш с учетом времени хранения (не более 12 или 24 часов)
     * Если кеш не найден или он старый, возвращается null
     * @param productCodeProviders
     * @return
     * @throws Exception
     */
    public static ArrayList<Cached> getCache(Collection<Pair<String, String>> productCodeProviders) throws Exception {
        TemplateQuery select = new TemplateQuery("select cache");
        DateTime oldestCreated = DateTime.now(DateTimeZone.UTC).minusHours(CACHE_LIVE_HOURS);
        ArrayList<String> codeProviders = new ArrayList<>();
        for (Pair<String, String> productCodeProvider : productCodeProviders) {
            codeProviders.add(createCodeProvider(productCodeProvider.getLeft(), productCodeProvider.getRight()));
        }
        select.SELECT("*").FROM(TABLE).WHERE().col_IN(CODE_PROVIDER).stringIN(codeProviders.toArray(new String[0]));
        ArrayList<Cached> cached = new ArrayList<>();
        try (Connection conn = MysqlConnector.getConnection();
             PreparedStatement pstmt = select.prepareQuery(conn)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                cached.add(new Cached(rs));
            }
        }
        // Удалить кеш по истечении времени
        ArrayList<String> toDelete = new ArrayList<>();
        for (Cached c : cached) {
            if (c.getUpdated() < oldestCreated.getMillis()) {
                toDelete.add(c.codeProvider);
            }
        }
        if (toDelete.size() > 0) {
            TemplateQuery delete = new TemplateQuery("delete cache");
            delete.DELETE(TABLE).WHERE().col_IN(CODE_PROVIDER).stringIN(toDelete.toArray(new String[0]));
            try (PreparedStatement pstmt = delete.prepareQuery(MysqlConnector.getConnection())) {
                pstmt.executeUpdate();
            }
        }
        return cached;
    }

    /**
     * Сохраняет новый кеш (или перезаписывает существующий, но на более актуальный)
     * @param productCode
     * @param provider
     * @param extraQtyOrdered
     * @throws SQLException
     * @throws NamingException
     */
    public static void doCache(String productCode, String provider, int extraQtyOrdered) throws SQLException, NamingException {
        ArrayList<Triple<String, String, Integer>> codeProviderQtys = new ArrayList<>();
        codeProviderQtys.add(new Triple<>(productCode, provider, extraQtyOrdered));
        doCache(codeProviderQtys);
    }

    /**
     * Сохраняет новый кеш (или перезаписывает существующий, но на более актуальный)
     * @param codeProviderQtys
     * @throws SQLException
     * @throws NamingException
     */
    public static void doCache(ArrayList<Triple<String, String, Integer>> codeProviderQtys) throws SQLException, NamingException {
        if (codeProviderQtys.size() == 0)
            return;
        TemplateQuery insert = new TemplateQuery("insert");
        long nowMillis = DateTime.now(DateTimeZone.UTC).getMillis();
        insert.INSERT_INTO(TABLE, CODE_PROVIDER, CODE, PROVIDER, QTY_ORDERED, UPDATED)
                .sql(" VALUES ");
        boolean notFirst = false;
        for (Triple<String, String, Integer> codeProviderQty : codeProviderQtys) {
            String codeProvider = createCodeProvider(codeProviderQty.getLeft(), codeProviderQty.getMedium());
            if (notFirst) {
                insert.sql(", ");
            }
            insert.sql("(").string(codeProvider).sql(", ")
                    .string(codeProviderQty.getLeft()).sql(", ")
                    .string(codeProviderQty.getMedium()).sql(", ")
                    .int_(codeProviderQty.getRight()).sql(", ")
                    .long_(nowMillis).sql(")");

        }
        insert.sql(" ON DUPLICATE KEY UPDATE ")
                .col(QTY_ORDERED).sql(QTY_ORDERED + "+VALUES(" + QTY_ORDERED + "), ").col(UPDATED).long_(nowMillis);
        try (PreparedStatement pstmt = insert.prepareQuery(MysqlConnector.getConnection())) {
            pstmt.executeUpdate();
        }
    }

    public static void setCacheLiveHours(int hours) {
        CACHE_LIVE_HOURS = hours;
    }

    private static String createCodeProvider(String code, String provider) {
        return code + "@#@" + provider;
    }
}
