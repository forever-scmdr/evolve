package ecommander.persistence.commandunits;

/**
 * Created by E on 23/11/2017.
 */
public class ProtectItemFilesDBUnit extends DBPersistenceCommandUnit {

	private long itemId;
	private boolean makeProtected;
	private boolean recursively;

	public ProtectItemFilesDBUnit(long itemId, boolean makeProtected, boolean recursively) {
		this.itemId = itemId;
		this.makeProtected = makeProtected;
		this.recursively = recursively;
	}

	@Override
	public void execute() throws Exception {

	}
}
