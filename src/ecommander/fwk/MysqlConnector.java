/*
* $RCSfile$
* $Revision$
* $Date$
* (c) Copyright International Business Machines Corporation, 2006
*/
package ecommander.fwk;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;


/**
 * Подключение к MySQL.
 * Единственный метод возвращает объект Connection
 * 
 * http://commons.apache.org/lang/api/org/apache/commons/lang/StringEscapeUtils.html
 * 
 * @author Karlov
 */
public class MysqlConnector
{
	private static final int MAX_CONNECTIONS = 24;
	
	private static AtomicInteger _open_count = new AtomicInteger(0);
	private static HashMap<Integer, Integer> connectionNames = new HashMap<>();
	//private static final HashSet<Integer> openConnections = new HashSet<>();
	private static final HashMap<Integer, String> openTraces = new HashMap<>();
	private static AtomicInteger com_name_counter = new AtomicInteger(0);
	
	//private static final Lock lock = new ReentrantLock();
	//private static final Condition isNotFull = lock.newCondition();

	public static class AutoRollback implements AutoCloseable {
		private boolean committed = false;
		private Connection conn;

		public AutoRollback(Connection conn) {
			this.conn = conn;
		}

		public void commit() throws SQLException {
			conn.commit();
			committed = true;
		}

		@Override
		public void close() throws Exception {
			if (!committed)
				conn.rollback();
		}
	}

	public static class LoggedConnection implements Connection {

		private final Connection conn;
		private final HttpServletRequest request;
		private ArrayList<Long> queryTimes = new ArrayList<>();
		private ArrayList<String> queries = new ArrayList<>();
		private long startQueryTime;
		private long createTime;
		private String currentQuery;
		
		private LoggedConnection(Connection conn, HttpServletRequest request) {
			this.conn = conn;
			this.request = request;
//			try {
//				lock.lock();
//				if (_open_count >= MAX_CONNECTIONS)
//					isNotFull.await();
				_open_count.incrementAndGet();
				Integer name = connectionNames.get(conn.hashCode());
				if (name == null) {
					name = com_name_counter.incrementAndGet();
					connectionNames.put(conn.hashCode(), name);
				}
//				synchronized (openConnections) {
//					openConnections.add(name);
//				}
				createTime = System.currentTimeMillis();
				StackTraceElement[] els = Thread.currentThread().getStackTrace();
				String trace = "";
				for (int i = 2; i < 12 && i < els.length; i++) {
					trace += "\n" + els[i];
				}
				synchronized (openTraces) {
					openTraces.put(name, trace);
					if (openTraces.size() > 5) {
						ServerLogger.error("\n\n\n/////////////---------- " + openTraces.size() + " CONNECTIONS ----------/////////////");
						StringBuilder message = new StringBuilder();
						for (Integer connName : openTraces.keySet()) {
							message.append("\n\n\tCONNECTION ").append(connName).append(":").append(openTraces.get(connName));
						}
						ServerLogger.error(message.toString());
					}
				}
				//ServerLogger.debug("/////////////---------- OPEN conneciton. Name " + name + "  Total: " + _open_count + createExtra() + " ----------/////////////" + trace);
//			} catch (InterruptedException e) {
//				ServerLogger.error("Interrupted", e);
//			}
//			finally {
//				lock.unlock();
//			}
		}
		
		public void queryFinished() {
			queryTimes.add(System.currentTimeMillis() - startQueryTime);
			queries.add(currentQuery);
		}
		
		private String createExtra() {
			if (request != null) {
				String query = request.getQueryString() == null ? "" : "?" + request.getQueryString();
				return "  IP: " + request.getRemoteAddr() + "  URL: " + request.getRequestURI() + query; 
			}
			return "";
		}
		
		public boolean isWrapperFor(Class<?> arg0) throws SQLException {
			return conn.isWrapperFor(arg0);
		}

		public <T> T unwrap(Class<T> arg0) throws SQLException {
			return conn.unwrap(arg0);
		}

		public void abort(Executor executor) throws SQLException {
			conn.abort(executor);
		}

		public void clearWarnings() throws SQLException {
			conn.clearWarnings();
		}

		public void close() throws SQLException {
			if (conn.isClosed())
				return;
			conn.close();
			_open_count.decrementAndGet();
			Integer name = connectionNames.get(conn.hashCode());
//			try {
//				lock.lock();
//				_open_count--;
//				if (_open_count < MAX_CONNECTIONS)
//					isNotFull.signal();
//			} finally {
//				lock.unlock();
//			}
			String logEntry = "";
			for (int i = 0; i < queryTimes.size(); i++) {
				logEntry += "\n" + queryTimes.get(i) + "\t" + queries.get(i);
			}
			long time = System.currentTimeMillis() - createTime;
			ServerLogger.error(logEntry);
			ServerLogger.error("/////////////---------- CLOSE conneciton. Name " + name + "   Open time: " + time + "   Total: " + _open_count
					+ createExtra() + " ----------/////////////");
			synchronized (openTraces) {
				openTraces.remove(name);
			}
//			synchronized (openConnections) {
//				openConnections.remove(name);
//				ServerLogger
//						.error("/////////////---------- REMAINS OPEN: " + StringUtils.join(openConnections, ", ") + " ----------/////////////");
//			}
		}

		public void commit() throws SQLException {
			conn.commit();
		}

		public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
			return conn.createArrayOf(typeName, elements);
		}

		public Blob createBlob() throws SQLException {
			return conn.createBlob();
		}

		public Clob createClob() throws SQLException {
			return conn.createClob();
		}

		public NClob createNClob() throws SQLException {
			return conn.createNClob();
		}

		public SQLXML createSQLXML() throws SQLException {
			return conn.createSQLXML();
		}

		public Statement createStatement() throws SQLException {
			return conn.createStatement();
		}

		public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
			return conn.createStatement(resultSetType, resultSetConcurrency);
		}

		public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
			return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
			return conn.createStruct(typeName, attributes);
		}

		public boolean getAutoCommit() throws SQLException {
			return conn.getAutoCommit();
		}

		public String getCatalog() throws SQLException {
			return conn.getCatalog();
		}

		public Properties getClientInfo() throws SQLException {
			return conn.getClientInfo();
		}

		public String getClientInfo(String name) throws SQLException {
			return conn.getClientInfo(name);
		}

		public int getHoldability() throws SQLException {
			return conn.getHoldability();
		}

		public DatabaseMetaData getMetaData() throws SQLException {
			return conn.getMetaData();
		}

		public int getNetworkTimeout() throws SQLException {
			return conn.getNetworkTimeout();
		}

		public String getSchema() throws SQLException {
			return conn.getSchema();
		}

		public int getTransactionIsolation() throws SQLException {
			return conn.getTransactionIsolation();
		}

		public Map<String, Class<?>> getTypeMap() throws SQLException {
			return conn.getTypeMap();
		}

		public SQLWarning getWarnings() throws SQLException {
			return conn.getWarnings();
		}

		public boolean isClosed() throws SQLException {
			return conn.isClosed();
		}

		public boolean isReadOnly() throws SQLException {
			return conn.isReadOnly();
		}

		public boolean isValid(int timeout) throws SQLException {
			return conn.isValid(timeout);
		}

		public String nativeSQL(String sql) throws SQLException {
			return conn.nativeSQL(sql);
		}

		public CallableStatement prepareCall(String sql) throws SQLException {
			return conn.prepareCall(sql);
		}

		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
		}

		public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		private void rememberPrepare(String sql) {
			startQueryTime = System.currentTimeMillis();
			currentQuery = sql;
		}
		
		public PreparedStatement prepareStatement(String sql) throws SQLException {
			rememberPrepare(sql);
			return conn.prepareStatement(sql);
		}

		public PreparedStatement prepareStatement(String sql, String statementForLog) throws SQLException {
			rememberPrepare(statementForLog);
			return conn.prepareStatement(sql);
		}

		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
			rememberPrepare(sql);
			return conn.prepareStatement(sql, autoGeneratedKeys);
		}

		public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys, String statementForLog) throws SQLException {
			rememberPrepare(statementForLog);
			return conn.prepareStatement(sql, autoGeneratedKeys);
		}

		public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
			rememberPrepare(sql);
			return conn.prepareStatement(sql, columnIndexes);
		}

		public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
			rememberPrepare(sql);
			return conn.prepareStatement(sql, columnNames);
		}

		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
			rememberPrepare(sql);
			return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
		}

		public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
				throws SQLException {
			rememberPrepare(sql);
			return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
		}

		public void releaseSavepoint(Savepoint savepoint) throws SQLException {
			conn.releaseSavepoint(savepoint);
		}

		public void rollback() throws SQLException {
			conn.rollback();
		}

		public void rollback(Savepoint savepoint) throws SQLException {
			conn.rollback(savepoint);
		}

		public void setAutoCommit(boolean autoCommit) throws SQLException {
			conn.setAutoCommit(autoCommit);
		}

		public void setCatalog(String catalog) throws SQLException {
			conn.setCatalog(catalog);
		}

		public void setClientInfo(Properties properties) throws SQLClientInfoException {
			conn.setClientInfo(properties);
		}

		public void setClientInfo(String name, String value) throws SQLClientInfoException {
			conn.setClientInfo(name, value);
		}

		public void setHoldability(int holdability) throws SQLException {
			conn.setHoldability(holdability);
		}

		public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
			conn.setNetworkTimeout(executor, milliseconds);
		}

		public void setReadOnly(boolean readOnly) throws SQLException {
			conn.setReadOnly(readOnly);
		}

		public Savepoint setSavepoint() throws SQLException {
			return conn.setSavepoint();
		}

		public Savepoint setSavepoint(String name) throws SQLException {
			return conn.setSavepoint(name);
		}

		public void setSchema(String schema) throws SQLException {
			conn.setSchema(schema);
		}

		public void setTransactionIsolation(int level) throws SQLException {
			conn.setTransactionIsolation(level);
		}

		public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
			conn.setTypeMap(map);
		}
		
	}
	
	private static DataSource _DS;
	static {
		try {
			_DS = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ECommanderDB");
			if (_DS == null)
				throw new FatalError("Datasource is NULL");
		} catch (Exception e) {
			ServerLogger.error("Unable to find JNDI source 'java:comp/env/jdbc/ECommanderDB'", e);
		}
	}
	/**
	 * Gets the connection from pool.
	 * If the pool is empty, creates the new one.
	 * @return
	 * @throws NamingException 
	 * @throws SQLException 
	 */
	public static synchronized LoggedConnection getConnection() throws NamingException, SQLException {
		//ServerLogger.debug("/////////////---------- trying to get connection ----------/////////////");
		return new LoggedConnection(_DS.getConnection(), null);
		//return _DS.getConnection();
	}

	public static synchronized LoggedConnection getConnection(HttpServletRequest request) throws NamingException, SQLException, InterruptedException {
		//ServerLogger.debug("/////////////---------- trying to get connection ----------/////////////");
		return new LoggedConnection(_DS.getConnection(), request);
		//return _DS.getConnection();
	}
	/**
	 * Marks the connection from pool as unused or closes it if it is not from pool
	 * @param conn
	 */
	public static void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) conn.close();
		}
		catch (SQLException e) {
			ServerLogger.error(e.getMessage(), e);
		}
	}
	
	public static void closeStatement(PreparedStatement pstmt) throws SQLException {
		if (pstmt != null) pstmt.close();
	}
	
	public static void closeStatement(Statement stmt) throws SQLException {
		if (stmt != null) stmt.close();
	}
}
