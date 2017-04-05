package ecommander.model;

import java.util.HashMap;

/**
 * Created by E on 19/2/2017.
 */
class AssocRegistry {
	private HashMap<String, Assoc> assocByName = new HashMap<>(10);
	private HashMap<Byte, Assoc> assocById = new HashMap<>(10);

	static final String PRIMARY_NAME = "_default_";
	static final byte PRIMARY_ID = (byte)0;
	static final Assoc PRIMARY = new Assoc(PRIMARY_ID, PRIMARY_NAME, "Первичная иерархия", "", true);

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

	Assoc getAssoc(String name) {
		return assocByName.get(name);
	}

	Assoc getAssoc(byte id) {
		return assocById.get(id);
	}
}
