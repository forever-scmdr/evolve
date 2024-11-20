package ecommander.extra;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ecommander.application.extra.ItemUtils;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ItemHttpPostForm;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.DeleteItemBDUnit;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;

public class ManageAgents extends Command {

	@Override
	public ResultPE execute() throws Exception {
		ItemHttpPostForm form = getItemForm();
		// Удаление
		if (form == null) {
			String ids = getVarSingleValueDefault("id", "");
			List<Item> agents = ItemQuery.loadByIdsString(Arrays.asList(StringUtils.split(ids, ',')));
			for (Item agent : agents) {
				executeAndCommitCommandUnits(new DeleteItemBDUnit(agent).fulltextIndex(false));
			}
			LuceneIndexMapper.reindexAll();
			return getResult("success").addVariable("message", "Агенты успешно удалены");
		} else {
			// Подгружен файл с агентами - разобрать
			if (StringUtils.equals(form.getItemTypeName(), ItemNames.ALL_AGENTS)) {
				Item allAgents = ItemUtils.ensureSingleRootItem(ItemNames.ALL_AGENTS, getInitiator(), false);
				form.editExistingItem(allAgents);
				executeAndCommitCommandUnits(new UpdateItemDBUnit(allAgents));
				return getResult("import");
			}
			// Редактирование
			if (form.getItemId() > 0) {
				Item agent = ItemQuery.loadById(form.getItemId());
				form.editExistingItem(agent);
				executeAndCommitCommandUnits(new UpdateItemDBUnit(agent));
				return getResult("success").addVariable("message", "Агент успешно обновлен");
			} 
			// Новый агент
			else {
				Item agents = ItemUtils.ensureSingleRootItem(ItemNames.ALL_AGENTS, getInitiator(), false);
				Item agent = form.createItem(getInitiator().getUserId(), getInitiator().getGroupId(), agents.getId());
				agent.setValue(ItemNames.agent.REGISTER_DATE, DateTime.now(DateTimeZone.UTC).getMillis());
				executeAndCommitCommandUnits(new SaveNewItemDBUnit(agent));
				return getResult("success").addVariable("message", "Агент успешно создан");
			}
		}
	}

}
