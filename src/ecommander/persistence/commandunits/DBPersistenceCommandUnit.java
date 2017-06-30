package ecommander.persistence.commandunits;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

import ecommander.fwk.UserNotAllowedException;
import ecommander.model.*;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.common.TransactionContext;
import ecommander.persistence.mappers.DBConstants;

import javax.naming.NamingException;

/**
 * Абстрактный класс для команд базы данных
 * @author EEEE
 *
 */
public abstract class DBPersistenceCommandUnit implements PersistenceCommandUnit, DBConstants.ItemTbl, DBConstants.ItemParent, DBConstants.ComputedLog {
	
	protected TransactionContext context;
	boolean ignoreUser = false;
	boolean ignoreFileErrors = false;
	boolean insertIntoFulltextIndex = true;
	boolean closeLuceneWriter = true;
	boolean processComputed = true;
	private ArrayList<PersistenceCommandUnit> executedCommands;
	
	public TransactionContext getTransactionContext() {
		return context;
	}
	/**
	 * При работе с базой данных здесь ничего не должно происходить
	 */
	protected final void rollbackCommands() throws Exception {
		if (executedCommands != null)
			for (PersistenceCommandUnit commandUnit : executedCommands) {
				commandUnit.rollback();
			}
	}
	/**
	 * Реализация по умолчанию
	 */
	public void rollback() throws Exception {
		rollbackCommands();
	}
	/**
	 * Получает подключение
	 */
	public void setTransactionContext(TransactionContext context) {
		this.context = context;
	}
	/**
	 * Если команда была вызвана для выполнения действий с айтемом, который не
	 * принадлежит текущему пользователю (тому, который вызвал команду), то выбрасывается 
	 * исключение UserNotAllowedException
	 * 
	 * Иногда надо игнорировать это исключение, т. е. все-таки выполнять действия с айтемами, которые
	 * не принадлежат пользователю. В таком случае можно установить атрибут ignoreUser = true
	 * 
	 * @return
	 */
	public DBPersistenceCommandUnit ignoreUser() {
		this.ignoreUser = true;
		return this;
	}
	/**
	 * Можно ли ингорировать ошибки, связанные с файлами, принадлежащими айтему.
	 * Иногда такие ошибки можно игнорировать. По умолчанию - false
	 * @return
	 */
	public DBPersistenceCommandUnit ignoreFileErrors() {
		this.ignoreFileErrors = true;
		return this;
	}

	/**
	 * Не включать в список обновления computed параметров айтемы, computed-параметры которых должны быть
	 * обновлены в связи с действиями команды
	 * @return
	 */
	public DBPersistenceCommandUnit ingoreComputed() {
		this.processComputed = false;
		return this;
	}
	/**
	 * Надо ли добавлять айтем в полнотекстовый индекс.
	 * Иногда добавлять надо сразу, иногда потом.
	 * @return
	 */
	public DBPersistenceCommandUnit noFulltextIndex() {
		this.insertIntoFulltextIndex = false;
		this.closeLuceneWriter = false;
		return this;
	}

	/**
	 * Не закрывать райтер индекса Lucene после добавления в него результатов выполнения этой команды
	 * @return
	 */
	public DBPersistenceCommandUnit dontCloseFulltextIndexWriter() {
		this.closeLuceneWriter = false;
		return this;
	}

	protected final void executeCommand(PersistenceCommandUnit commandUnit) throws Exception {
		if (executedCommands == null)
			executedCommands = new ArrayList<>();
		commandUnit.setTransactionContext(context);
		commandUnit.execute();
		executedCommands.add(commandUnit);
	}

	/**
	 * Выполнить команду с такими же настройками, как и у вызывающей команды.
	 * Настройки - записывать в полнотекстовый индекс, закрывать полнотекстовый индекс после записи,
	 * игнорировать права пользователя, игнорировать файловые ошибки
	 * @param command
	 * @throws Exception
	 */
	protected final void executeCommandInherited(PersistenceCommandUnit command) throws Exception {
		if (command != null) {
			if (command instanceof DBPersistenceCommandUnit) {
				((DBPersistenceCommandUnit) command).insertIntoFulltextIndex = insertIntoFulltextIndex;
				((DBPersistenceCommandUnit) command).closeLuceneWriter = closeLuceneWriter;
				((DBPersistenceCommandUnit) command).ignoreUser = ignoreUser;
				((DBPersistenceCommandUnit) command).ignoreFileErrors = ignoreFileErrors;
			}
			executeCommand(command);
		}
	}
	/**
	 * Проверка, можно ли текущему пользователю выполнять действия с заданным айтемом
	 * @param item
	 * @throws UserNotAllowedException 
	 */
	protected final void testPrivileges(ItemBasics item) throws UserNotAllowedException {
		if (ignoreUser || item == null)
			return;
		if (context == null)
			throw new IllegalStateException("Can not test user privileges against item before transaction context is set");
		User admin = context.getInitiator();
		// Если айтем персональный
		Security.testPrivileges(admin, item);
	}

	/**
	 * Проверка, можно ли текущему пользователю выполнять заданные действия с другип пользователем
	 * @param user
	 * @param justGroups - только изменение принадлежности к группам (не удаление или изменение пароля)
	 * @throws SQLException
	 * @throws NamingException
	 * @throws UserNotAllowedException
	 */
	protected final void testPrivileges(User user, boolean justGroups) throws SQLException, NamingException, UserNotAllowedException {
		if (ignoreUser || user == null)
			return;
		if (context == null)
			throw new IllegalStateException("Can not test user privileges against another user before transaction context is set");
		User admin = context.getInitiator();
		// Для того, чтобы проверить, что поменял админ, нужно сначала загрузить старого пользователя
		User oldUser = UserMapper.getUser(user.getUserId(), getTransactionContext().getConnection());
		HashSet<String> adminGroups = new HashSet<>();
		for (User.Group group : admin.getGroups()) {
			if (admin.isAdmin(group.name))
				adminGroups.add(group.name);
		}
		HashSet<String> oldUserGroups = new HashSet<>();
		for (User.Group group : oldUser.getGroups()) {
			oldUserGroups.add(group.name);
		}
		HashSet<String> newUserGroups = new HashSet<>();
		for (User.Group group : user.getGroups()) {
			newUserGroups.add(group.name);
		}
		if (justGroups) {
			HashSet<String> addedGroups = new HashSet<>(newUserGroups);
			addedGroups.removeAll(oldUserGroups);
			HashSet<String> removedGroups = new HashSet<>(oldUserGroups);
			removedGroups.removeAll(newUserGroups);
			if (!adminGroups.containsAll(addedGroups) || !addedGroups.containsAll(removedGroups)) {
				throw new UserNotAllowedException("Action is not allowed to user " + admin.getName());
			}
		} else {
			if (!adminGroups.containsAll(newUserGroups) || !adminGroups.containsAll(oldUserGroups)) {
				throw new UserNotAllowedException("Action is not allowed to user " + admin.getName());
			}
		}
	}

	/**
	 * Добавить в очередь обновления computed-айтемов всех предшественников заданного айтема с заданными
	 * ассоциациями
	 * @param itemId
	 * @param assocId
	 * @throws SQLException
	 */
	protected final void addItemPredecessorsToComputedLog(long itemId, Byte... assocId) throws SQLException {
		if (processComputed && ItemTypeRegistry.hasComputedItems()) {
			TemplateQuery logInsert = new TemplateQuery("Insert into update log");
			logInsert
					.INSERT_INTO(COMPUTED_LOG_TBL, L_ITEM)
					.SELECT(I_ID).FROM(ITEM_TBL).INNER_JOIN(ITEM_PARENT_TBL, I_ID, IP_PARENT_ID)
					.WHERE().col(IP_CHILD_ID).setLong(itemId)
					.AND().col(IP_ASSOC_ID, " IN").byteArrayIN(assocId)
					.AND().col(I_SUPERTYPE, " IN").intArrayIN(ItemTypeRegistry.getAllComputedSupertypes())
					.ON_DUPLICATE_KEY_UPDATE(L_ITEM).sql(L_ITEM);

			try(PreparedStatement pstmt = logInsert.prepareQuery(getTransactionContext().getConnection())) {
				pstmt.executeUpdate();
			}
		}
	}
}