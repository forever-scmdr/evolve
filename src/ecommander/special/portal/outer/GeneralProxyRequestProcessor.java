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

    private final boolean isRotating;

    private GeneralProxyRequestProcessor(boolean isRotating) {
        this.isRotating = isRotating;
    }

    /**
     * Для общих обычных запросов
     * @return
     */
    public static GeneralProxyRequestProcessor general() {
        return new GeneralProxyRequestProcessor(false);
    }

    /**
     * Для запросов к серверу с каруселью
     * @return
     */
    public static GeneralProxyRequestProcessor rotating() {
        return new GeneralProxyRequestProcessor(true);
    }

    /**
     * Синхронное выполнение
     * Метод зависает до выполения запроса
     * @param url
     * @param responseMimeType
     * @return
     */
    public Result submitSync(String url, String responseMimeType) throws EcommanderException {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        Request request = isRotating
                ? ProxyRequestDispatcher.submitRotaitngUrls(responseMimeType, url)
                : ProxyRequestDispatcher.submitGeneralUrls(responseMimeType, url);
        try {
            boolean isSuccess = request.awaitExecution();
            if (!isSuccess) {
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
     * @param url
     * @param responseMimeType
     */
    public void submitAsync(ResultHandler handler, String url, String responseMimeType) {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        new Thread(() -> {
            try {
                submitSyncAsAsync(handler, url, responseMimeType);
            } catch (EcommanderException e) {
                ServerLogger.error("Download thread error", e);
            }
        }).start();
    }

    /**
     * Синхронное выполнение, но по форме асинхронного, можно использовать тот же класс ResultHandler что и для
     * асинхронного. Метод зависает до выполнения.
     * @param handler
     * @param url
     * @param responseMimeType
     */
    public void submitSyncAsAsync(ResultHandler handler, String url, String responseMimeType) throws EcommanderException {
        // Выполняются все запросы на сервер (в частности все подзапросы BOM)
        final Request request = ProxyRequestDispatcher.submitGeneralUrls(responseMimeType, url);
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
