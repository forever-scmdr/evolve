package ecommander.special.portal.outer.providers;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.ProxyRequestDispatcher;
import ecommander.special.portal.outer.Request;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс, который подключается к определенному (в реализациях) удаленному серверу, получает с него информацию,
 * преобразует к единому формату и возвращает клиенту
 *
 * Для каждого сервера должен быть свой такой класс. Помимо него должен быть также класс в QueryExecutor, который
 * фактически и осуществляет подключение к серверу с формированием нужной строки запроса и заголовков
 */
public abstract class ProviderGetter implements ItemNames {

    /**
     * Результат выполнения запроса
     */
    class Result {
        private int errorNum;
        private String errorMessage;
        private Request request;

        protected Result(Request request, int errorNum, String errorMessage) {
            this.request = request;
            this.errorNum = errorNum;
            this.errorMessage = errorMessage;
        }

        public Request getRequest() {
            return request;
        }

        public int getErrorNum() {
            return errorNum;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean isSuccess() {
            return errorNum == SUCCESS;
        }

    }

    /**
     * Класс для интервала цен. Сделан только для того, чтобы можно было сортировать по
     * возрастанию (или убыванию) количества товаров в интервале (до какого количества действует цена)
     */
    protected static class PriceBreak {
        protected final int qty;
        protected final String curCode;
        protected final BigDecimal priceOriginal;

        public PriceBreak(int qty, String curCode, BigDecimal priceOriginal) {
            this.qty = qty;
            this.curCode = curCode;
            this.priceOriginal = priceOriginal;
        }
    }

    public int SUCCESS = 0;
    public int REQUEST_ERROR = 1;
    public int CONNECTION_ERROR = 2;
    public int RESPONSE_ERROR = 3;
    public int OTHER_ERROR = 4;

    /**
     * Называние провайдера данных (хоста)
     * Не доменное имя, а название, например, findchips или oemsecrets
     * @return
     */
    public abstract String getProviderName();

    /**
     * Обработать результат, возвращенный сервером, для одного запроса
     * Исходный результат не обработан, находится в виде, переданном сервером, может также быть и ошибочным
     * @param query
     * @param input
     * @return
     */
    protected abstract void processQueryResult(Request.Query query, OuterInputData input) throws Exception;


    /**
     * Получить данные от провайдера и добавить в структуру XML, переданную в качестве параметра
     * В этом методе выполняется подключение к серверу, этот метод блокирует поток надолго
     * @param input
     * @return
     */
    public Result getData(OuterInputData input) throws Exception {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        Request request = ProxyRequestDispatcher.submitRequest(getProviderName(), input.getQueries().keySet());
        Result result;
        try {
            Timer.getTimer().start("API # outer_execution");
            request.awaitExecution();
            Timer.getTimer().stop("API # outer_execution");
            result = new Result(request, SUCCESS, null);
        } catch (Exception e) {
            return new Result(request, CONNECTION_ERROR, ExceptionUtils.getStackTrace(e));
        }
        // Результирующий документ
        for (Request.Query query : request.getAllQueries()) {
            Timer.getTimer().start("API # outer_process");
            processQueryResult(query, input);
            Timer.getTimer().stop("API # outer_process");
        }
        return result;
    }

    /**
     * Получить коэффициент для поставщика.
     * Кеш коэффициентов не хранится в экземпляре чтобы не было вечного кеша, т.к. пришлось бы
     * при изменении коэффициента перезагружать сервер
     * TODO сделать кеш, подобный кешу айтемов ItemCache, но для любых объектов (в т.ч. для кеша файлов)
     * @param distributorName
     * @param input
     * @return
     */
    protected BigDecimal getDistributorQuotient(String distributorName, OuterInputData input) {
        if (!input.hasDistributorQuotients()) {
            final String QUOTIENTS = "catalog_extra_quotients";
            List<Item> catalogsSettings = null;
            try {
                catalogsSettings = ItemCache.getArray(QUOTIENTS, () -> new ItemQuery(PRICE_CATALOG).loadItems());
            } catch (Exception e) {
                ServerLogger.error("Unable to load price catalog '" + distributorName + "'", e);
            }
            HashMap<String, BigDecimal> quotients = new HashMap<>();
            if (catalogsSettings != null) {
                for (Item setting : catalogsSettings) {
                    quotients.put(setting.getStringValue(price_catalog_.NAME), setting.getDecimalValue(price_catalog_.QUOTIENT, BigDecimal.ONE));
                }
            }
            input.setDistributorQuotients(quotients);
        }
        return input.getDistributorQuotient(distributorName);
    }

    /**
     * Добаить элемент <server/> с нужными атрибутами
     * @param xml
     * @param query
     */
    protected void addServerElement(XmlDocumentBuilder xml, Request.Query query) {
        xml.addElement("server", null, "host", getProviderName(), "millis", query.getProcessMillis(),
                "tries", query.getNumTries(), "proxies", StringUtils.join(query.getProxyTries(), " "));
    }
}
