package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.OkWebClient;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Просто перенаправляет запрос на другой сервер с установленным на нем Ecommander-ом
 * Другими словами перенаправляет на другой наш сайт
 *
 * Передаваемые параметры:
 *      url         - урл, который надо получить
 *      mime_type   - тип возвращаемого результата (не обязательно)
 *      headers     - дополнительные заголовки (не обязательно)
 */
public class AnotherEcommanderProxy extends Command {

    private static final String GLOBAL_COOKIE = "digi_cookie";
    @Override
    public ResultPE execute() throws Exception {
        String url = getVarSingleValue("url");
        String mimeType = getVarSingleValueDefault("mime_type", "text/html");
        String answer = "Неверный формат запроса";
        List<Object> headers = getVarValues("headers");
        ArrayList<String> readyHeaders = new ArrayList<>();
        for (Object header : headers) {
            String name = StringUtils.substringBefore((String) header, ":");
            String value = StringUtils.substringAfter((String) header, ":");
            readyHeaders.add(name);
            readyHeaders.add(value);
        }
        String cookie = AppContext.getGlobalVar(GLOBAL_COOKIE);
        if (StringUtils.isNotBlank(cookie)) {
            readyHeaders.add("Cookie");
            readyHeaders.add(cookie);
        }
        String[] headersArg = readyHeaders.toArray(new String[readyHeaders.size()]);
        try {
            if (StringUtils.isNotBlank(url)) {
                ServerLogger.debug("\n\n\t\tPROXYING: " + url + "\nheaders: " + headers);
                if (StringUtils.startsWith(mimeType, "text")) {
                    return getResult("html").setValue(OkWebClient.getInstance().getString(url, headersArg));
                } else {
                    byte[] bytes = OkWebClient.getInstance().getBytes(url, headersArg);
                    return getResult("bytes").setBytes(bytes).setMimeType(mimeType);
                }
            } else {
                return getResult("illegal_argument").setValue(answer);
            }
        } catch (Exception e) {
            String error = Strings.ERROR_MARK + "\n\n" + ExceptionUtils.getStackTrace(e);
            return getResult("error").setValue(error);
        }
    }
}
