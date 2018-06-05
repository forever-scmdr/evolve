package ecommander.persistence.common;

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
public final class SynchronousTransaction implements AutoCloseable {

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
		boolean inited = false;
		try {
			ServerLogger.debug("Start syncronous transaction");
			executedCommands = new ArrayList<>();
			conn = MysqlConnector.getConnection();
			conn.setAutoCommit(false);
			context = new TransactionContext(conn, initiator);
			inited = true;
		} catch (Exception e) {
			ServerLogger.error("Can not start the thransaction.\nHere's the error:", e);
			throw e;
		} finally {
			if (!inited)
				close();
		}
	}
	/**
	 * Завершить выполнение (комитить)
	 * @throws Exception
	 */
	public final void commit() throws Exception {
		if (conn == null)
			return;
		Exception exception;
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
			close();
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
		boolean executed = false;
		try {
			command.setTransactionContext(context);
			command.execute();
			executedCommands.add(command);
			executed = true;
		} finally {
			if (!executed)
				rollback();
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
			if (executedCommands != null) {
				for (PersistenceCommandUnit command : executedCommands) {
					command.rollback();
				}
			}
		} finally {
			close();
		}
	}

	/**
	 * Установить нового пользователя, от имени которого будет выполняться транзакция
	 * @param newInitiator
	 */
	public void switchInitiator(User newInitiator) {
		this.initiator = newInitiator;
	}

	@Override
	public void close() throws Exception {
		MysqlConnector.closeConnection(conn);
		conn = null;
	}
}
