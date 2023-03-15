package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class CreateUpdatedOrdersFeed extends Command {

	@Override
	public ResultPE execute() throws Exception {
		List<Item> updatedOrders = ItemQuery.loadByParamValue("purchase", "status", "updated_by_dealer");

		HashMap<Long, List<Item>> ordersByUser = new HashMap<>();
		Set<Item> dealers = new HashSet<>();

		for(Item order : updatedOrders){
			ItemQuery q = new ItemQuery("user_jur");
			q.setChildId(order.getId(), false);

			Item dealer = q.loadFirstItem();

			dealers.add(dealer);

			if(ordersByUser.containsKey(dealer.getId())){
				ordersByUser.get(dealer.getId()).add(order);
			}else{
				List<Item> orderList = new ArrayList<>();
				orderList.add(order);
				ordersByUser.put(dealer.getId(), orderList);
			}
		}



		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("order_changes", "from", DateDataType.outputDate(new Date().getTime()));

		for(Item dealer : dealers){
			doc.startElement("dealer");
			doc.addElement("uid", dealer.getValue("uid"));
			String login = StringUtils.isBlank(dealer.getStringValue("login"))? dealer.getStringValue("email") : dealer.getStringValue("login");
			doc.addElement("login", login);

			List<Item> orders = ordersByUser.get(dealer.getId());

			for(Item order : orders){
				buildOrder(order, doc);
			}

			doc.endElement();
		}

		doc.endElement();

		ResultPE res = getResult("complete");
		res.setValue(doc.toString());
		return res;
	}

	private void buildOrder(Item order, XmlDocumentBuilder doc) throws Exception {
		doc.startElement("order");
		doc.addElement("id", order.getValue("num"));
		doc.addElement("status", order.getValue("status"));
		doc.addElement("sum", order.getValue("sum_discount"));
		List<Item> children =  ItemQuery.loadByParentId(order.getId(), new Byte[]{ItemTypeRegistry.getPrimaryAssocId()});

		//Payments
		List<Item> payments = children.stream().filter(i -> i.getTypeName().equals("payment_stage")).collect(Collectors.toList());
		doc.startElement("payments");
		for(Item payment : payments){
			doc.startElement("payment");
			doc.addElement("date", payment.outputValue("date"));
			doc.addElement("sum", payment.getValue("sum"));
			doc.endElement();
		}
		doc.endElement();

		//boughts
		List<Item> boughts = children.stream().filter(i -> i.getTypeName().equals("bought")).collect(Collectors.toList());

		for(Item bought : boughts){
			doc.startElement("purchase");
			doc.addElement("id", bought.getValue("code"));
			doc.addElement("complect_name", bought.getValue("сomplectation_name"));
			doc.addElement("status", bought.getValue("status"));
			doc.addElement("proposed_dealer_date", bought.outputValue("proposed_dealer_date"));
			doc.addElement("deadline_date", bought.outputValue("deadline_date"));
			doc.endElement();
		}

		doc.endElement();
	}
}
