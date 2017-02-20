package ecommander.model.item;

import java.util.Collections;
import java.util.HashMap;

/**
 * Created by E on 19/2/2017.
 */
class AssocRegistry {
	private HashMap<String, Assoc> assocByName = new HashMap<>(10);
	private HashMap<Byte, Assoc> assocById = new HashMap<>(10);

	void addAssoc(Assoc assoc) {
		assocByName.put(assoc.getName(), assoc);
		assocById.put(assoc.getId(), assoc);
	}

	Assoc getAssoc(String name) {
		return assocByName.get(name);
	}

	Assoc getAssoc(byte id) {
		return assocById.get(id);
	}
}
