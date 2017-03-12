package ecommander.model;

import java.util.Collection;
import java.util.HashMap;

/**
 * Реестр пользовательских групп
 * Реестр определен как только прочитается файл users.xml
 * @author EEEE
 *
 */
public class UserGroupRegistry {
	
	private static UserGroupRegistry REGISTRY;
	
	private HashMap<String, Byte> groups;
	
	private UserGroupRegistry() {
		groups = new HashMap<>();
		groups.put(User.USER_DEFAULT_GROUP, User.NO_GROUP_ID);
	}
	
	private static UserGroupRegistry getRegistry() {
		if (REGISTRY == null)
			REGISTRY = new UserGroupRegistry();
		return REGISTRY;
	}
	
	public static void addGroup(String groupName, byte groupId) {
		getRegistry().groups.put(groupName, groupId);
	}
	
	public static Collection<String> getGroupNames() {
		return getRegistry().groups.keySet();
	}
	
	public static int getGroup(String groupName) {
		return getRegistry().groups.get(groupName);
	}
	
	public static boolean groupExists(String groupName) {
		return getRegistry().groups.containsKey(groupName);
	}
	
	public static byte getDefaultGroup() {
		return getRegistry().groups.get(User.USER_DEFAULT_GROUP);
	}
	/**
	 * Создать нового пользователя (новый объект класса User), не сохраняя его в БД
	 * @param groupName
	 * @param userName
	 * @param password
	 * @param description
	 * @return
	 */
	public static User createNewUser(String groupName, String userName, String password, String description) {
		if (groupExists(groupName))
			return new User(userName, password, description, groupName, User.NO_USER_ID, getGroup(groupName));
		else
			throw new IllegalArgumentException("There is no group '" + groupName + "'");
	}
}