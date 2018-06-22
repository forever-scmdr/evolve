package extra;

import ecommander.fwk.ServerLogger;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DeleteAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by E on 21/6/2018.
 */
public class ManagePurchase extends Command implements ItemNames {
	private final String MANAGES_ASSOC = "manages";
	private final String ERROR = "error";
	private final String SUCCESS = "success";
	private final String MESSAGE = "message";

	@Override
	public ResultPE execute() throws Exception {
		try {
			String purchaseIdStr = getVarSingleValue("p");
			String userIdStr = getVarSingleValue("u");
			if (StringUtils.isNotBlank(purchaseIdStr)) {
				long purchaseId = Long.parseLong(purchaseIdStr);
				String managerEmail = getVarSingleValue("manager");
				String statusString = getVarSingleValue("status");
				Item purch = ItemQuery.loadById(purchaseId);
				if (purch == null) {
					return getResult(ERROR).setVariable(MESSAGE, "Не найден требуемый заказ");
				}
				if (StringUtils.isNotBlank(managerEmail)) {
					// Загрузка пользователя заказа
					Item purchaseUser = new ItemQuery(USER).setChildId(purchaseId, false, ItemTypeRegistry.getPrimaryAssoc().getName()).loadFirstItem();
					if (purchaseUser == null) {
						return getResult(ERROR).setVariable(MESSAGE, "Некорректный заказ, отсутствует заказчик");
					}
					return setNewManager(purchaseUser, managerEmail);
				} else if (StringUtils.isNotBlank(statusString)) {
					purch.setValueUI(purchase.STATUS, statusString);
					executeAndCommitCommandUnits(SaveItemDBUnit.get(purch));
					return getResult(SUCCESS).setVariable(MESSAGE, "Статус заказа изменен");
				}
			}
			else if (StringUtils.isNotBlank(userIdStr)) {
				long userId = Long.parseLong(userIdStr);
				Item user = ItemQuery.loadById(userId);
				if (user == null) {
					return getResult(ERROR).setVariable(MESSAGE, "Не найден требуемый пользователь");
				}
				return setNewManager(user, getVarSingleValue("manager"));
			}
			return getResult(ERROR).setVariable(MESSAGE, "Не передана необходимая для выполения действия информация");
		} catch (Exception e) {
			ServerLogger.error("Error managing purchase", e);
		}

		return getResult(ERROR).setVariable(MESSAGE, "Ошибка изменения заказа");
	}

	/**
	 * Установить нового менеждера для заказчика
	 * @param purchaseUser
	 * @param managerEmail
	 * @return
	 * @throws Exception
	 */
	private ResultPE setNewManager(Item purchaseUser, String managerEmail) throws Exception {
		final byte managesId = ItemTypeRegistry.getAssocId(MANAGES_ASSOC);
		// Загрузка и проверка нового менежера
		Item newOwnerItem = new ItemQuery(MANAGER).addParameterCriteria(manager.EMAIL, managerEmail, "=", null, Compare.SOME).loadFirstItem();
		if (newOwnerItem == null) {
			return getResult(ERROR).setVariable(MESSAGE, "Некорректный логин менеджера");
		}
		// Загрузка для удаления старого менеджера заказчика
		Item oldOwnerItem = new ItemQuery(MANAGER).setChildId(purchaseUser.getId(), false, MANAGES_ASSOC).loadFirstItem();
		// Если старый и новый менеджеры одинаковые, ничего делать не надо
		if (oldOwnerItem != null && newOwnerItem.getId() == oldOwnerItem.getId()) {
			return getResult(SUCCESS).setVariable(MESSAGE, "Изменение менеджера не требуется");
		}
		// Удаление старого менеджера
		if (oldOwnerItem != null) {
			executeCommandUnit(new DeleteAssocDBUnit(purchaseUser, oldOwnerItem, managesId));
		}
		// Прикрепление нового менеджера
		executeCommandUnit(new CreateAssocDBUnit(purchaseUser, newOwnerItem, managesId, false));
		commitCommandUnits();
		return getResult(SUCCESS).setVariable(MESSAGE, "Заказчику присвоен новый менеджер");
	}
}
