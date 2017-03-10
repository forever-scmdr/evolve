package ecommander.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.EcommanderException;
import ecommander.model.User;

/**
 * Класс, который отвечает за выполнение одной транзакции, а также за откат всех операций, если
 * транзакция не может быть выполнена
 * @author EEEE
 *
 */
public class DelayedTransaction {
	
	private static byte NUMBER_OF_TRIES = 2;

	private LinkedList<PersistenceCommandUnit> commands;
	private TransactionContext context;
	private int failedCommandIndex = 0;
	private boolean finished = false;
	private User initiator = null;

	public DelayedTransaction(User initiator) {
		commands = new LinkedList<PersistenceCommandUnit>();
		this.initiator = initiator;
	}
	/**
	 * Добавить команду в конец списка, для последующего ее выполнения
	 * 
	 * @param command
	 */
	public void addCommandUnit(PersistenceCommandUnit command) {
		refresh();
		commands.add(command);
	}
	/**
	 * Добавить команду в начало списка, для последующего ее выполнения
	 * 
	 * @param command
	 */
	public void pushCommandUnit(PersistenceCommandUnit command) {
		refresh();
		commands.push(command);
	}	
	
	private void refresh() {
		if (finished) {
			commands = new LinkedList<PersistenceCommandUnit>();
			finished = false;
		}
	}
	/**
	 * Выполнить транзакцию
	 * @throws Exception
	 */
	public int execute() throws Exception {
		if (finished)
			return 0;
		Connection conn = null;
		Exception exception = null;
		for (int i = 0; i < NUMBER_OF_TRIES; i++) {
			try {
				ServerLogger.debug("Start transaction, try #" + (i + 1));
				conn = MysqlConnector.getConnection();
				conn.setAutoCommit(false);
				context = new TransactionContext(conn, initiator);
				executeCommands();
				conn.commit();
				MysqlConnector.closeConnection(conn);
				ServerLogger.debug("Transaction successfull at try #" + (i + 1));
				finished = true;
				// return not no make the exception
				return commands.size();
			} catch (EcommanderException e) {
				exception = e;
				ServerLogger.error("Some error occured. Rolling back the transaction.\nHere's the error:", e);
				if (conn != null) {
					try {
						rollback();
						conn.rollback();
						MysqlConnector.closeConnection(conn);
					} catch (SQLException sqlE) {
						ServerLogger.error("SQL Exception during rolling back the transaction.", sqlE);
					}
				}
			} catch (Exception e) {
				exception = e;
				ServerLogger.error("Some error occured. Rolling back the transaction.\nHere's the error:", e);
				if (conn != null) {
					try {
						rollback();
						conn.rollback();
						MysqlConnector.closeConnection(conn);
					} catch (SQLException sqlE) {
						ServerLogger.error("SQL Exception during rolling back the transaction.", sqlE);
					}
				}
			} finally {
				MysqlConnector.closeConnection(conn);
			}
		}
		if (exception != null) throw exception;
		return 0;
	}
	/**
	 * Сколько команд добавлено в транзакцию
	 * @return
	 */
	public int getCommandCount() {
		return commands.size();
	}
	/**
	 * Пытается выполнить все команды
	 * 
	 * @throws Exception
	 */
	protected void executeCommands() throws Exception {
		try {
			failedCommandIndex = 0;
			for (PersistenceCommandUnit command : commands) {
				failedCommandIndex++;
				command.setTransactionContext(context);
				command.execute();
			}
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * Откатывает выполненные успешно команды
	 */
	protected void rollback() {
		try {
			for (int i = 0; i < failedCommandIndex; i++) {
				commands.get(i).rollback();
			}
		} catch (Exception e) {
			ServerLogger.error("Transaction command rollback error", e);
		}
	}
}
