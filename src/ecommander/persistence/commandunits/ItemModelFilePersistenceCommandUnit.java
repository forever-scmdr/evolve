package ecommander.persistence.commandunits;

import ecommander.controllers.AppContext;
/**
 * Базовая команда для работы с файлом определения типов айтемов
 * @author E
 *
 */
public abstract class ItemModelFilePersistenceCommandUnit extends ModelFilePersistenceCommandUnit {

	public static final String ROOT_ELEMENT = "items";
	public static final String ITEM_ELEMENT = "item";
	
	@Override
	protected String getFileName() {
		return AppContext.getUserModelPath();
	}

	@Override
	protected String getRootElementName() {
		return ROOT_ELEMENT;
	}

}
