package ecommander.special.portal.outer;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ServerLogger;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.nio.charset.StandardCharsets;

/**
 * Класс для выполнения запросов в удаленным серверам через прокси.
 * Можно передавать либо один урл либо много уролов в одном запросе. Если урлов много, они будут считаться одним запросом
 * Каждый запрос (все его урлы) можно отправлять либо синхронно либо асинхронно. Если отправлять синхронно, то
 * метод подвисает и ждет выполнения всех запросов. Если отправлять асинхронно, то метод возвращается сразу, но после
 * выполнения запросов вызывается переданный в метод обработчик.
 */
public class GeneralProxyRequestProcessor {

    public interface ResultHandler {
        void handleResult(Result result);
    }

    /**
     * Синхронное выполнение
     * Метод зависает до выполения запроса
     * @param urls
     * @return
     */
    public static Result submitSync(String... urls) throws EcommanderException {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        Request request = ProxyRequestDispatcher.submitGeneralUrls(urls);
        try {
            boolean hadErrors = request.awaitExecution();
            if (hadErrors) {
                StringBuilder errors = new StringBuilder();
                for (Request.Query query : request.getAllQueries()) {
                    if (query.getStatus() != Request.Status.SUCCESS) {
                        errors.append(query.getResultString(StandardCharsets.UTF_8)).append('\n');
                    }
                }
                return new Result(request, Result.RESPONSE_ERROR, errors.toString());
            }
            return new Result(request, Result.SUCCESS, null);
        } catch (Exception e) {
            return new Result(request, Result.CONNECTION_ERROR, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * Асинхронное выполнение
     * Метод возвращается сразу, далее запускается поток с обработчиком
     * TODO можно сделать ThreadPool чтобы не было возможности запуска множества потоков
     * @param handler
     * @param urls
     */
    public static void submitAsync(ResultHandler handler, String... urls) {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        new Thread(() -> {
            try {
                submitSyncAsAsync(handler, urls);
            } catch (EcommanderException e) {
                ServerLogger.error("Download thread error", e);
            }
        }).start();
    }

    /**
     * Синхронное выполнение, но по форме асинхронного, можно использовать тот же класс ResultHandler что и для
     * асинхронного. Метод зависает до выполнения.
     * @param handler
     * @param urls
     */
    public static void submitSyncAsAsync(ResultHandler handler, String... urls) throws EcommanderException {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        final Request request = ProxyRequestDispatcher.submitGeneralUrls(urls);
        Result result;
        try {
            request.awaitExecution();
            result = new Result(request, Result.SUCCESS, null);
        } catch (Exception e) {
            result = new Result(request, Result.CONNECTION_ERROR, ExceptionUtils.getStackTrace(e));
        }
        handler.handleResult(result);
    }
}
