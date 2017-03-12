package ecommander.filesystem;

import ecommander.fwk.Strings;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;

/**
 * Команда для файлов. TransactionContext не используется
 * @author EEEE
 */
public abstract class FilePersistenceCommandUnit implements PersistenceCommandUnit {
	
	public FilePersistenceCommandUnit() {

	}
	
	protected String createItemFileDirectoryName(long itemId, String predIdPath) {
		return predIdPath + itemId + Strings.SLASH;
	}
	
	public TransactionContext getTransactionContext() {
		return null;
	}

	public void setTransactionContext(TransactionContext context) {
		// ничего не делать
	}
}