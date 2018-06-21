package extra;

import ecommander.fwk.ServerLogger;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.LongDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.CreateAssocDBUnit;
import ecommander.persistence.commandunits.DeleteAssocDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
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
			final byte managesId = ItemTypeRegistry.getAssocId(MANAGES_ASSOC);
			long purchaseId = Long.parseLong(getVarSingleValue("p"));
			String managerEmail = getVarSingleValue("manager");
			String statusString = getVarSingleValue("status");
			Item purch = ItemQuery.loadById(purchaseId);
			if (purch == null) {
				return getResult(ERROR).setVariable(MESSAGE, "Не найден требуемый заказ");
			}
			if (StringUtils.isNotBlank(managerEmail)) {
				// Загрузка и проверка нового менежера
				Item newOwnerItem = new ItemQuery(MANAGER).addParameterCriteria(manager.EMAIL, managerEmail, "=", null, Compare.SOME).loadFirstItem();
				if (newOwnerItem == null) {
					return getResult(ERROR).setVariable(MESSAGE, "Некорректный логин менеджера");
				}
				// Загрузка пользователя заказа
				Item purchaseUser = new ItemQuery(USER).setChildId(purchaseId, false, ItemTypeRegistry.getPrimaryAssoc().getName()).loadFirstItem();
				if (purchaseUser == null) {
					return getResult(ERROR).setVariable(MESSAGE, "Некорректный заказ, отсутствует заказчик");
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
			} else if (StringUtils.isNotBlank(statusString)) {
				purch.setValueUI(purchase.STATUS, statusString);
				executeAndCommitCommandUnits(SaveItemDBUnit.get(purch));
				return getResult(SUCCESS).setVariable(MESSAGE, "Статус заказа изменен");
			}
			return getResult(ERROR).setVariable(MESSAGE, "Не передана необходимая для выполения действия информация");
		} catch (Exception e) {
			ServerLogger.error("Error managing purchase", e);
		}

		return getResult(ERROR).setVariable(MESSAGE, "Ошибка изменения заказа");
	}
}
