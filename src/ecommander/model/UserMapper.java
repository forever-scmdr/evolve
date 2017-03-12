package ecommander.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.NamingException;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.persistence.mappers.DBConstants;



/**
 * Загружает и сохраняет пользователей и группы
 * @author EEEE
 *
 */
public class UserMapper {
	
	/**
	 * Привтный конструктор
	 */
	private UserMapper() {
		
	}
	/**
	 * Получает юзера по логину и паролю.
	 * Если юзер не найден, возвращается null
	 * @param login
	 * @param pass
	 * @return
	 * @throws SQLException
	 * @throws NamingException 
	 */
	public static User getUser(String login, String pass) throws SQLException, NamingException {
		Connection conn = null;
		User user = null;
		try {
			String sql = "SELECT * FROM " + DBConstants.Users.TABLE + ", " + DBConstants.UserGroup.TABLE + " WHERE "
					+ DBConstants.Users.LOGIN + "=? AND " + DBConstants.Users.PASSWORD + "=? AND "
					+ DBConstants.Users.GROUP + "=" + DBConstants.UserGroup.NAME;
			conn = MysqlConnector.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ServerLogger.debug(sql);
			pstmt.setString(1, login);
			pstmt.setString(2, pass);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next())
				user = new User(rs.getString(DBConstants.Users.LOGIN), rs.getString(DBConstants.Users.PASSWORD), rs
						.getString(DBConstants.Users.DESCRIPTION), rs.getString(DBConstants.UserGroup.NAME), rs
						.getLong(DBConstants.Users.ID), rs.getInt(DBConstants.UserGroup.ID));
			return user;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Загружает юзера по его ID
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws NamingException 
	 */
	public static User getUser(long userId) throws SQLException, NamingException {
		Connection conn = null;
		User user = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql = new String();
			sql += "SELECT * FROM " + DBConstants.Users.TABLE + ", " + DBConstants.UserGroup.TABLE + " WHERE "
					+ DBConstants.Users.ID + "=" + userId + " AND "
					+ DBConstants.Users.GROUP + "=" + DBConstants.UserGroup.NAME;
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);

			if (rs.next())
				user = new User(rs.getString(DBConstants.Users.LOGIN), rs.getString(DBConstants.Users.PASSWORD), rs
						.getString(DBConstants.Users.DESCRIPTION), rs.getString(DBConstants.UserGroup.NAME), rs
						.getLong(DBConstants.Users.ID), rs.getInt(DBConstants.UserGroup.ID));
			return user;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Получает всех пользователей
	 * @return
	 * @throws SQLException
	 * @throws NamingException 
	 */
	public static ArrayList<User> getAllUsers() throws SQLException, NamingException {
		Connection conn = null;
		ArrayList<User> users = new ArrayList<User>();
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql = new String();
			sql += "SELECT * FROM " + DBConstants.Users.TABLE + ", " + DBConstants.UserGroup.TABLE + " WHERE "
					+ DBConstants.Users.GROUP + "=" + DBConstants.UserGroup.NAME + " ORDER BY " + DBConstants.UserGroup.NAME;
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
				users.add(new User(rs.getString(DBConstants.Users.LOGIN), rs.getString(DBConstants.Users.PASSWORD), rs
						.getString(DBConstants.Users.DESCRIPTION), rs.getString(DBConstants.UserGroup.NAME), rs
						.getLong(DBConstants.Users.ID), rs.getInt(DBConstants.UserGroup.ID)));
			return users;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Проверяет, есть ли такое имя пользователя
	 * @param userName
	 * @return
	 * @throws SQLException 
	 * @throws NamingException 
	 */
	public static boolean userNameExists(String userName) throws NamingException, SQLException {
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			String sql = new String();
			sql += "SELECT " + DBConstants.Users.LOGIN + " FROM " + DBConstants.Users.TABLE + " WHERE "
					+ DBConstants.Users.LOGIN + "=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userName);
			ServerLogger.debug(pstmt.toString());
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				return true;
			return false;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Загрузить все группы пользователей
	 * @throws SQLException
	 * @throws NamingException 
	 */
	public static void loadUserGorups() throws SQLException, NamingException {
		Connection conn = null;
		try {
			conn = MysqlConnector.getConnection();
			Statement stmt = conn.createStatement();
			String sql = new String();
			sql += "SELECT * FROM " + DBConstants.UserGroup.TABLE + " ORDER BY " + DBConstants.UserGroup.NAME;
			ServerLogger.debug(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next())
				UserGroupRegistry.addGroup(rs.getString(DBConstants.UserGroup.NAME), rs.getByte(DBConstants.UserGroup.ID));
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
	/**
	 * Загрузить пользователя по логину
	 * @param login
	 * @return
	 * @throws SQLException
	 * @throws NamingException
	 */
	public static User getUserByLogin(String login) throws SQLException, NamingException {
		Connection conn = null;
		User user = null;
		try {
			String sql = "SELECT * FROM " + DBConstants.Users.TABLE + ", " + DBConstants.UserGroup.TABLE + " WHERE " + DBConstants.Users.LOGIN
					+ "=? AND " + DBConstants.Users.GROUP + "=" + DBConstants.UserGroup.NAME;
			conn = MysqlConnector.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ServerLogger.debug(sql);
			pstmt.setString(1, login);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next())
				user = new User(rs.getString(DBConstants.Users.LOGIN), rs.getString(DBConstants.Users.PASSWORD),
						rs.getString(DBConstants.Users.DESCRIPTION), rs.getString(DBConstants.UserGroup.NAME),
						rs.getLong(DBConstants.Users.ID), rs.getInt(DBConstants.UserGroup.ID));
			return user;
		} finally {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
	}
}
