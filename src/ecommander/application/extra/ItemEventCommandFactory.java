package ecommander.application.extra;

import ecommander.model.item.Item;
import ecommander.persistence.PersistenceCommandUnit;
/**
 * Интерфейс фабрики для создания команд, которые должны выполнятся по событию, которое происходит
 * с айтемом. Например, дополнительная обработка после сохранения айтема
 * @author E
 *
 */
public interface ItemEventCommandFactory {
	PersistenceCommandUnit createCommand(Item item, Item initialVersion) throws Exception;
}
