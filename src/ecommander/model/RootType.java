package ecommander.model;

/**
 * Корневой айтем, может быть только один
 * Created by E on 12/3/2017.
 */
public class RootType extends ItemTypeContainer implements ItemBasics {

	final static int ROOT_TYPE_ID = 0;
	final static long ROOT_ITEM_ID = 0L;

	final int typeId = 0;
	final long id = 0L;

	RootType() {

	}

	public int getTypeId() {
		return typeId;
	}

	@Override
	public byte getOwnerGroupId() {
		return User.NO_GROUP_ID;
	}

	@Override
	public int getOwnerUserId() {
		return User.ANONYMOUS_ID;
	}

	@Override
	public byte getStatus() {
		return Item.STATUS_NORMAL;
	}

	@Override
	public String getKey() {
		return getName();
	}

	@Override
	public boolean isFileProtected() {
		return false;
	}

	@Override
	public boolean isPersonal() {
		return false;
	}

	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return "root";
	}
}
