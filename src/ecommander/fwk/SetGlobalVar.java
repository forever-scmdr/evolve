package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;

/**
 * Берет со страницы переменные с именами name и value и делает глобальную переменную с такими названием и значением
 */
public class SetGlobalVar extends Command {
    @Override
    public ResultPE execute() throws Exception {
        String name = getVarSingleValueDefault("name", null);
        String value = getVarSingleValueDefault("value", null);
        AppContext.setGlobalVar(name, value);
        return null;
    }
}
