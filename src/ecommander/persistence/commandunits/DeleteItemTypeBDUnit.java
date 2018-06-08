package ecommander.persistence.commandunits;

import org.apache.commons.lang3.StringUtils;

import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;

/**
 * Удаление типа айтема.
 * Тип може удалятся с удалением всех его айтемов либо с изменением типа всех его айтемов. Во втором случае все айтемы сохраняются
 * @author EEEE
 */
public class DeleteItemTypeBDUnit extends ItemModelFilePersistenceCommandUnit {
	
	private int typeId;
	private int newTypeId = -1;
	private String typeName = null;

	public DeleteItemTypeBDUnit(int typeId, int newTypeId) {
		this.typeId = typeId;
		this.newTypeId = newTypeId;
	}

	public DeleteItemTypeBDUnit(int typeId) {
		this.typeId = typeId;
		this.newTypeId = -1;
	}

	public DeleteItemTypeBDUnit(String typeName) {
		this.typeName = typeName;
		this.newTypeId = -1;
	}

	@Override
	protected void executeInt() throws Exception {
		if (typeName == null) {
			ItemType deletedItem = ItemTypeRegistry.getItemType(typeId);
			if (deletedItem != null)
				typeName = deletedItem.getName();
		}
		// Удаление типа и всех его айтемов
		if (newTypeId < 0 && typeName != null) {
			String startMark = getStartMark(typeName) + '\n';
			String endMark = getEndMark(typeName) + '\n';
			while (StringUtils.contains(getFileContents(), startMark)) {
				String startPart = StringUtils.substringBefore(getFileContents(), startMark);
				String endPart = StringUtils.substringAfter(getFileContents(), endMark);
				setFileContents(startPart + endPart);
			}
			saveFile();
		}
		// Удаление типа и замена типа всех его айтемов
		else {
			// TODO <enhance> сделать изменение типа айтемов удаляемого типа
		}
	}

}