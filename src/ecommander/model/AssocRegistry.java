package ecommander.model;

import java.util.HashMap;

/**
 * Created by E on 19/2/2017.
 */
class AssocRegistry {
	private HashMap<String, Assoc> assocByName = new HashMap<>(10);
	private HashMap<Byte, Assoc> assocById = new HashMap<>(10);

	static final String DEFAULT_NAME = "_default_";
	static final byte DEFAULT_ID = (byte)0;
	static final Assoc DEFAULT = new Assoc(DEFAULT_ID, DEFAULT_NAME, "Личный сабайтем", "", false, 0);

	AssocRegistry() {
		assocByName.put(DEFAULT_NAME, DEFAULT);
		assocById.put(DEFAULT_ID, DEFAULT);
	}

	void addAssoc(Assoc assoc) {
		assocByName.put(assoc.getName(), assoc);
		assocById.put(assoc.getId(), assoc);
	}

	Assoc getDefault() {
		return DEFAULT;
	}

	Assoc getAssoc(String name) {
		return assocByName.get(name);
	}

	Assoc getAssoc(byte id) {
		return assocById.get(id);
	}
}
