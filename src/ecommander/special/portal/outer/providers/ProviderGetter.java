package ecommander.special.portal.outer.providers;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.Request;
import extra.CurrencyRates;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;

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
     * Получить данные от провайдера и добавить в структуру XML, переданную в качестве параметра
     * В этом методе выполняется подключение к серверу, этот метод блокирует поток надолго
     * @param userInput - все данные от пользователя (запросы, фильтры и т.д.)
     * @param rates
     * @return
     */
    public abstract Result getData(UserInput userInput, CurrencyRates rates);

    /**
     * Получить коэффициент для поставщика.
     * Кеш коэффициентов не хранится в экземпляре чтобы не было вечного кеша, т.к. пришлось бы
     * при изменении коэффициента перезагружать сервер
     * TODO сделать кеш, подобный кешу айтемов ItemCache, но для любых объектов
     * @param distributorName
     * @param distributorQuotients
     * @return
     */
    protected BigDecimal getDistributorQuotient(String distributorName, HashMap<String, BigDecimal> distributorQuotients) {
        BigDecimal extraQuotient = distributorQuotients.get(distributorName); // дополнительный коэффициент для цены для поставщика
        if (extraQuotient == null) {
            Item catalogSettings = null;
            try {
                catalogSettings = ItemCache.get(distributorName,
                        () -> new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(ItemNames.price_catalog_.NAME, distributorName).loadFirstItem());
            } catch (Exception e) {
                ServerLogger.error("Unable to load price catalog '" + distributorName + "'", e);
            }
            if (catalogSettings != null) {
                extraQuotient = catalogSettings.getDecimalValue(ItemNames.price_catalog_.QUOTIENT, BigDecimal.ONE);
            } else {
                extraQuotient = BigDecimal.ONE;
            }
            distributorQuotients.put(distributorName, extraQuotient);
        }
        return extraQuotient;
    }


    protected void addServerElement(XmlDocumentBuilder xml, Request.Query query) {
        xml.addElement("server", null, "host", getProviderName(), "millis", query.getProcessMillis(),
                "tries", query.getNumTries(), "proxies", StringUtils.join(query.getProxyTries(), " "));
    }
}
