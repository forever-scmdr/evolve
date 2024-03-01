package ecommander.special.portal.outer;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Запрос от одного клиента. Одно действие запроса (юзер один раз нажал кнопку "Найти")
 * Содержит один или несколько поисковых запросов
 * Обращается к определенному серверу. Юзер не задает сервер, сервер назначается отдельно, но в
 * запросе он известен
 */
public class Request {

    /**
     * Статусы запроса
     */
    public enum Status {
        NEW,            // вновь созданный запрос, который не начинал обрабатываться
        CHAINING,       // запрос пошел на обработку и часть урлов уже добавлены в очереди
        CHAINED,        // все урлы добавлены в очереди, запрос на обработке
        SUCCESS,        // успешно завершено выполнение запроса
        PROXY_FAILURE,  // ошибка на стороне прокси сервера - прокси сервер не работает (для Query)
        HOST_FAILURE,   // ошибка на стороне хоста данных - сервер API или сайт данных не работает (для Query)
        FAILURE         // выполнение запроса завершено, но у некоторых урлов возникли ошибки
    }

    /**
     * Отдельные строки запроса в общем запросе.
     * В общем запросе может (и это основной вариант) быть всего одна строка
     */
    public static class Query {
        public final String query;          // одиночный запрос (текст)
        private final Request request;      // родительский общий запрос (где много объектов Query)
        private Status status = Status.NEW; // статус запроса
        private String result;              // результирующий html или json
        private Future<Query> future;       // future
        private long processNanos;         // количество миллисекунд от начала выполнения до конца

        protected Query(String query, Request request) {
            this.query = query;
            this.request = request;
        }

        protected void endProcess(Status status, String result) {
            this.status = status;
            this.result = result;
            processNanos = System.nanoTime() - processNanos;
            request.queryGotResponse(this);
        }

        public Status getStatus() {
            return status;
        }

        public String getResult() {
            return result;
        }

        public String getQueryString() {
            return query;
        }

        public String getHostName() {
            return request.host;
        }

        public void startProcess(Future<Query> future) {
            this.future = future;
            processNanos = System.nanoTime();
        }

        public long getProcessMillis() {
            return processNanos / 1000000;
        }
    }

    private String host;    // сервер, на который выполняется запрос, название сервера (не сам урл)
    private ArrayList<Query> notExecutedQueries = new ArrayList<>();
    private ArrayList<Query> executedQueries = new ArrayList<>();
    private boolean hasError = false;
    private Status status = Status.NEW; // статус всего запроса
    private long millisPassed = 0;      // сколько прошло миллисекунд от момента отправки на выполнение

    public Request(String host) {
        this.host = host;
    }

    public Request(String host, String... query) {
        this(host);
        for (String q : query) {
            addQuery(q);
        }
    }

    /**
     * Добавть один поисковой запрос к общему запросу
     * @param q
     */
    public void addQuery(String q) {
        Query query = new Query(q, this);
        notExecutedQueries.add(query);
    }

    /**
     * Получить все выполненные запросы
     * @return
     */
    public ArrayList<Query> getExecutedQueries() {
        return executedQueries;
    }

    /**
     * Получить статус общего запроса
     * @return
     */
    public Status getStatus() {
        return getStatus();
    }

    /**
     * Когда запрос получил свой результат
     * @param query
     */
    private void queryGotResponse(Query query) {
        hasError |= query.status == Status.FAILURE;
        executedQueries.add(query);
        notExecutedQueries.remove(query);
        if (notExecutedQueries.size() == 0) {
            status = hasError ? Status.FAILURE : Status.SUCCESS;
        }
    }
}
