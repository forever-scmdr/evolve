package ecommander.model;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;

import java.util.Collection;
import java.util.HashSet;

/**
 * Реестр пользовательских групп
 * Реестр определен как только прочитается файл users.xml
 * @author EEEE
 *
 */
public class UserGroupRegistry {
	
	private static UserGroupRegistry REGISTRY;
	
	private BidiMap<String, Byte> groups = new DualHashBidiMap<>();
	
	private UserGroupRegistry() { }
	
	private static UserGroupRegistry getRegistry() {
		if (REGISTRY == null)
			REGISTRY = new UserGroupRegistry();
		return REGISTRY;
	}

	static void clearRegistry() {
		REGISTRY = null;
	}

	static void addGroup(String groupName, byte groupId) {
		getRegistry().groups.put(groupName, groupId);
	}
	
	public static Collection<String> getGroupNames() {
		return getRegistry().groups.keySet();
	}

	/**
	 * Получить ID группы по названию
	 * @param groupName
	 * @return
	 */
	public static byte getGroup(String groupName) {
		return getRegistry().groups.get(groupName);
	}

	/**
	 * Получить название группы по ее ID
	 * @param groupId
	 * @return
	 */
	public static String getGroup(byte groupId) {
		return getRegistry().groups.getKey(groupId);
	}

	public static boolean groupExists(String groupName) {
		return getRegistry().groups.containsKey(groupName);
	}
	
	public static Byte getDefaultGroup() {
		return getRegistry().groups.get(User.USER_DEFAULT_GROUP);
	}

	public static Byte[] getAllGroupIds() {
		return getRegistry().groups.values().toArray(new Byte[0]);
	}

	public static HashSet<Integer> getAllGroupIdsInteger() {
		HashSet<Integer> result = new HashSet<>();
		for (Byte value : getRegistry().groups.values()) {
			result.add(value.intValue());
		}
		return result;
	}
}