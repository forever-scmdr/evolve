package ecommander.persistence.commandunits;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;

/**
 * Удаление типа айтема.
 * Тип може удалятся с удалением всех его айтемов либо с изменением типа всех его айтемов. Во втором случае все айтемы сохраняются
 * @author EEEE
 */
public class DeleteItemTypeBDUnit extends ItemModelFilePersistenceCommandUnit {
	
	private int typeId;
	private int newTypeId = -1;

	public DeleteItemTypeBDUnit(int typeId, int newTypeId) {
		this.typeId = typeId;
		this.newTypeId = newTypeId;
	}

	public DeleteItemTypeBDUnit(int typeId) {
		this.typeId = typeId;
		this.newTypeId = -1;
	}
	
	@Override
	protected void executeInt() throws Exception {
		ItemType deletedItem = ItemTypeRegistry.getItemType(typeId);
		// Удаление типа и всех его айтемов
		if (newTypeId < 0) {
			String startMark = getStartMark(deletedItem.getName());
			String endMark = getEndMark(deletedItem.getName());
			String startPart = StringUtils.substringBefore(getFileContents(), startMark);
			String endPart = StringUtils.substringAfter(getFileContents(), endMark);
			setFileContents(startPart + endPart);
			saveFile();
		}
		// Удаление типа и замена типа всех его айтемов
		else {
			// TODO <enhance> сделать изменение типа айтемов удаляемого типа
		}
	}

}