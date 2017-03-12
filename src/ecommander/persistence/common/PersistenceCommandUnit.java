package ecommander.persistence.common;


/**
 * Интерфейс, который определяет команды для работы с айтемами
 * Любая команда должна принадлежать какой-то транзакции
 * @author EEEE
 *
 */
public interface PersistenceCommandUnit {
	/**
	 * Получить контекст трназакции (например для получения подключения к базе)
	 * @return
	 */
	TransactionContext getTransactionContext();
	/**
	 * Установить контекст трзанакции
	 * @param context
	 */
	void setTransactionContext(TransactionContext context);
	/**
	 * Выполнить команду
	 * @throws Exception
	 */
	void execute() throws Exception;
	/**
	 * Откатить команду
	 * @throws Exception
	 */
	void rollback() throws Exception;
}