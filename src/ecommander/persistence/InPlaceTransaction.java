package ecommander.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ecommander.common.MysqlConnector;
import ecommander.common.ServerLogger;
import ecommander.common.exceptions.EcommanderException;
import ecommander.users.User;

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
		executedCommands = new ArrayList<PersistenceCommandUnit>();
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
		Connection conn = null;
		Exception exception = null;
//		final String commitSql = "COMMIT";
//		final String rollbackSql = "ROLLBACK";
		for (int i = 0; i < NUMBER_OF_TRIES; i++) {
			Statement stmt = null;
			try {
				ServerLogger.debug("Start transaction, try #" + (i + 1));
				conn = MysqlConnector.getConnection();
				stmt = conn.createStatement();
				context = new TransactionContext(conn, initiator);
				conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				conn.setAutoCommit(false);
//				String beginTransactionSql = "SET SESSION TRANSACTION ISOLATION LEVEL READ UNCOMMITTED;\r\n" 
//					+ "START TRANSACTION";
//				ServerLogger.debug(beginTransactionSql);
//				stmt.execute(beginTransactionSql);
				performTransaction();
//				ServerLogger.debug(commitSql);
//				stmt.execute(commitSql);
				conn.commit();
				MysqlConnector.closeConnection(conn);
				ServerLogger.debug("Transaction successfull at try #" + (i + 1));
				// return not no make the exception
				return;
			} catch (EcommanderException e) {
				if (conn != null) {
					try {
						conn.rollback();
//						ServerLogger.debug(rollbackSql);
//						stmt.execute(rollbackSql);
						rollback();
						MysqlConnector.closeConnection(conn);
					} catch (SQLException sqlE) {
						ServerLogger.error("SQL Exception during rolling back the transaction.", sqlE);
					}
				}
				throw e;
			} catch (Exception e) {
				ServerLogger.error("Some error occured. Rolling back the transaction.\nHere's the error:", e);
				exception = e;
				if (conn != null) {
					try {
						conn.rollback();
//						ServerLogger.debug(rollbackSql);
//						stmt.execute(rollbackSql);
						rollback();
						MysqlConnector.closeConnection(conn);
					} catch (SQLException sqlE) {
						ServerLogger.error("SQL Exception during rolling back the transaction.", sqlE);
					}
				}
			} finally {
				MysqlConnector.closeStatement(stmt);
				MysqlConnector.closeConnection(conn);
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
