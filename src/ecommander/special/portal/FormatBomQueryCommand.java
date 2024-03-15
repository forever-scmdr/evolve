package ecommander.special.portal;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.special.portal.outer.providers.UserInput;
import org.apache.commons.lang3.StringUtils;

/**
 * Команда, которая преобразует строку, введенную пользователем, в XML структуру для BOM запроса
 * Структура:
 *
 * <bom>
 *     <query qty="100">112233</query>
 *     <query qty="20">device 334</query>
 *     <query qty="50">qwerty 334 mmt</query>
 * </bom>
 *
 */
public class FormatBomQueryCommand extends Command {
    @Override
    public ResultPE execute() throws Exception {
        String query = getVarSingleValue("q");
        ResultPE result = getResult("xml");
        if (StringUtils.isNotBlank(query)) {
            UserInput input = UserInput.createForBomParsing(query);
            int maxQuerySize = 0;
            XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
            xml.startElement("bom");
            for (String singleQuery : input.getQueries().keySet()) {
                xml.addElement("query", singleQuery, "qty", input.getQueries().get(singleQuery));
                maxQuerySize = Math.max(maxQuerySize, singleQuery.length());
            }
            xml.addElement("max_size", maxQuerySize);
            xml.endElement();
            result.setValue(xml.toString());
        }
        return result;
    }
}
