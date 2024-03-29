package ecommander.fwk;

import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


public class SubscribeCommand extends Command {

	protected static final String SYSTEM_ITEM = "system";
	protected static final String OBSERVERS_ITEM = "observers";
	protected static final String OBSERVER_ITEM = "observer";

	protected static final String OBSERVER_PARAM = "observer";
	protected static final String OBSERVABLE_PARAM = "observable";

	@Override
	public ResultPE execute() throws Exception {
		String observer, observable;
		if (getItemForm() != null) {
			Item form = getItemForm().getItemSingleTransient();
			observer = form.getStringValue(OBSERVER_PARAM);
			observable = form.getStringValue(OBSERVABLE_PARAM);
		} else {
			observer = getVarSingleValue("observer");
			observable = getVarSingleValue("observable");
		}
		if (StringUtils.isAnyBlank(observer, observable)) {
			saveSessionForm("observer");
			return getResult("error_not_set");
		}
		List<Item> existingObservers = new ItemQuery(OBSERVER_ITEM)
				.addParameterCriteria(OBSERVER_PARAM, observer, "=", null, Compare.SOME)
				.addParameterCriteria(OBSERVABLE_PARAM, observable, "=", null, Compare.SOME)
				.loadItems();
		String action = getVarSingleValueDefault("action", "add");
		boolean isAdd = StringUtils.equalsAnyIgnoreCase(action, "add", "set", "create");
		if (isAdd) {
			if (existingObservers.size() == 0) {
				Item system = ItemUtils.ensureSingleRootAnonymousItem(SYSTEM_ITEM, getInitiator());
				Item observers = ItemUtils.ensureSingleAnonymousItem(OBSERVERS_ITEM, getInitiator(), system.getId());
				Item obs = Item.newChildItem(ItemTypeRegistry.getItemType(OBSERVER_ITEM), observers);
				Item.updateParamValues(getItemForm().getItemSingleTransient(), obs);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(obs).ignoreUser());
			}
			return getResult("success");
		} else {
			for (Item existingObserver : existingObservers) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(existingObserver).ignoreUser().noFulltextIndex());
			}
			return getResult("deleted");
		}
	}
}
