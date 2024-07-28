package extra;

import ecommander.fwk.OkWebClient;
import ecommander.fwk.Strings;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Просто перенаправляет запрос на другой сервер с установленным на нем Ecommander-ом
 * Другими словами перенаправляет на другой наш сайт
 *
 * Передаваемые параметры:
 *      url  - урл, который надо получить
 *
 */
public class AnotherEcommanderProxy extends Command {
    @Override
    public ResultPE execute() throws Exception {
        String url = getVarSingleValue("url");
        String mimeType = getVarSingleValueDefault("mime_type", "text/html");
        String answer = "Неверный формат запроса";
        try {
            if (StringUtils.isNotBlank(url)) {
                if (StringUtils.startsWith(mimeType, "text")) {
                    return getResult("html").setValue(OkWebClient.getInstance().getString(url));
                } else {
                    byte[] bytes = OkWebClient.getInstance().getBytes(url);
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
