package extra;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

public class SearchAnyRemoteCommand extends Command {

    @Override
    public ResultPE execute() throws Exception {
        String remote = getVarSingleValueDefault("remote", "findchips");
        if (StringUtils.containsIgnoreCase(remote, "findchips")) {
            Command findchipsCommand = new SearchFindchipsCommand(this);
            return findchipsCommand.execute();
        }
        if (StringUtils.containsIgnoreCase(remote, "oemsecrets")) {
            Command oemsecretsCommand = new SearchOemsecretsCommand(this);
            return oemsecretsCommand.execute();
        }
        return null;
    }
}
