package ecommander.persistence.commandunits;

import ecommander.model.Item;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

/**
 * Created by User on 20.03.2017.
 */
public class CreateAssocDBUnit extends DBPersistenceCommandUnit implements DBConstants {

	private Item item;
	private long parentId;
	private byte assocId;
	boolean isItemNew = false;

	public CreateAssocDBUnit(Item item, long parentId, byte assocId, boolean isItemNew) {
		this.assocId = assocId;
		this.item = item;
		this.parentId = parentId;
		this.isItemNew = isItemNew;
	}

	public CreateAssocDBUnit(Item item, long parentId, byte assocId) {
		this.assocId = assocId;
		this.item = item;
		this.parentId = parentId;
	}

	@Override
	public void execute() throws Exception {
		TemplateQuery checkQuery = new TemplateQuery("check assoc validity");
		checkQuery.SELECT(ItemParent.PARENT_ID).FROM(ItemParent.TABLE)
				.WHERE().crit(ItemParent.CHILD_ID, "=").setLong(item.getId())
				.AND().crit(ItemParent.ASSOC_ID, "=").setByte(assocId)
				.UNION_ALL()
				.SELECT(ItemParent.PARENT_ID).FROM(ItemParent.TABLE)
				.WHERE().crit(ItemParent.CHILD_ID, "=").setLong(parentId)
				.AND().crit(ItemParent.ASSOC_ID, "=").setByte(assocId);

	}
}
