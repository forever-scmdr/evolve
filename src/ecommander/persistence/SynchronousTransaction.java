package ecommander.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.model.User;

/**
 * Класс, который отвечает за выполнение одной транзакции, а также за откат всех операций, если
 * транзакция не может быть выполнена
 * Отличается от простой DelayedTransaction тем, что внутри тразакции можно испольовать результаты,
 * полученные в этой же транзакции.
 * @author EEEE
 */
public final class SynchronousTransaction {

	private ArrayList<PersistenceCommandUnit> executedCommands;
	private TransactionContext context;
	private User initiator = null;
	private Connection conn = null;
	
	public SynchronousTransaction(User initiator) {
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
	private void startTransaction() throws Exception {
		try {
			ServerLogger.debug("Start syncronous transaction");
			executedCommands = new ArrayList<PersistenceCommandUnit>();
			conn = MysqlConnector.getConnection();
			conn.setAutoCommit(false);
			context = new TransactionContext(conn, initiator);
		} catch (Exception e) {
			ServerLogger.error("Can not start the thransaction.\nHere's the error:", e);
			MysqlConnector.closeConnection(conn);
			conn = null;
			throw e;
		}
	}
	/**
	 * Завершить выполнение (комитить)
	 * @throws Exception
	 */
	public final void commit() throws Exception {
		if (conn == null)
			return;
		Exception exception = null;
		try {
			conn.commit();
			ServerLogger.debug("Transaction successfull");
			return;
		} catch (Exception e) {
			ServerLogger.error("Some error occured. Rolling back the transaction.\nHere's the error:", e);
			exception = e;
			try {
				rollback();
			} catch (SQLException sqlE) {
				ServerLogger.error("SQL Exception during rolling back the transaction.", sqlE);
			}
		} finally {
			MysqlConnector.closeConnection(conn);
			conn = null;
		}
		throw exception;
	}
	/**
	 * Пытается выполнить все команды
	 * 
	 * @throws Exception
	 */
	public final void executeCommandUnit(PersistenceCommandUnit command) throws Exception {
		if (conn == null)
			startTransaction();
		try {
			command.setTransactionContext(context);
			command.execute();
			executedCommands.add(command);
		} catch (Exception e) {
			rollback();
			throw e;
		}
	}
	/**
	 * Откатывает выполненные успешно команды
	 * @throws Exception 
	 */
	public final void rollback() throws Exception {
		try {
			if (conn != null)
				conn.rollback();
			for (PersistenceCommandUnit command : executedCommands) {
				command.rollback();
			}
		} finally {
			MysqlConnector.closeConnection(conn);
			conn = null;
		}
	}
	/**
	 * Завершение выполнения транзакции
	 * !!!   ВСЕГДА   !!! вызывать после выполнения в блоке finally,
	 * т. к. в этом методе выполняется закрытие соединения
	 */
	public final void finalize() {
		MysqlConnector.closeConnection(conn);
	}
}
