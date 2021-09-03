package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.XmlDataSource;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.MathUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class PurchasesImportCommand extends IntegrateBase implements ItemNames {

	private static HashMap<String, String> PARAM_MAP = new HashMap<>();
	static {
		PARAM_MAP.put("total_sum", purchase_.SUM);
		PARAM_MAP.put("total_simple_sum", purchase_.SIMPLE_SUM);
		PARAM_MAP.put("number", purchase_.NUM);
	}

	private File file;

	@Override
	protected boolean makePreparations() throws Exception {
		file = new File(AppContext.getRealPath(getVarSingleValue("file")));
		if (!file.exists()) {
			info.addError("No user file found", "");
			return false;
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		XmlDataSource xml = new XmlDataSource(file.getAbsolutePath(), StandardCharsets.UTF_8);
		XmlDataSource.Node orderNode = null;
		ItemType itemType = ItemTypeRegistry.getItemType(PURCHASE);
		Item userCatalog = ItemUtils.ensureSingleRootAnonymousItem(REGISTERED_CATALOG, getInitiator());
		ItemType jurType = ItemTypeRegistry.getItemType(USER_JUR);
		ItemType physType = ItemTypeRegistry.getItemType(USER_PHYS);
		info.setProcessed(0);
		info.setCurrentJob("Обработка заказов");
		do {
			orderNode = xml.findNextNode("order");
			if (orderNode == null) {
				info.addLog("Интеграция завершена");
				xml.finishDocument();
				return;
			}
			orderNode = xml.scanCurrentNode();
			Document doc = orderNode.getDoc();
			String login = null, email = null;
			Elements logins = doc.getElementsByTag("login");
			Elements emails = doc.getElementsByTag("email");

			// Проверки заполенности

			if (emails != null && !emails.isEmpty())
				email = StringUtils.normalizeSpace(emails.first().ownText());
			if (logins != null && !logins.isEmpty())
				login = StringUtils.normalizeSpace(logins.first().ownText());
			if (StringUtils.isAllBlank(email, login)) {
				info.addError("У заказа " + doc.attr("id") + " отсутствует получатель", "");
				info.increaseProcessed();
				continue;
			}

			// Проверки пользователя

			Item user = null;
			if (StringUtils.isNotBlank(email)) {
				user = ItemQuery.loadSingleItemByParamValue(USER, user_.EMAIL, email);
			}
			if (user == null && StringUtils.isNotBlank(login)) {
				user = ItemQuery.loadSingleItemByParamValue(USER, user_.OLD_LOGIN, login);
				if (user == null)
					user = ItemQuery.loadSingleItemByParamValue(USER, user_.EMAIL, login);
			}
			if (user == null) {
				info.addLog("Не найден пользователь {} {}. Создание анонимного пользователя", email, login);
				Elements phys = doc.getElementsByTag("phys");
				ItemType type = phys.size() > 0 && StringUtils.equalsIgnoreCase("0", StringUtils.normalizeSpace(phys.get(0).ownText())) ? jurType : physType;
				user = Item.newChildItem(type, userCatalog);
				String newEmail = StringUtils.isNotBlank(email) ? email : login;
				user.setValueUI(user_.EMAIL, newEmail);
				if (StringUtils.isNotBlank(login))
					user.setValueUI(user_.OLD_LOGIN, login);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(user).ignoreUser());
			}

			// Создание и заполнение заказа

			Item purcahse = Item.newChildItem(itemType, user);
			for (Element el : doc.getElementsByTag("order").first().children()) {
				if (itemType.hasParameter(el.tagName())) {
					purcahse.setValueUI(el.tagName(), StringUtils.normalizeSpace(el.ownText()));
				} else if (PARAM_MAP.containsKey(el.tagName())) {
					purcahse.setValueUI(PARAM_MAP.get(el.tagName()), StringUtils.normalizeSpace(el.ownText()));
				}
			}

			// Удаление старого заказа с таким номером

			ArrayList<Item> oldPurchases = new ArrayList<>();
			if (!purcahse.isValueEmpty(purchase_.INT_NUMBER)) {
				oldPurchases = ItemQuery.loadByParamValue(PURCHASE, purchase_.INT_NUMBER, purcahse.outputValue(purchase_.INT_NUMBER));
			}
			if (oldPurchases.isEmpty() && !purcahse.isValueEmpty(purchase_.NUM)) {
				oldPurchases = ItemQuery.loadByParamValue(PURCHASE, purchase_.NUM, purcahse.outputValue(purchase_.NUM));
			}
			for (Item oldPurchase : oldPurchases) {
				executeAndCommitCommandUnits(ItemStatusDBUnit.delete(oldPurchase).ignoreUser());
			}

			// Сохранение заказа

			executeAndCommitCommandUnits(SaveItemDBUnit.get(purcahse).noFulltextIndex().ignoreUser());

			// Создание и заполнение товаров

			Elements codes = doc.getElementsByTag("code");
			Elements names = doc.getElementsByTag("name");
			Elements prices = doc.getElementsByTag("price");
			Elements totalQtys = doc.getElementsByTag("total_quantity");
			Elements qtys = doc.getElementsByTag("quantity");
			Elements zeroQtys = doc.getElementsByTag("zero_quantity");
			Elements sums = doc.getElementsByTag("sum");

			ItemType boughtType = ItemTypeRegistry.getItemType(BOUGHT);
			for (int i = 0; i < codes.size(); i++) {
				Item bought = Item.newChildItem(boughtType, purcahse);
				bought.setValueUI(bought_.CODE, StringUtils.normalizeSpace(codes.get(i).ownText()));
				if (i < names.size())
					bought.setValueUI(bought_.NAME, StringUtils.normalizeSpace(names.get(i).ownText()));
				if (i < prices.size())
					bought.setValueUI(bought_.PRICE, StringUtils.normalizeSpace(prices.get(i).ownText()));
				if (i < totalQtys.size())
					bought.setValueUI(bought_.QTY_TOTAL, StringUtils.normalizeSpace(totalQtys.get(i).ownText()));
				if (i < qtys.size())
					bought.setValueUI(bought_.QTY_AVAIL, StringUtils.normalizeSpace(qtys.get(i).ownText()));
				if (i < zeroQtys.size())
					bought.setValueUI(bought_.QTY_ZERO, StringUtils.normalizeSpace(zeroQtys.get(i).ownText()));
				if (i < sums.size())
					bought.setValueUI(bought_.SUM, StringUtils.normalizeSpace(sums.get(i).ownText()));
				executeAndCommitCommandUnits(SaveItemDBUnit.get(bought).noFulltextIndex().ignoreUser());
			}

			info.increaseProcessed();
		} while (true);
	}

	@Override
	protected void terminate() throws Exception {

	}
}
