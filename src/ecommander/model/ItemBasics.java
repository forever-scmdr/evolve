package ecommander.model;

/**
 * Базовые сведения об айтеме
 * Created by E on 3/4/2017.
 */
public interface ItemBasics {
	long getId();
	int getTypeId();
	byte getOwnerGroupId();
	int getOwnerUserId();
	byte getStatus();
	String getKey();
	boolean isFileProtected();
	boolean isPersonal();
}
