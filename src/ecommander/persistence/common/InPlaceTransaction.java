package ecommander.persistence.common;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.EcommanderException;
import ecommander.model.User;

/**
 * Класс, который отвечает за выполнение одной транзакции, а также за откат всех операций, если
 * транзакция не может быть выполнена
 * Отличается от простой DelayedTransaction тем, что внутри тразакции можно испольовать результаты,
 * полученные в этой же транзакции.
 * @author EEEE
 *
 */
public abstract class InPlaceTransaction {
	
	private static byte NUMBER_OF_TRIES = 2;

	private ArrayList<PersistenceCommandUnit> executedCommands;
	private TransactionContext context;
	private User initiator = null;
	/**
	 * Этот метод надо переопределять
	 */
	public abstract void performTransaction() throws Exception;
	
	public InPlaceTransaction(User initiator) {
		executedCommands = new ArrayList<>();
		this.initiator = initiator;
	}
	/**
	 * Возвращает контекст транзакции
	 * @return
	 */
	protected TransactionContext getTransactionContext() {
		return context;
	}
	/**
	 * Выполнить транзакцию
	 * @throws Exception
	 */
	public final void execute() throws Exception {
		Exception exception = null;
		for (int i = 0; i < NUMBER_OF_TRIES; i++) {
			try (Connection conn = MysqlConnector.getConnection();
			     MysqlConnector.AutoRollback committer = new MysqlConnector.AutoRollback(conn)
			) {
				ServerLogger.debug("Start transaction, try #" + (i + 1));
				conn.setAutoCommit(false);
				context = new TransactionContext(conn, initiator);
				performTransaction();
				committer.commit();
				ServerLogger.debug("Transaction successfull at try #" + (i + 1));
				// return not no make the exception
				return;
			} catch (Exception e) {
				exception = e;
				ServerLogger.error("Some error occured. Rolling back the transaction.\nHere's the error:", e);
				rollback();
			}
		}
		throw exception;
	}
	/**
	 * Пытается выполнить все команды
	 * 
	 * @throws Exception
	 */
	protected void executeCommandUnit(PersistenceCommandUnit command) throws Exception {
		try {
			command.setTransactionContext(context);
			command.execute();
			executedCommands.add(command);
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * Откатывает выполненные успешно команды
	 */
	private void rollback() {
		try {
			for (PersistenceCommandUnit command : executedCommands) {
				command.rollback();
			}
		} catch (Exception e) {
			ServerLogger.error("Transaction command rollback error", e);
		}
	}
}
