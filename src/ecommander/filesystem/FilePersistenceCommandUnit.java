package ecommander.filesystem;

import ecommander.fwk.Strings;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;

import java.io.File;

/**
 * Команда для файлов. TransactionContext не используется
 * @author EEEE
 */
public abstract class FilePersistenceCommandUnit implements PersistenceCommandUnit {

	public FilePersistenceCommandUnit() {

	}

	public TransactionContext getTransactionContext() {
		return null;
	}

	public void setTransactionContext(TransactionContext context) {
		// ничего не делать
	}
}