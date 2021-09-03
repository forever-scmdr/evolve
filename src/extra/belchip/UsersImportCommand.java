package extra.belchip;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.XmlDataSource;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class UsersImportCommand extends IntegrateBase implements ItemNames {

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
		XmlDataSource.Node userNode = null;
		String tag = "register_jur";
		ItemType itemType = ItemTypeRegistry.getItemType(USER_JUR);
		info.setProcessed(0);
		info.setCurrentJob("Обработка юридических лиц");
		Item userCatalog = ItemUtils.ensureSingleRootAnonymousItem(REGISTERED_CATALOG, getInitiator());
		do {
			userNode = xml.findNextNode(tag);
			if (userNode == null) {
				if (StringUtils.equalsIgnoreCase(tag, "register_phys")) {
					info.addLog("Интеграция завершена");
					return;
				}
				tag = "register_phys";
				itemType = ItemTypeRegistry.getItemType(USER_PHYS);
				xml.finishDocument();
				xml = new XmlDataSource(file.getAbsolutePath(), StandardCharsets.UTF_8);
				userNode = xml.findNextNode(tag);
				info.setCurrentJob("Обработка физических лиц");
			}
			if (userNode == null) {
				info.addLog("Интеграция завершена");
				return;
			}

			userNode = xml.scanCurrentNode();
			Document doc = userNode.getDoc();
			Item user = Item.newChildItem(itemType, userCatalog);
			String login = null;
			for (Element el : doc.getElementsByTag(tag).first().children()) {
				if (itemType.hasParameter(el.tagName())) {
					user.setValueUI(el.tagName(), StringUtils.normalizeSpace(el.ownText()));
				} else if (StringUtils.equalsIgnoreCase("login", el.tagName())) {
					login = StringUtils.normalizeSpace(el.ownText());
				}
			}
			user.setValue(user_.OLD_LOGIN, login);
			String caption = StringUtils.joinWith(" ",
					login,
					user.getStringValue(user_phys_.NAME),
					user.getStringValue(user_jur_.ORGANIZATION));
			if (user.isValueEmpty(user_.EMAIL) && StringUtils.isNotBlank(login)) {
				info.addLog("У пользователя " + caption + " не указан email. Используется логин", "");
				user.setValue(user_.EMAIL, login);
			}
			String email = StringUtils.substring(user.getStringValue(user_.EMAIL), 0, 98);
			user.setValue(user_.EMAIL, email);
			if (user.isValueEmpty(user_.EMAIL)) {
				info.addLog("У пользователя " + caption + " не указан email и логин. Пользователь не перенесен", "");
				info.increaseProcessed();
				continue;
			}
			if (user.isValueEmpty(user_.PASSWORD)) {
				info.addError("У пользователя " + caption + " не указан пароль. Пароль по умолчанию 12345", "");
				user.setValue(user_.PASSWORD, "12345");
			}
			user.setValue(user_.REGISTERED, (byte)1);
			Item existingUser = ItemQuery.loadSingleItemByParamValue(USER, user_.EMAIL, user.getStringValue(user_.EMAIL));
			if (existingUser != null) {
				info.addLog("Пользователь " + user.getStringValue(user_.EMAIL) + " существует, пропускаем");
				info.increaseProcessed();
				continue;
			}
			executeAndCommitCommandUnits(SaveItemDBUnit.get(user).noFulltextIndex());
			info.increaseProcessed();
		} while (true);
	}

	@Override
	protected void terminate() throws Exception {

	}
}
