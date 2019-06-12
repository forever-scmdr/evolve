package extra;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.model.UserMapper;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.DeleteUserDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;

/**
 * Created by E on 27/3/2019.
 */
public class DeleteRegisteredUser implements ItemEventCommandFactory {

	private static class Command extends DBPersistenceCommandUnit {

		private Item regForm;

		public Command(Item regForm) {
			this.regForm = regForm;
		}

		@Override
		public void execute() throws Exception {
			int userId = UserMapper.getUserId(regForm.getStringValue("email"), getTransactionContext().getConnection());
			if (userId > 0)
				executeCommand(new DeleteUserDBUnit(userId, true));
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new Command(item);
	}
}
