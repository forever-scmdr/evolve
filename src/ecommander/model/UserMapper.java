package ecommander.model;

import ecommander.fwk.MysqlConnector;
import ecommander.persistence.common.TemplateQuery;
import ecommander.persistence.mappers.DBConstants;

import javax.naming.NamingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;



/**
 * Загружает и сохраняет пользователей и группы
 * @author EEEE
 *
 */
public class UserMapper implements DBConstants.UsersTbl, DBConstants.UserGroups, DBConstants.Group {
	
	/**
	 * Привтный конструктор
	 */
	private UserMapper() {
		
	}

	private static User createUser(TemplateQuery query, Connection conn) throws SQLException, NamingException {
		User user = null;
		try (PreparedStatement pstmt = query.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				if (user == null)
					user = new User(rs.getString(U_LOGIN), rs.getString(U_PASSWORD), rs.getString(U_DESCRIPTION), rs.getInt(U_ID));
				user.addGroup(rs.getString(UG_GROUP_NAME), rs.getByte(UG_GROUP_ID), rs.getByte(UG_ROLE));
			}
		}
		return user;
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
	public static User getUser(String login, String pass, Connection conn) throws SQLException, NamingException {
		TemplateQuery selectUser = new TemplateQuery("Select user by login and password");
		selectUser.SELECT("*").FROM(U_TABLE).INNER_JOIN(UG_TABLE, U_ID, UG_USER_ID)
				.WHERE().col(U_LOGIN).setString(login).AND().col(U_PASSWORD).setString(pass);
		return createUser(selectUser, conn);
	}
	/**
	 * Загружает юзера по его ID
	 * @param userId
	 * @return
	 * @throws SQLException
	 * @throws NamingException 
	 */
	public static User getUser(int userId, Connection conn) throws SQLException, NamingException {
		TemplateQuery selectUser = new TemplateQuery("Select user by ID");
		selectUser.SELECT("*").FROM(U_TABLE).INNER_JOIN(UG_TABLE, U_ID, UG_USER_ID)
				.WHERE().col(U_ID).setInt(userId);
		return createUser(selectUser, conn);
	}
	/**
	 * Получает всех пользователей
	 * @return
	 * @throws SQLException
	 * @throws NamingException 
	 */
	public static ArrayList<User> getAllUsers(Connection conn) throws SQLException, NamingException {
		TemplateQuery selectUsers = new TemplateQuery("Select all users");
		selectUsers.SELECT("*").FROM(U_TABLE).INNER_JOIN(UG_TABLE, U_ID, UG_USER_ID);
		HashMap<Integer, User> allUsers = new HashMap<>();
		try (PreparedStatement pstmt = selectUsers.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int userId = rs.getInt(U_ID);
				User user = allUsers.get(userId);
				if (user == null) {
					user = new User(rs.getString(U_LOGIN), rs.getString(U_PASSWORD), rs.getString(U_DESCRIPTION), rs.getInt(U_ID));
					allUsers.put(userId, user);
				}
				user.addGroup(rs.getString(UG_GROUP_NAME), rs.getByte(UG_GROUP_ID), rs.getByte(UG_ROLE));
			}
		}
		return new ArrayList<>(allUsers.values());
	}
	/**
	 * Проверяет, есть ли такое имя пользователя
	 * @param userName
	 * @return
	 * @throws SQLException 
	 * @throws NamingException 
	 */
	public static boolean userNameExists(String userName, Connection conn) throws NamingException, SQLException {
		TemplateQuery checkUserName = new TemplateQuery("Check user name");
		checkUserName.SELECT("*").FROM(U_TABLE).WHERE().col(U_LOGIN).setString(userName);
		try (PreparedStatement pstmt = checkUserName.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Загрузить ID пользователя по его имени
	 * @param userName
	 * @param conn
	 * @return
	 * @throws NamingException
	 * @throws SQLException
	 */
	public static int getUserId(String userName, Connection conn) throws NamingException, SQLException {
		TemplateQuery checkUserName = new TemplateQuery("Check user name");
		checkUserName.SELECT(U_ID).FROM(U_TABLE).WHERE().col(U_LOGIN).setString(userName);
		try (PreparedStatement pstmt = checkUserName.prepareQuery(conn)) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(U_ID);
			}
		}
		return -1;
	}
	/**
	 * Загрузить все группы пользователей
	 * @throws SQLException
	 * @throws NamingException 
	 */
	public static void loadUserGorups() throws SQLException, NamingException {
		TemplateQuery selectGroups = new TemplateQuery("Select all groups");
		selectGroups.SELECT("*").FROM(G_TABLE);
		try (
				Connection conn = MysqlConnector.getConnection();
				PreparedStatement pstmt = selectGroups.prepareQuery(conn)
		) {
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				UserGroupRegistry.addGroup(rs.getString(G_NAME), rs.getByte(G_ID));
			}
		}
	}
}
