package ecommander.extra;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import ecommander.application.extra.ItemUtils;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.LinkPE;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;

public class EmailQueueAdd extends Command {
	
	@Override
	public ResultPE execute() throws Exception {
		List<String> emailIds = getVarValues("email_id");
		String agentIdsStr = getVarSingleValue("agent_ids");
		boolean resend = StringUtils.equalsIgnoreCase(getVarSingleValue("resend"), "yes");
		if (StringUtils.isBlank(agentIdsStr) || emailIds.size() == 0)
			return null;
		ArrayList<String> agentIds = new ArrayList<String>();
		CollectionUtils.addAll(agentIds, StringUtils.split(agentIdsStr, ','));
		if (agentIds.size() == 0)
			return null;
		List<Item> agents = ItemQuery.loadByIdsString(agentIds);
		List<Item> emails = ItemQuery.loadByIdsString(emailIds);
		Item queue = ItemUtils.ensureSingleRootItem(ItemNames.EMAIL_QUEUE, getInitiator(), false);
		ItemType queueItemType = ItemTypeRegistry.getItemType(ItemNames.EMAIL_QUEUE_ITEM);
		
		HashSet<String> rejected = new HashSet();
		String rejectedString = ItemQuery.loadSingleItemByName("rejected").getStringValue("email_list","");
		
		CollectionUtils.addAll(rejected, StringUtils.split(rejectedString, ",; "));
		
		int commandCount = 0;
		for (Item agent : agents) {
			ArrayList<String> agentEmails = new ArrayList<String>();
			String agentEmail = agent.getStringValue(ItemNames.agent.EMAIL, "");
			String agentEmail2 = agent.getStringValue(ItemNames.agent.EMAIL_2, "");
			String agentEmail3 = agent.getStringValue(ItemNames.agent.EMAIL_3, "");
			CollectionUtils.addAll(agentEmails, StringUtils.split(agentEmail, ",; "));
			CollectionUtils.addAll(agentEmails, StringUtils.split(agentEmail2, ",; "));
			CollectionUtils.addAll(agentEmails, StringUtils.split(agentEmail3, ",; "));
			if (StringUtils.isBlank(agentEmail) || rejected.contains(agentEmail))
				continue;
			for (Item email : emails) {
				if (!resend) {
					if (agent.containsValue(ItemNames.agent.EMAILS_SENT, email.getId()))
						continue;
				}
				String template = email.getStringValue(ItemNames.email_post.TEMPLATE, "agent_standard_email");
				LinkPE templateLink = LinkPE.newDirectLink("email template", template, false);
				templateLink.addStaticVariable("post", email.getId() + "");
				templateLink.addStaticVariable("agent", agent.getId() + "");
				for(String em : agentEmails) {
					Item queueItem = Item.newChildItem(queueItemType, queue);
					queueItem.setValue(ItemNames.email_queue_item.DATE_ADDED, System.currentTimeMillis());
					//queueItem.setValue(ItemNames.email_queue_item.ADDRESS_TO, StringUtils.join(agentEmails, ','));
					queueItem.setValue(ItemNames.email_queue_item.ADDRESS_TO, em);
					queueItem.setValue(ItemNames.email_queue_item.EMAIL_URL, templateLink.serialize());
					queueItem.setValue(ItemNames.email_queue_item.AGEND_ID, agent.getId());
					queueItem.setValue(ItemNames.email_queue_item.EMAIL_ID, email.getId());
					executeCommandUnit(new SaveNewItemDBUnit(queueItem, false));
					
					if (++commandCount >= 10) {
						commitCommandUnits();
						commandCount = 0;
					}
				}
			}
		}
		commitCommandUnits();
		return null;
	}

}
