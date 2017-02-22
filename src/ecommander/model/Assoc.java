package ecommander.model;

/**
 * Ассоциация
 * Created by E on 26/1/2017.
 */
public class Assoc {
	private final byte id;
	private final String name;
	private final String caption;
	private final String description;
	private final boolean isTransitive;
	private final int hash;

	private static final String DEFAULT_NAME = "_default_";
	private static final Assoc DEFAULT = new Assoc((byte)0, DEFAULT_NAME, "Личный сабайтем", "", false, 0);

	public Assoc(byte id, String name, String caption, String description, boolean isTransitive, int hash) {
		this.id = id;
		this.name = name;
		this.caption = caption;
		this.description = description;
		this.isTransitive = isTransitive;
		this.hash = hash;
	}

	public byte getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getHash() {
		return name.hashCode();
	}

	public String getCaption() {
		return caption;
	}

	public String getDescription() {
		return description;
	}

	public boolean isTransitive() {
		return isTransitive;
	}

	public static Assoc getDefault() {
		return DEFAULT;
	}

	public static String getDefaultName() {
		return DEFAULT_NAME;
	}
}
