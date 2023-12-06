package ecommander.persistence.common;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Timer;
import ecommander.model.User;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;

import java.sql.Connection;
import java.util.LinkedList;

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
	private Timer[] timerArg; // внешний таймер для отладки времени выполнения команд

	public DelayedTransaction(User initiator, Timer... timer) {
		commands = new LinkedList<>();
		this.initiator = initiator;
		this.timerArg = timer;
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
			commands = new LinkedList<>();
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
		Exception exception = null;
		for (int i = 0; i < NUMBER_OF_TRIES; i++) {
			try (Connection conn = MysqlConnector.getConnection();
			     MysqlConnector.AutoRollback committer = new MysqlConnector.AutoRollback(conn)
			) {
				ServerLogger.debug("Start transaction, try #" + (i + 1));
				conn.setAutoCommit(false);
				context = new TransactionContext(conn, initiator, timerArg);
				executeCommands();
				context.getTimer().start("transaction commit");
				committer.commit();
				context.getTimer().stop("transaction commit");
				if (conn instanceof MysqlConnector.LoggedConnection) {
					((MysqlConnector.LoggedConnection) conn).queryFinished();
				}
				ServerLogger.debug("Transaction successfull at try #" + (i + 1));
				finished = true;
				// return not no make the exception
				return commands.size();
			} catch (Exception e) {
				exception = e;
				ServerLogger.error("Some error occured. Rolling back the transaction.\nHere's the error:", e);
				rollback();
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
				if (context.getConnection() instanceof MysqlConnector.LoggedConnection) {
					((MysqlConnector.LoggedConnection) context.getConnection()).queryFinished();
				}
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

	/**
	 * Выполнить одну команду и завершить транзакцию
	 * @param initiator
	 * @param commandUnit
	 * @throws Exception
	 */
	public static void executeSingle(User initiator, PersistenceCommandUnit commandUnit) throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(initiator);
		transaction.addCommandUnit(commandUnit);
		transaction.execute();
	}
}
