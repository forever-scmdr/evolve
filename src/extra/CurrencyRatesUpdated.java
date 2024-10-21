package extra;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.common.PersistenceCommandUnit;

/**
 * Класс для перезаписи кеша после обновления айтема курсов валют
 */
public class CurrencyRatesUpdated  implements ItemEventCommandFactory {
    @Override
    public PersistenceCommandUnit createCommand(Item item) throws Exception {
        return new DBPersistenceCommandUnit() {
            @Override
            public void execute() throws Exception {
               Thread t = new Thread(new Runnable() {
                   @Override
                   public void run() {
                       try {
                           Thread.sleep(2000);
                           CurrencyRates.reload();
                       } catch (Exception e) {
                           ServerLogger.error("unable to update currencies", e);
                       }
                   }
               });
               t.start();
            }
        };
    }
}
