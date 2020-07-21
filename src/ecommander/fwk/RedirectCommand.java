package ecommander.fwk;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;

/**
 * Простое перенаправление по ссылке success
 */
public class RedirectCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		return getResult("success");
	}
}
