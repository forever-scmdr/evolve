package ecommander.extra;

import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.EmailUtils;
import ecommander.application.extra.ItemUtils;
import ecommander.extra._generated.Agent;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ItemHttpPostForm;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.SynchronousTransaction;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.users.User;

public class SubscribeCommand extends Command {
	
	private final String TOPIC = "Новая подписка на рассылку новостей. Подписчик: ";
	private final String TOPIC_CHANGE = "Подписчик %s изменил список интересующих тем";
	private final String TOPIC_CANCEL = "Подписчик %s отказался от рассылки";

	@Override
	public ResultPE execute() throws Exception {
		try{
		ItemHttpPostForm postForm = getItemForm();
		Agent subscriber = Agent.get(postForm.createItem(User.getDefaultUser()));

		boolean cancel = "yes".equals(postForm.getSingleExtra("cancel")) || "yes".equals(getVarSingleValue("cancel"));
		String email = subscriber.get_email();
		
		StringBuilder emailText = new StringBuilder();
		emailText.append("E-mail: ").append(email);
		emailText.append("\nТемы новостей:");
		for(Object tag : subscriber.getValues("tags")) {
			emailText.append('\n').append(tag);
		}
		String terobrestEmail = getVarSingleValue("email");
		
		if (StringUtils.isEmpty(email) || !email.contains("@"))
			return getResult("error_not_set");

		Item old = ItemQuery.loadSingleItemByParamValue(ItemNames.AGENT, ItemNames.agent.EMAIL, email);

		if (old != null) {
			if (cancel) {
				old.removeValue(ItemTypeRegistry.getItemType(ItemNames.AGENT).getParameter(ItemNames.agent.TAGS).getId());
				executeAndCommitCommandUnits(new UpdateItemDBUnit(old).ignoreUser(true));
				String topic = String.format(TOPIC_CANCEL, email);
				EmailUtils.sendTextGmailDefault(terobrestEmail, topic, topic);
				EmailUtils.sendTextGmailDefault(email, "вы успешно отказались от рассылки новостей Термобрест", "Вы больше не будете получать новости СП ООО «ТермоБрест»");
				return getResult("cancel_success");
			} else {
				ItemHttpPostForm.editExistingItem(postForm, old, ItemNames.agent.TAGS);
				executeAndCommitCommandUnits(new UpdateItemDBUnit(old).ignoreUser(true));
				String topic = String.format(TOPIC_CHANGE, email);
				EmailUtils.sendTextGmailDefault(terobrestEmail, topic, emailText.toString());
				return getResult("success");
			}
		} else {
			if (cancel) {
				return getResult("cancel_success");
			}
			Item rss = ItemUtils.ensureSingleRootItem(ItemNames.RSS, User.getDefaultUser(), false);
			subscriber.setDirectParentId(rss.getId());
			executeAndCommitCommandUnits(new SaveNewItemDBUnit(subscriber).ignoreUser(true));
			EmailUtils.sendTextGmailDefault(terobrestEmail, TOPIC + email, emailText.toString());
			
		}
		}catch(Exception e){
			return getResult("general_error");
		}
		return getResult("success");
	}

	public void createSubscriber(ItemHttpPostForm postForm) throws Exception{
		
		SynchronousTransaction transaction = new SynchronousTransaction(User.getDefaultUser());
		
		String email = postForm.getValueStr(ItemNames.agent.EMAIL);
		Item old = ItemQuery.loadSingleItemByParamValue(ItemNames.AGENT, ItemNames.agent.EMAIL, email);
		
		if (old != null) {
			postForm.editExistingItem(old);
			transaction.executeCommandUnit(new UpdateItemDBUnit(old));
			transaction.commit();
		} else {
			Item rss = ItemUtils.ensureSingleRootItem(ItemNames.RSS, User.getDefaultUser(), false);
			Item subscriber = postForm.createItem(User.getDefaultUser().getUserId(), User.getDefaultUser().getGroupId(), rss.getId());
			transaction.executeCommandUnit(new SaveNewItemDBUnit(subscriber));
			transaction.commit();
		}
	}
}
