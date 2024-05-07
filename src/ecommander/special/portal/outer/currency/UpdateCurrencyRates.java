package ecommander.special.portal.outer.currency;

import ecommander.fwk.ItemUtils;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;

public class UpdateCurrencyRates extends Command implements ItemNames {

    @Override
    public ResultPE execute() throws Exception {
        String curCode = CurrencyRates.DEFAULT_CURRENCY;
        Item settings = ItemQuery.loadSingleItemByName(DISPLAY_SETTINGS);
        if (settings != null) {
            curCode = settings.getStringValue(display_settings_.DEFAULT_CURRENCY, CurrencyRates.DEFAULT_CURRENCY);
        }
        CurrencyRatesGetter getter = CurrencyRates.getCurrencyGetter(curCode);
        Item currencies = ItemQuery.loadSingleItemByName(CURRENCIES);
        if (currencies == null) {
            Item catalogMeta = ItemUtils.ensureSingleRootAnonymousItem(CATALOG_META, User.getDefaultUser());
            currencies = ItemUtils.ensureSingleAnonymousItem(CURRENCIES, User.getDefaultUser(), catalogMeta.getId());
        }
        try {
            String xml = getter.getBankRatesRemote();
            currencies.setValue(currencies_.XML, xml);
            executeAndCommitCommandUnits(SaveItemDBUnit.get(currencies));
        } catch (Exception e) {
            ServerLogger.error("Unable to update currency rates with code " + curCode, e);
        }
        return null;
    }

}
