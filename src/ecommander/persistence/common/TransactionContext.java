/*
 * Created on 26.01.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ecommander.persistence.common;

import java.sql.Connection;

import ecommander.model.User;

/**
 * Контекст тразнзакции, пока в нем хранится только подключение к базе данных
 * 
 * @author karlov
 */
public class TransactionContext {
	private Connection connection;
	private User initiator = User.getDefaultUser();

	public TransactionContext(Connection conn, User initiator) {
		this.connection = conn;
		if (initiator != null)
			this.initiator = initiator;
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

}
