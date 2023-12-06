/*
 * Created on 26.01.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ecommander.persistence.common;

import java.sql.Connection;

import ecommander.fwk.Timer;
import ecommander.model.User;

/**
 * Контекст тразнзакции, пока в нем хранится только подключение к базе данных
 * 
 * @author karlov
 */
public class TransactionContext {
	private Connection connection;
	private User initiator;
	private Timer timer;

	public TransactionContext(Connection conn, User initiator, Timer... timer) {
		this.connection = conn;
		this.initiator = initiator;
		if (timer.length > 0)
			this.timer = timer[0];
		else
			this.timer = new Timer();
	}
	/**
	 * @return
	 */
	public Connection getConnection() {
		return connection;
	}
	
	public User getInitiator() {
		return initiator;
	}
	/**
	 * @param connection
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Получить таймер для отладки времени выполения команд
 	 * @return
	 */
	public Timer getTimer() {
		return timer;
	}
}
