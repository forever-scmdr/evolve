package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ItemEventCommandFactory;
import ecommander.model.Item;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.common.PersistenceCommandUnit;

/**
 * Обновить глобальную переменную куки для digikey
 */
public class UpdateDigikeyCookie implements ItemEventCommandFactory {

    private static class UpdateCookie extends DBPersistenceCommandUnit {

        private Item catalogMeta;

        public UpdateCookie(Item catalogMeta) {
            this.catalogMeta = catalogMeta;
        }

        @Override
        public void execute() throws Exception {
            AppContext.setGlobalVar("digi_cookie", catalogMeta.getStringValue("digikey_cookie"));
        }
    }

    @Override
    public PersistenceCommandUnit createCommand(Item item) throws Exception {
        return new UpdateCookie(item);
    }
}
