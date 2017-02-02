package ecommander.controllers;

import java.sql.SQLException;
import java.sql.Statement;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ecommander.common.ServerLogger;
import ecommander.persistence.DelayedTransaction;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.mappers.DBConstants;

/**
<authority>
	<groups>
		<group>admin</group>
	</groups>
	<users>
		<user>
			<group>admin</group>
			<login>admin</login>
			<password>eeee</password>
			<description>Ярослав</description>
		</user>
	</users>
</authority>
 * @author EEEE
 *
 */
public class UserCreationController {

	public static final String AUTHORITY_ELEMENT = "authority";
	public static final String GROUPS_ELEMENT = "groups";
	public static final String GROUP_ELEMENT = "group";
	public static final String USERS_ELEMENT = "users";
	public static final String USER_ELEMENT = "user";
	public static final String LOGIN_ELEMENT = "login";
	public static final String PASSWORD_ELEMENT = "password";
	public static final String DESCRIPTION_ELEMENT = "description";
	
	public static class ReadAndCreateCommandUnit extends DBPersistenceCommandUnit {

		public void execute() throws Exception {
			// Удалить всех юзеров
			clearUsers();
			
			// Прасить документ
			DOMParser parser = new DOMParser();
			parser.parse(AppContext.getUsersPath());
			final Document document = parser.getDocument();

			// Сначала группы
			// Только один элемент <groups>
			NodeList groups = document.getElementsByTagName(GROUPS_ELEMENT);
			Node groupsNode = groups.item(0);
			for (Node groupsSubnode = groupsNode.getFirstChild(); groupsSubnode != null; groupsSubnode = groupsSubnode.getNextSibling()) {
				if (groupsSubnode.getNodeType() == Node.ELEMENT_NODE && groupsSubnode.getNodeName().equalsIgnoreCase(GROUP_ELEMENT)) {
					String groupName = groupsSubnode.getFirstChild().getNodeValue();
					// Сохраниние каждой группы по отдельности
					String groupSql = new String();
					groupSql += "INSERT INTO " + DBConstants.UserGroup.TABLE + " SET " + DBConstants.UserGroup.NAME + "='"
							+ groupName + "'";
					ServerLogger.debug(groupSql);
					Statement stmt = getTransactionContext().getConnection().createStatement();
					stmt.executeUpdate(groupSql);
				}
			}
			// Потом юзеры
			// Только один элемент <users>
			NodeList users = document.getElementsByTagName(USERS_ELEMENT);
			Node usersNode = users.item(0);
			for (Node usersSubnode = usersNode.getFirstChild(); usersSubnode != null; usersSubnode = usersSubnode.getNextSibling()) {
				if (usersSubnode.getNodeType() == Node.ELEMENT_NODE && usersSubnode.getNodeName().equalsIgnoreCase(USER_ELEMENT)) {
					Node userNode = usersSubnode;
					String login = null, group = null, pass = null, description = null;
					for (Node userSubnode = userNode.getFirstChild(); userSubnode != null; userSubnode = userSubnode.getNextSibling()) {
						if (userSubnode.getNodeType() == Node.ELEMENT_NODE && userSubnode.getNodeName().equalsIgnoreCase(GROUP_ELEMENT)) {
							group = userSubnode.getFirstChild().getNodeValue();
						} else if (userSubnode.getNodeType() == Node.ELEMENT_NODE && userSubnode.getNodeName().equalsIgnoreCase(LOGIN_ELEMENT)) {
							login = userSubnode.getFirstChild().getNodeValue();
						} else if (userSubnode.getNodeType() == Node.ELEMENT_NODE
								&& userSubnode.getNodeName().equalsIgnoreCase(PASSWORD_ELEMENT)) {
							pass = userSubnode.getFirstChild().getNodeValue();
						} else if (userSubnode.getNodeType() == Node.ELEMENT_NODE
								&& userSubnode.getNodeName().equalsIgnoreCase(DESCRIPTION_ELEMENT)) {
							description = userSubnode.getFirstChild() != null ? userSubnode.getFirstChild().getNodeValue() : null;
						}
					}
					// Сохраниние каждого юзера
					String groupSql = new String();
					groupSql
						+= "INSERT INTO "
						+ DBConstants.Users.TABLE
						+ " SET "
						+ DBConstants.Users.DESCRIPTION + "='" + description + "', "
						+ DBConstants.Users.GROUP + "='" + group + "', "
						+ DBConstants.Users.LOGIN + "='" + login + "', "
						+ DBConstants.Users.PASSWORD + "='" + pass + "'";
					ServerLogger.debug(groupSql);
					Statement stmt = getTransactionContext().getConnection().createStatement();
					stmt.executeUpdate(groupSql);
				}
			}
		}
		
		private void clearUsers() throws SQLException {
			String clearSql = new String();
			clearSql
				+= "TRUNCATE "
				+ DBConstants.UserGroup.TABLE;
			ServerLogger.debug(clearSql);
			Statement stmt = getTransactionContext().getConnection().createStatement();
			stmt.executeUpdate(clearSql.toString());
			clearSql = new String();
			clearSql
				+= "TRUNCATE "
				+ DBConstants.Users.TABLE;
			ServerLogger.debug(clearSql);
			stmt.executeUpdate(clearSql.toString());
		}
	}
	
	/**
	 * Парсит XML модели данных и сохраняет все в БД одной транзакцией
	 * @throws Exception 
	 */
	public void readAndCreateUsers() throws Exception {
		DelayedTransaction transaction = new DelayedTransaction(null);
		transaction.addCommandUnit(new ReadAndCreateCommandUnit());
		transaction.execute();
	}
}
