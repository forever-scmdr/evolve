package ecommander.special.portal.outer.providers;

import ecommander.fwk.XmlDocumentBuilder;
import extra.CurrencyRates;
import org.apache.commons.lang3.StringUtils;

/**
 * Класс, который подключается к определенному (в реализациях) удаленному серверу, получает с него информацию,
 * преобразует к единому формату и возвращает клиенту
 *
 * Для каждого сервера должен быть свой такой класс. Помимо него должен быть также класс в QueryExecutor, который
 * фактически и осуществляет подключение к серверу с формированием нужной строки запроса и заголовков
 */
public interface ProviderGetter {

    /**
     * Результат выполнения запроса
     */
    class Result {
        private int errorNum;
        private String errorMessage;
        private XmlDocumentBuilder xml;

        protected Result(int errorNum, String errorMessage, XmlDocumentBuilder xml) {
            this.errorNum = errorNum;
            this.errorMessage = errorMessage;
            this.xml = xml;
        }

        protected Result(int errorNum, String errorMessage) {
            this.errorNum = errorNum;
            this.errorMessage = errorMessage;
        }

        public int getErrorNum() {
            return errorNum;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public XmlDocumentBuilder getXml() {
            return xml;
        }

        public boolean isSuccess() {
            return errorNum == SUCCESS;
        }

        public boolean isNotBlank() {
            return StringUtils.isNotBlank(xml.getXmlStringSB());
        }
    }

    int SUCCESS = 0;
    int REQUEST_ERROR = 1;
    int CONNECTION_ERROR = 2;
    int RESPONSE_ERROR = 3;
    int OTHER_ERROR = 4;

    /**
     * Называние провайдера данных (хоста)
     * Не доменное имя, а название, например, findchips или oemsecrets
     * @return
     */
    String getProviderName();

    /**
     * Получить данные от провайдера и добавить в структуру XML, переданную в качестве параметра
     * В этом методе выполняется подключение к серверу, этот метод блокирует поток надолго
     * @param query -   запрос. Метод выполняет только один запрос (т.к. серверы поддерживают только один запрос в одно обращение)
     * @param userInput -   все фильтры, полученные от пользователя (чтобы не возвращать варианты, которые не подходят)
     * @param rates
     * @return
     */
    Result getData(String query, UserInput userInput, CurrencyRates rates);
}
