package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

public class LowerCaseSearchQueryCommand extends Command {
    @Override
    public ResultPE execute() throws Exception {
        String q = getVarSingleValue("q");
        if (StringUtils.isNotBlank(q)) {
            setPageVariable("q", null);
            setPageVariable("q", StringUtils.lowerCase(q));
        }
        return null;
    }
}
