package ecommander.model;

import ecommander.extra._generated.Book;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class User {
	public static class Group {
		public final String name;
		public final byte id;
		public final byte role;

		private Group(String name, byte id, byte role) {
			this.name = name;
			this.id = id;
			this.role = role;
		}
	}

	public static final byte SIMPLE = (byte) 0;
	public static final byte ADMIN = (byte) 1;

	public static final String USER_DEFAULT_GROUP = "common"; // группа по умолчанию

	public static final int NO_USER_ID = 0;
	public static final byte NO_GROUP_ID = 0;

	private String name;
	private String password;
	private String description;
	private int userId;
	private HashMap<String, Group> groupRoles = new HashMap<>();

	public User(String name, String password, String description, int userId) {
		this.name = name;
		this.password = password;
		this.description = description;
		this.userId = userId;
	}
	/**
	 * Вернуть пользователя по умолчанию (userId - 0, группа - common, простой пользователь)
	 * @return
	 */
	public static User getDefaultUser() {
		Byte defaultGroup = UserGroupRegistry.getDefaultGroup();
		User user = new User("", "", "", NO_USER_ID);
		user.addGroup(USER_DEFAULT_GROUP, defaultGroup, SIMPLE);
		return user;
	}

	public void addGroup(String name, byte id, byte role) {
		groupRoles.put(name, new Group(name, id, role));
	}

	/**
	 * Приналдлежит ли пользователь заданной групе
	 * @param group
	 * @return
	 */
	public boolean belongsTo(String group) {
		return groupRoles.containsKey(group);
	}

	/**
	 * Приналдлежит ли пользователь заданной групе
	 * @param groupId
	 * @return
	 */
	public boolean belongsTo(byte groupId) {
		return belongsTo(UserGroupRegistry.getGroup(groupId));
	}

	/**
	 * Получить роль пользователя в заданной группе
	 * @param group
	 * @return
	 */
	public Byte getRole(String group) {
		return groupRoles.containsKey(group) ? groupRoles.get(group).role : null;
	}

	/**
	 * Проверить, является ли пользователь админом заданной группы
	 * @param group
	 * @return
	 */
	public boolean isAdmin(String group) {
		return groupRoles.containsKey(group) && groupRoles.get(group).role == ADMIN;
	}

	/**
	 * Проверить, является ли пользователь админом заданной группы
	 * @param group
	 * @return
	 */
	public boolean isAdmin(byte group) {
		String groupName = UserGroupRegistry.getGroup(group);
		return groupRoles.containsKey(groupName) && groupRoles.get(groupName).role == ADMIN;
	}
	/**
	 * Получить роль пользователя в заданной группе
	 * @param group
	 * @return
	 */
	public Byte getRole(byte group) {
		return getRole(UserGroupRegistry.getGroup(group));
	}

	/**
	 * Получить все группы пользователя и его роли в этих группах
	 * @return
	 */
	public ArrayList<Group> getGroups() {
		return new ArrayList<>(groupRoles.values());
	}
	/**
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	public void setNewPassword(String newPassword) {
		password = newPassword;
	}

	public void setNewName(String newName) {
		name = newName;
	}

	public boolean isAnonimous() {
		return userId == NO_USER_ID;
	}

	/**
	 * @return
	 */
	public int getUserId() {
		return userId;
	}

	public void setNewId(int id) {
		userId = id;
	}
}
