package ecommander.fwk;

import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;
/**
 * Интерфейс фабрики для создания команд, которые должны выполнятся по событию, которое происходит
 * с айтемом. Например, дополнительная обработка после сохранения айтема
 * @author E
 *
 */
public interface ItemEventCommandFactory {
	PersistenceCommandUnit createCommand(Item item, Item initialVersion) throws Exception;
}
