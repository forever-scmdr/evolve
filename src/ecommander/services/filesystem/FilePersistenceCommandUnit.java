package ecommander.services.filesystem;

import ecommander.common.Strings;
import ecommander.persistence.PersistenceCommandUnit;
import ecommander.persistence.TransactionContext;

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