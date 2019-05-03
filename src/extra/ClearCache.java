package extra;

import ecommander.controllers.PageController;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;

/**
 * Created by user on 26.04.2019.
 */
public class ClearCache extends Command {
	@Override
	public ResultPE execute() throws Exception {
		PageController.clearCache();
		return null;
	}
}
