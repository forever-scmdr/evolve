package ecommander.special.portal.outer;

import ecommander.fwk.OkWebClient;
import ecommander.fwk.Strings;
import ecommander.special.portal.outer.providers.Providers;
import kotlin.NotImplementedError;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Класс, который выполняет запрос к определенному серверу
 * От сервера зависит вид строки http запроса и формат возвращаемого результата
 *
 * Этот класс должен использоваться только классом Proxy при добавлении задания
 * на запрос в пул потоков
 */
public class QueryExecutor {

    public static abstract class Executor {
        protected String query;
        protected String proxyServer;
        protected String resultMimeType;
        protected byte[] result;

        private Executor(String query, String resultMimeType, String proxyServer) {
            this.query = StringUtils.normalizeSpace(query);
            this.proxyServer = proxyServer;
            this.resultMimeType = resultMimeType;
        }
        /**
         * Фактически выполнить запрос на сервер.
         * Составить строку запроса и выполнить с помощью клиента
         * Результат сохраняется в поле result
         * @return
         */
        public abstract boolean executeQuery() throws Exception;

        public byte[] getResult() {
            return result;
        }
    }

    /**
     * Запрос на findchips.com
     */
    private static class FindchipsExecutor extends Executor {

        public static final String SERVER = "https://www.findchips.com/search/";

        private FindchipsExecutor(String query, String proxyServer) {
            super(query, "text/html", proxyServer);
        }

        @Override
        public boolean executeQuery() throws Exception {
            boolean hasProxy = false;
            try {
                if (StringUtils.isNotBlank(query)) {
                    query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
                    if (StringUtils.isNotBlank(proxyServer)) {
                        String requestUrl = SERVER + query;
                        if (StringUtils.isNotBlank(proxyServer) && StringUtils.startsWith(proxyServer, "http")) {
                            hasProxy = true;
                            String proxyUrl = proxyServer + "?url=" + requestUrl;
                            result = OkWebClient.getInstance().getBytes(proxyUrl);
                        } else {
                            result = OkWebClient.getInstance().getBytes(requestUrl);
                        }
                    }
                    // если задан параметр server - значит надо подулючаться удаленному серверу
                    else {
                        result = ("Не указан прокси сервер для урла " + SERVER).getBytes();
                        return false;
                    }
                } else {
                    result = ("Неверный формат запроса для урла " + SERVER + ". Запрос '" + query + "'").getBytes();
                    return false;
                }
            } catch (Exception e) {
                result = ExceptionUtils.getStackTrace(e).getBytes();
                if (!hasProxy) {
                    result = (Strings.ERROR_MARK + "\n\n" + new String(result, StandardCharsets.UTF_8)).getBytes();
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Запрос на oemsecretsapi.com
     */
    private static class OemsecretsExecutor extends Executor {

        private static final String USD = "USD";
        private static final String URL_WITH_KEY
                = "https://oemsecretsapi.com/partsearch?apiKey=5yddaj3l7y9m6bvolfwu2bycbqxylktaqj3gugtqx4kmsat2hprit7cubn3ge7m1&searchTerm={Q}&currency={CUR}";

        private OemsecretsExecutor(String query, String proxyServer) {
            super(query, "text/html", proxyServer);
        }

        @Override
        public boolean executeQuery() throws Exception {
            String curCode = USD; // временно, возможно надо на постоянно оставить
            boolean hasProxy = false;
            try {
                if (StringUtils.isNotBlank(query)) {
                    query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
                    String requestUrl = StringUtils.replace(URL_WITH_KEY, "{Q}", query);
                    requestUrl = StringUtils.replace(requestUrl, "{CUR}", curCode);
                    if (StringUtils.isNotBlank(proxyServer) && StringUtils.startsWith(proxyServer, "http")) {
                        hasProxy = true;
                        String proxyUrl = proxyServer + "?url=" + URLEncoder.encode(requestUrl, Strings.SYSTEM_ENCODING);
                        result = OkWebClient.getInstance().getBytes(proxyUrl);
                    } else {
                        result = OkWebClient.getInstance().getBytes(requestUrl);
                    }
                } else {
                    result = ("Неверный формат запроса").getBytes();
                    return false;
                }
            } catch (Exception e) {
                result = ExceptionUtils.getStackTrace(e).getBytes();
                if (!hasProxy) {
                    result = (Strings.ERROR_MARK + "\n\n" + new String(result, StandardCharsets.UTF_8)).getBytes();
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Запрос на wxapp.bom.ai (китайский сайтец)
     */
    private static class WxappExecutor extends Executor {

        private static final String USD = "USD";
        private static final String URL_WITH_KEY = "https://wxapp.bom.ai/{COMPANY_CODE}/external/post";
        private static final String COMPANY_CODE = "CYD";
        private static final String CLIENT_ID = "834ddf79-c30e-426b-ab94-c3047191e8ca";
        private static final String CLIENT_SECRET = "834ddf79-c30e-426b-ab94-c3047191e8ca";
        private static final String POST_JSON = "{\n" +
                "    \"method\": \"supplier-inventory-get\",\n" +
                "    \"timestampUtc\": \"{TIMESTAMP_UTC}\",\n" +
                "    \"clientId\": \"{CLIENT_ID}\",\n" +
                "    \"sign\": \"{SIGN}\",\n" +
                "    \"model\": \"{QUERY}\",\n" +
                "    \"pageIndex\": 1,\n" +
                "    \"pageSize\": 100\n" +
                "}";

        private WxappExecutor(String query) {
            super(query, "text/html", null);
        }

        @Override
        public boolean executeQuery() throws Exception {
            String curCode = USD; // временно, возможно надо на постоянно оставить
            try {
                if (StringUtils.isNotBlank(query)) {
                    query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
                    long timestamp = DateTime.now(DateTimeZone.UTC).getMillis();
                    String signSrc = CLIENT_ID + CLIENT_SECRET + timestamp;
                    String hash = DigestUtils.md5Hex(signSrc).toUpperCase();
                    String json = POST_JSON.replace("{TIMESTAMP_UTC}", timestamp + "")
                            .replace("{CLIENT_ID}", CLIENT_ID)
                            .replace("{SIGN}", hash)
                            .replace("{QUERY}", query);
                    String requestUrl = URL_WITH_KEY.replace("{COMPANY_CODE}", COMPANY_CODE);
                    result = OkWebClient.getInstance().postBytesHeaders(requestUrl, json, "application/json; charset=utf-8");
                } else {
                    result = ("Неверный формат запроса").getBytes();
                    return false;
                }
            } catch (Exception e) {
                result = ExceptionUtils.getStackTrace(e).getBytes();
                result = (Strings.ERROR_MARK + "\n\n" + new String(result, StandardCharsets.UTF_8)).getBytes();
                return false;
            }
            return true;
        }
    }



    /**
     * Прямой запрос ко всем другим сайтам. Если их много, все они группируются в один "сервер"
     * с одной на всех очередью
     */
    private static class GeneralServerExecutor extends Executor {

        private GeneralServerExecutor(String query, String resultMimeType, String proxyServer) {
            super(query, resultMimeType, proxyServer);
        }

        @Override
        public boolean executeQuery() throws Exception {
            boolean hasProxy = false;
            try {
                if (StringUtils.isNotBlank(query)) {
                    if (StringUtils.isNotBlank(proxyServer)) {
                        String requestUrl = query;
                        if (StringUtils.startsWith(requestUrl, "//"))
                            requestUrl = "https:" + requestUrl;
                        if (StringUtils.isNotBlank(proxyServer) && StringUtils.startsWith(proxyServer, "http")) {
                            hasProxy = true;
                            String proxyUrl = proxyServer + "?url=" + requestUrl + "&mime_type=" + resultMimeType;
                            result = OkWebClient.getInstance().getBytes(proxyUrl);
                        } else {
                            result = OkWebClient.getInstance().getBytes(requestUrl);
                        }
                    }
                    // если задан параметр server - значит надо подулючаться удаленному серверу
                    else {
                        result = ("Не указан прокси сервер для урла '" + query + "'").getBytes();
                        return false;
                    }
                } else {
                    result = ("Неверный формат запроса для урла '" + query + "'").getBytes();
                    return false;
                }
            } catch (Exception e) {
                result = ExceptionUtils.getStackTrace(e).getBytes();
                if (!hasProxy) {
                    result = (Strings.ERROR_MARK + "\n\n" + new String(result, StandardCharsets.UTF_8)).getBytes();
                }
                return false;
            }
            return true;
        }
    }

    /**
     * Создать исполнитель запроса для определенного хоста (сайта, с которого берется информация)
     * @param hostName
     * @param proxyServer
     * @param query
     * @return
     */
    public static Executor get(String hostName, String proxyServer, String query, String resultMimeType) {
        if (StringUtils.containsIgnoreCase(hostName, Providers.FINDCHIPS)) {
            return new FindchipsExecutor(query, proxyServer);
        }
        if (StringUtils.containsIgnoreCase(hostName, Providers.OEMSECRETS)) {
            return new OemsecretsExecutor(query, proxyServer);
        }
        if (StringUtils.containsIgnoreCase(hostName, Providers.WXAPP)) {
            return new WxappExecutor(query);
        }
        return new GeneralServerExecutor(query, resultMimeType, proxyServer);
    }
}
