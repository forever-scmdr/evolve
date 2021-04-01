package extra;

import ecommander.fwk.ItemUtils;
import ecommander.model.Item;
import ecommander.model.UserGroupRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ChangeItemOwnerDBUnit;

public class SetOptional extends Command {
	@Override
	public ResultPE execute() throws Exception {
		Item optional = ItemUtils.ensureSingleRootAnonymousItem("optional_modules", getInitiator());
		executeAndCommitCommandUnits(ChangeItemOwnerDBUnit.newGroup(optional, UserGroupRegistry.getGroup("0dmin")).ignoreUser(true));
		return null;
	}
}
