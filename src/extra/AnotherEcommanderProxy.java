package extra;

import ecommander.fwk.OkWebClient;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Просто перенаправляет запрос на другой сервер с установленным на нем Ecommander-ом
 * Другими словами перенаправляет на другой наш сайт
 *
 * Передаваемые параметры:
 *      server  - proxy сервер (наш другой сайт)
 *      path    - путь урла (без переменных, как правило просто название страницы)
 *      q   - дописываются в запрос
 *
 */
public class AnotherEcommanderProxy extends Command {
    @Override
    public ResultPE execute() throws Exception {
        String server = getVarSingleValue("server");
        String path = getVarSingleValue("path");
        String q = getVarSingleValue("q");
        String cur = getVarSingleValue("cur");
        String answer = "Неверный формат запроса";
        try {
            q = StringUtils.normalizeSpace(q);
            if (StringUtils.isNotBlank(q)) {
                String requestUrl = server + path + "?q=" + q + "&cur=" + cur;
                answer = OkWebClient.getInstance().getString(requestUrl);
            } else {
                return getResult("illegal_argument").setValue(answer);
            }
        } catch (Exception e) {
            return getResult("error").setValue(ExceptionUtils.getStackTrace(e));
        }
        return getResult("xml").setValue(answer);
    }
}
