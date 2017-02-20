package ecommander.model.item;

/**
 * Ассоциация
 * Created by E on 26/1/2017.
 */
public class Assoc {
	private byte id;
	private String name;
	private String caption;
	private String description;
	private boolean isTransitive;

	public Assoc(byte id, String name, String caption, String description, boolean isTransitive) {
		this.id = id;
		this.name = name;
		this.caption = caption;
		this.description = description;
		this.isTransitive = isTransitive;
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
}
