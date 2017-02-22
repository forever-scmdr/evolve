package ecommander.persistence.commandunits;

import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;

/**
 * Удаление параметра айтема
 * @author EEEE
 */
public class DeleteParameterDescriptionBDUnit extends ItemModelFilePersistenceCommandUnit {
	
	private Integer itemId;
	private Integer paramId;

	public DeleteParameterDescriptionBDUnit(Integer itemId, Integer paramId) {
		this.itemId = itemId;
		this.paramId = paramId;
	}

	@Override
	protected void executeInt() throws Exception {
		ItemType itemDesc = ItemTypeRegistry.getItemType(itemId);
		itemDesc.removeParameter(paramId);
		executeCommand(new UpdateItemTypeDBUnit(itemDesc));
	}

}