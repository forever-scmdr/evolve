package ecommander.model;

/**
 * Корневой айтем, может быть только один
 * Created by E on 12/3/2017.
 */
public class RootType extends ItemTypeContainer {

	static final int TYPE_ID = 0;
	static final long ID = 0L;

	RootType() {

	}

	public int getTypeId() {
		return TYPE_ID;
	}

	public long getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "root";
	}
}
