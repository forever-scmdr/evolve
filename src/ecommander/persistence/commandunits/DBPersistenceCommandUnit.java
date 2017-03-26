package ecommander.persistence.commandunits;

import java.util.ArrayList;

import ecommander.fwk.UserNotAllowedException;
import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;
import ecommander.model.User;

/**
 * Абстрактный класс для команд базы данных
 * @author EEEE
 *
 */
public abstract class DBPersistenceCommandUnit implements PersistenceCommandUnit {
	
	protected TransactionContext context;
	boolean ignoreUser = false;
	boolean ignoreFileErrors = false;
	boolean insertIntoFulltextIndex = true;
	boolean closeLuceneWriter = true;
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
	 * @param ignore
	 * @return
	 */
	public DBPersistenceCommandUnit ignoreUser(boolean ignore) {
		this.ignoreUser = ignore;
		return this;
	}
	/**
	 * Можно ли ингорировать ошибки, связанные с файлами, принадлежащими айтему.
	 * Иногда такие ошибки можно игнорировать. По умолчанию - false
	 * @param ignore
	 * @return
	 */
	public DBPersistenceCommandUnit ignoreFileErrors(boolean ignore) {
		this.ignoreFileErrors = ignore;
		return this;
	}
	/**
	 * Надо ли добавлять айтем в полнотекстовый индекс.
	 * Иногда добавлять надо сразу, иногда потом.
	 * Второй параметр - закрывать ли Writer Lucene (запись в полнотекстовый индекс) после выполнения команды.
	 * Если выполняется блок команд, то закрывание-открывание райтера начинает занимать много времени.
	 * @param fulltextIndex
	 * @param closeWriter
	 * @return
	 */
	public DBPersistenceCommandUnit fulltextIndex(boolean fulltextIndex, boolean... closeWriter) {
		this.insertIntoFulltextIndex = fulltextIndex;
		if (closeWriter.length > 0)
			this.closeLuceneWriter = closeWriter[0];
		return this;
	}
	
	protected final void executeCommand(PersistenceCommandUnit commandUnit) throws Exception {
		if (executedCommands == null)
			executedCommands = new ArrayList<PersistenceCommandUnit>();
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
				((DBPersistenceCommandUnit) command).fulltextIndex(insertIntoFulltextIndex, closeLuceneWriter);
				((DBPersistenceCommandUnit) command).ignoreUser(ignoreUser);
				((DBPersistenceCommandUnit) command).ignoreFileErrors(ignoreFileErrors);
			}
			executeCommand(command);
		}
	}
	/**
	 * Проверка, можно ли текущему пользователю выполнять действия с заданным айтемом
	 * Если пользователь 
	 * @param item
	 * @throws UserNotAllowedException 
	 */
	protected final void testPrivileges(Item item) throws UserNotAllowedException {
		if (ignoreUser || item == null)
			return;
		if (context == null)
			throw new IllegalStateException("Can not test user privileges against item before transaction context is set");
		User user = context.getInitiator();
		// Если айтем персональный
		if (item.isPersonal()) {
			if (item.getOwnerUserId() != user.getUserId())
				throw new UserNotAllowedException();
		// Если айтем общий (нет владельца)
		} else {
			if ((item.getOwnerGroupId() != user.getGroupId()) && !user.isSuperUser())
				throw new UserNotAllowedException();
		}
	}
}