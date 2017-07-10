package ecommander.model;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Сведения об ассоциациях
 * Created by E on 19/2/2017.
 */
class AssocRegistry {
	private HashMap<String, Assoc> assocByName = new HashMap<>(10);
	private HashMap<Byte, Assoc> assocById = new HashMap<>(10);

	static final String PRIMARY_NAME = "_default_";
	static final byte PRIMARY_ID = (byte)0;
	static final String ROOT_NAME = "_default_";
	static final byte ROOT_ID = Byte.MAX_VALUE;
	// Первичная ассоциация. Все айтемы связаны друг с другом этой ассоциацией по умолчанию.
	static final Assoc PRIMARY = new Assoc(PRIMARY_ID, PRIMARY_NAME, "Первичная иерархия", "", true);
	// Связь корневых айтемов с виртуальным корнем (которого физически нет).
	// Она нужна чтобы можно было извлекать корневые и не корневые айтемы единообразно, в том числе одним списком
	static final Assoc ROOT = new Assoc(ROOT_ID, ROOT_NAME, "Связь с пседвокорнем", "", false);

	AssocRegistry() {
		assocByName.put(PRIMARY_NAME, PRIMARY);
		assocById.put(PRIMARY_ID, PRIMARY);
	}

	void addAssoc(Assoc assoc) {
		assocByName.put(assoc.getName(), assoc);
		assocById.put(assoc.getId(), assoc);
	}

	Assoc getPrimary() {
		return PRIMARY;
	}

	Assoc getRoot() {
		return ROOT;
	}

	Assoc getAssoc(String name) {
		return assocByName.get(name);
	}

	Assoc getAssoc(byte id) {
		return assocById.get(id);
	}

	Byte[] getAllAssocIds() {
		return assocById.keySet().toArray(new Byte[0]);
	}

	Byte[] getAllOtherAssocIds(byte... excludedAssoc) {
		HashSet<Byte> allIds = new HashSet<>(assocById.keySet());
		for (byte assocId : excludedAssoc) {
			allIds.remove(assocId);
		}
		return allIds.toArray(new Byte[0]);
	}
}
