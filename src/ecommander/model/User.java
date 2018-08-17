package ecommander.model;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;

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

	public static final int ANONYMOUS_ID = 0;
	public static final byte NO_GROUP_ID = 0;

	private String name;
	private String password;
	private String description;
	private int userId;
	private HashMap<String, Group> groupRoles = new HashMap<>();
	private String groupRolesStr = ""; // Строка с группами и ролями. Используется при кешировании страниц

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
		User user = new User("", "", "", ANONYMOUS_ID);
		user.addGroup(USER_DEFAULT_GROUP, defaultGroup, SIMPLE);
		return user;
	}

	public void addGroup(String name, byte id, byte role) {
		Group oldGroup = groupRoles.get(name);
		String oldRoleStr = null;
		String newRoleStr = name + "_" + (role == ADMIN ? "adm_" : "sim_");
		if (oldGroup != null) {
			oldRoleStr = name + "_" + (oldGroup.role == ADMIN ? "adm_" : "sim_");
		}
		groupRoles.put(name, new Group(name, id, role));
		if (oldRoleStr != null) {
			StringUtils.replace(groupRolesStr, oldRoleStr, newRoleStr);
		} else {
			groupRolesStr += newRoleStr;
		}
	}

	/**
	 * Приналдлежит ли пользователь заданной групе
	 * @param group
	 * @return
	 */
	public boolean inGroup(String group) {
		return groupRoles.containsKey(group);
	}

	/**
	 * Приналдлежит ли пользователь заданной групе
	 * @param groupId
	 * @return
	 */
	public boolean inGroup(byte groupId) {
		return inGroup(UserGroupRegistry.getGroup(groupId));
	}

	/**
	 * Получить роль пользователя в заданной группе
	 * @param group
	 * @return
	 */
	public byte getRole(String group) {
		return groupRoles.containsKey(group) ? groupRoles.get(group).role : (byte)-1;
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
	 * Является ли пользователь суперпользователем - галвным админом.
	 * Суперпользователь является администратором главной группы (группы по умолчанию) - common
	 * @return
	 */
	public boolean isSuperUser() {
		return isAdmin(USER_DEFAULT_GROUP);
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
	public byte getRole(byte group) {
		return getRole(UserGroupRegistry.getGroup(group));
	}

	/**
	 * Получить все группы пользователя и его роли в этих группах
	 * @return
	 */
	public HashSet<Group> getGroups() {
		return new HashSet<>(groupRoles.values());
	}

	/**
	 * Все ID групп, в которых пользователь является администратором
	 * @return
	 */
	public Byte[] getAdminGroupIds() {
		HashSet<Byte> adminGoups = new HashSet<>();
		for (Group group : groupRoles.values()) {
			if (group.role == ADMIN)
				adminGoups.add(group.id);
		}
		return adminGoups.toArray(new Byte[0]);
	}

	/**
	 * Все ID групп, в которых пользователь НЕ является администратором
	 * @return
	 */
	public Byte[] getNotAdminGroupIds() {
		HashSet<Byte> simpleGoups = new HashSet<>();
		for (Group group : groupRoles.values()) {
			if (group.role == SIMPLE)
				simpleGoups.add(group.id);
		}
		return simpleGoups.toArray(new Byte[0]);
	}

	/**
	 * Получить строку с группами и ролями, для кеширования страниц
	 * @return
	 */
	public String getGroupRolesStr() {
		return groupRolesStr;
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
		return userId == ANONYMOUS_ID;
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

	/**
	 * Группы, которые есть у другого пользователя, но которых нет у этого пользователя
	 * @param user
	 * @return
	 */
	public HashSet<String> groupsNotInOf(User user) {
		HashSet<String> ownGroups = new HashSet<>(groupRoles.keySet());
		HashSet<String> otherUserGroups = new HashSet<>(user.groupRoles.keySet());
		otherUserGroups.removeAll(ownGroups);
		return otherUserGroups;
	}

	/**
	 * Группы, которые есть у этого пользователя, но которых нет у другого пользователя
	 * @param user
	 * @return
	 */
	public HashSet<String> groupsExtraOf(User user) {
		HashSet<String> ownGroups = new HashSet<>(groupRoles.keySet());
		HashSet<String> otherUserGroups = new HashSet<>(user.groupRoles.keySet());
		ownGroups.removeAll(otherUserGroups);
		return ownGroups;
	}

	/**
	 * Находит общие группы для обоих пользователей (этого и другого)
	 * @param user
	 * @return
	 */
	public HashSet<String> commonGroups(User user) {
		HashSet<String> ownGroups = new HashSet<>(groupRoles.keySet());
		HashSet<String> otherUserGroups = new HashSet<>(user.groupRoles.keySet());
		ownGroups.retainAll(otherUserGroups);
		return ownGroups;
	}
}
