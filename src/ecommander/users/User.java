package ecommander.users;

public class User {
	public static final String USER_DEFAULT_GROUP = "common"; // группа по умолчанию
	
	public static final int NO_GROUP_ID = 0;
	public static final long NO_USER_ID = 0;
	
	private String name;
	private String password;
	private String description;
	private String group = USER_DEFAULT_GROUP;
	
	private long userId;
	private int groupId = NO_GROUP_ID;
	
	
	
	public User(String name, String password, String description, String group, long userId, int groupId) {
		super();
		this.name = name;
		this.password = password;
		this.description = description;
		this.group = group;
		this.userId = userId;
		this.groupId = groupId;
	}
	/**
	 * Вернуть пользователя по умолчанию (группа - common, groupId - ID группы common, userId - 0)
	 * @return
	 */
	public static User getDefaultUser() {
		Integer defaultGroup = UserGroupRegistry.getDefaultGroup();
		int groupId = NO_GROUP_ID;
		if (defaultGroup != null)
			groupId = defaultGroup;
		return new User("", "", "", USER_DEFAULT_GROUP, NO_USER_ID, groupId);
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
	public String getGroup() {
		return group;
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
	
	public boolean isDefaultGroup() {
		return getGroup().equals(USER_DEFAULT_GROUP);
	}

	public boolean isAnonimous() {
		return userId == NO_USER_ID;
	}
	/**
	 * Обладает ли пользователь правами главного администратора
	 * @return
	 */
	public boolean isSuperUser() {
		return isDefaultGroup() && !isAnonimous();
	}
	
	/**
	 * @return
	 */
	public int getGroupId() {
		return groupId;
	}

	/**
	 * @return
	 */
	public long getUserId() {
		return userId;
	}

	public void setNewId(long id) {
		userId = id;
	}
	
	public void setGroup(String groupName){
		int newGroupId = UserGroupRegistry.getGroup(groupName);
		this.group = groupName;
		this.groupId = newGroupId;
	}
	
	public void setGroup(int groupId){
		String newGroup = UserGroupRegistry.getGroupName(groupId);
		this.group = newGroup;
		this.groupId = groupId;
	}
}
