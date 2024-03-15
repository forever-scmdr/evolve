package ecommander.special.portal.outer;

import ecommander.fwk.Strings;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Один прокси сервер со своим пулом потоков
 * Прокси сервер представляет собой пул потоков, которые подключаются к одному и тому же
 * удаленному серверу через клиент.
 * В прокси передается запрос (объект Query, НЕ общий Request). Этот запрос попадает в пул
 * потоков. Когда запрос выполнился, в него устанавливается содержимое, полученное от сервера
 * и соответсвующий статус
 */
public class Proxy {

    private static final int EXEC_TIMES_COUNT = 3;

    public enum Status {
        ONLINE,     // прокси работает
        OFFLINE     // прокси не работает
    }
    /**
     * Класс потока, который фактически выполняет запрос на сервер и
     * устанавливает результат
     */
    private class ProcessServerQueryCallable implements Callable<Request.Query> {
        private Request.Query query;

        public ProcessServerQueryCallable(Request.Query query) {
            this.query = query;
        }

        @Override
        public Request.Query call() throws Exception {
            QueryExecutor.Executor executor = QueryExecutor.get(query.getHostName(), proxyAddress, query.query);
            boolean success = executor.executeQuery();
            if (success) {
                if (StringUtils.startsWith(executor.getResult(), Strings.ERROR_MARK)) {
                    query.endProcess(Request.Status.HOST_FAILURE, executor.getResult());
                } else {
                    query.endProcess(Request.Status.SUCCESS, executor.getResult());
                    synchronized (Proxy.this) {
                        if (status == Status.OFFLINE) {
                            status = Status.ONLINE;
                        }
                        storeExecTime(query.getProcessMillis());
                    }
                }
            } else {
                query.endProcess(Request.Status.PROXY_FAILURE, executor.getResult());
                synchronized (Proxy.this) {
                    if (status == Status.ONLINE) {
                        status = Status.OFFLINE;
                    }
                }
            }
            return query;
        }
    }

    private final String proxyAddress;    // Адрес прокси сервера
    private Status status = Status.ONLINE;

    private LinkedList<Long> lastExecTimes = new LinkedList<>(); // времена исполнения последних запросов
    private long avgExecutionTime = 0;  // среднее время исполнения последних запросов
    private ExecutorService threadPool;   // Пул потоков выполнения запросов на сервер

    public Proxy(String server, int numberOfThreads) {
        this.proxyAddress = server;
        this.threadPool = Executors.newFixedThreadPool(numberOfThreads);
    }

    /**
     * Начать выполнение запроса
     * За процессом выполнения запроса должен следить другой класс (с помощью future)
     * @param query
     */
    public void submitQuery(Request.Query query) {
        Future<Request.Query> queryFuture = threadPool.submit(new ProcessServerQueryCallable(query));
        query.startProcess(queryFuture, proxyAddress);
    }

    /**
     * Работает ли в данный момент этот прокси
     * @return
     */
    public boolean isOnline() {
        return status == Status.ONLINE;
    }

    /**
     * Сохранить время исполнения запроса (для статистики)
     * @param time
     */
    private void storeExecTime(long time) {
        lastExecTimes.addFirst(time);
        if (lastExecTimes.size() == EXEC_TIMES_COUNT + 1) {
            Long last = lastExecTimes.removeLast();
            avgExecutionTime -= (last / EXEC_TIMES_COUNT);
            avgExecutionTime += (time / lastExecTimes.size());
        } else {
            for (Long lastExecTime : lastExecTimes) {
                avgExecutionTime += lastExecTime;
            }
            avgExecutionTime /= lastExecTimes.size();
        }
    }

}
