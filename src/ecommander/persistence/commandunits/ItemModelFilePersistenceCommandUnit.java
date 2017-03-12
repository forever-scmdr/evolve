package ecommander.persistence.commandunits;

import ecommander.controllers.AppContext;
import ecommander.model.DataModelXmlElementNames;

/**
 * Базовая команда для работы с файлом определения типов айтемов
 * @author E
 *
 */
public abstract class ItemModelFilePersistenceCommandUnit extends ModelFilePersistenceCommandUnit implements DataModelXmlElementNames {

	@Override
	protected String getFileName() {
		return AppContext.getUserModelPath();
	}

	@Override
	protected String getRootElementName() {
		return ITEMS;
	}

}
