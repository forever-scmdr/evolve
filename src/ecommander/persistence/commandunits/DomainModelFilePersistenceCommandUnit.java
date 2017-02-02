package ecommander.persistence.commandunits;

import ecommander.controllers.AppContext;
/**
 * Базовая команда для работы с файлом доменов
 * @author E
 *
 */
abstract class DomainModelFilePersistenceCommandUnit extends ItemModelFilePersistenceCommandUnit {
	
	public static final String ROOT_ELEMENT = "domains";
	public static final String DOMAIN_ELEMENT = "domain";
	
	protected String getFileName() {
		return AppContext.getDomainsModelPath();
	}
	
}
