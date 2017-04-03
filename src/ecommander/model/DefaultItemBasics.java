package ecommander.model;

/**
 * Базовые сведения об айтеме
 * Created by E on 3/4/2017.
 */
public class DefaultItemBasics implements ItemBasics {
	private long id;
	private int typeId;
	private String key;
	private byte ownerGroupId;
	private int ownerUserId;
	private byte status;
	private boolean isFileProtected;

	public DefaultItemBasics(long id, int typeId, String key, byte ownerGroupId, int ownerUserId, byte status, boolean isFileProtected) {
		this.id = id;
		this.typeId = typeId;
		this.key = key;
		this.ownerGroupId = ownerGroupId;
		this.ownerUserId = ownerUserId;
		this.status = status;
		this.isFileProtected = isFileProtected;
	}


	@Override
	public long getId() {
		return id;
	}

	@Override
	public int getTypeId() {
		return typeId;
	}

	@Override
	public byte getOwnerGroupId() {
		return ownerGroupId;
	}

	@Override
	public int getOwnerUserId() {
		return ownerUserId;
	}

	@Override
	public byte getStatus() {
		return status;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public boolean isFileProtected() {
		return isFileProtected;
	}

	@Override
	public boolean isPersonal() {
		return ownerUserId != 0;
	}
}
