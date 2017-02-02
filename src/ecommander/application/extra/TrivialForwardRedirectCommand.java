package ecommander.application.extra;

import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
import ecommander.pages.elements.ResultPE.ResultType;

public class TrivialForwardRedirectCommand extends Command {

	@Override
	public ResultPE execute() throws Exception {
		for (ResultPE result : getAllResults()) {
			if (result.getType() == ResultType.redirect || result.getType() == ResultType.forward)
				return result;
		}
		return null;
	}

}
