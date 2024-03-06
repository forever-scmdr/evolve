package ecommander.special.portal.outer;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.Future;

/**
 * Запрос от одного клиента. Одно действие запроса (юзер один раз нажал кнопку "Найти")
 * Содержит один или несколько поисковых запросов
 * Обращается к определенному серверу. Юзер не задает сервер, сервер назначается отдельно, но в
 * запросе он известен
 */
public class Request {

    public static int MAX_TRIES = 3;

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
        private volatile Status status = Status.NEW; // статус запроса
        private volatile String result;              // результирующий html или json
        private Future<Query> future;       // future
        private volatile long processNanos;          // количество миллисекунд от начала выполнения до конца
        private int numTries = 0;           // Количество попыток выполнения запроса
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
            return request.hostName;
        }

        public int getNumTries() {
            return numTries;
        }

        public Future<Query> getFuture() {
            return future;
        }

        public void startProcess(Future<Query> future) {
            this.future = future;
            processNanos = System.nanoTime();
            numTries++;
        }

        public long getProcessMillis() {
            return processNanos / 1000000;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || (obj instanceof Query && ((Query) obj).query.equals(query) && ((Query) obj).request == request);
        }
    }

    private String hostName;    // название сервера, на который выполняется запрос, название сервера (не сам урл)
    private HostThroughProxies host;    // сервер, на который выполняется запрос, название сервера (не сам урл)
    // все запросы
    private LinkedHashSet<Query> allQueries = new LinkedHashSet<>();
    // запросы, которые уже не надо выполнять, у них уже есть нужный результат
    private LinkedHashSet<Query> successfulQueries = new LinkedHashSet<>();
    // запросы, выполненные с ошибкой (требуется повторное выполнение)
    private LinkedHashSet<Query> failedQueries = new LinkedHashSet<>();
    private volatile boolean hasError = false;
    private volatile Status status = Status.NEW; // статус всего запроса
    private long nanosTookToFinish = 0;      // сколько прошло миллисекунд от момента отправки на выполнение

    public Request(String hostName) {
        this.hostName = hostName;
    }

    Request(String host, String... query) {
        this(host);
        for (String q : query) {
            addQuery(q);
        }
    }

    /**
     * Добавть один поисковой запрос к общему запросу
     * @param q
     */
    void addQuery(String q) {
        Query query = new Query(q, this);
        allQueries.add(query);
    }

    /**
     * Получить статус общего запроса
     * @return
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Когда запрос получил свой результат
     * Вызывается автоматически (не надо вызывать вручную)
     *
     * Этот метод вызывается параллельно из разных потоков
     * (изнутри потоков из пулов разных прокси)
     * @param query
     */
    private synchronized void queryGotResponse(Query query) {
        hasError |= query.status == Status.FAILURE;
        if (query.status == Status.SUCCESS) {
            successfulQueries.add(query);
        } else {
            failedQueries.add(query);
        }
        if (allQueries.size() == (failedQueries.size() + successfulQueries.size())) {
            status = hasError ? Status.FAILURE : Status.SUCCESS;
        }
    }

    /**
     * Запустить весь запрос (все поисковые слова) на выполнение
     * Этот метод может блокироваться в случае если очередь прокси сервера переполнена,
     * и надо подождать пока в ней освободится место (выполнится другой запрос этого прокси)
     * @param host
     */
    void submit(HostThroughProxies host) {
        status = Status.CHAINING;
        this.host = host;
        for (Query query : allQueries) {
            host.getNextProxyAndRotate().submitQuery(query);
        }
        status = Status.CHAINED;
    }

    /**
     * Блокирующий метод (блокирует поток в котором вызывается)
     * Метод ждет до момента, пока все запросы не будут выполнены, желательно без ошибок.
     */
    public boolean awaitExecution(HostThroughProxies... alternativeHost) {
        nanosTookToFinish = System.nanoTime();
        waitForQueries(allQueries);
        if (hasError) {
            boolean notYetMaxTries = true;
            if (alternativeHost.length > 0) {
                host = alternativeHost[0];
            }
            while (notYetMaxTries && hasError) {
                notYetMaxTries = false;
                hasError = false;
                LinkedHashSet<Query> toExecute = new LinkedHashSet<>(failedQueries);
                failedQueries.clear();
                for (Query query : toExecute) {
                    if (query.getNumTries() < MAX_TRIES) {
                        notYetMaxTries = true;
                        host.getNextProxyAndRotate().submitQuery(query);
                    }
                }
                waitForQueries(toExecute);
            }
        }
        nanosTookToFinish = System.nanoTime() - nanosTookToFinish;
        status = hasError ? Status.FAILURE : Status.SUCCESS;
        return !hasError;
    }

    /**
     * Блокирующий метод (блокирует поток в котором вызывается)
     * Ждет выполнения всех запросов из переданного списка
     * @param queries
     */
    private void waitForQueries(Collection<Query> queries) {
        for (Query query : queries) {
            try {
                query.getFuture().get();
            } catch (Exception e) {
                query.status = Status.FAILURE;
                failedQueries.add(query);
            }
        }
    }

    /**
     * Вернуть время выполнения запроса
     * @return
     */
    public long getNanosTookToFinish() {
        return nanosTookToFinish;
    }

    /**
     * Все запросы.
     * Может понадобиться, например, если надо посмотреть какой выполнился, какой нет
     */
    public LinkedHashSet<Query> getAllQueries() {
        return allQueries;
    }
}
