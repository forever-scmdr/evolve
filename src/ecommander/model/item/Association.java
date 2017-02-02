package ecommander.model.item;

/**
 * Ассоциация
 * Created by E on 26/1/2017.
 */
public class Association implements ModelElement {
	private long id;
	private String name;
	private String caption;
	private String description;
	private boolean isTransitive;

	public Association(long id, String name, String caption, String description, boolean isTransitive) {
		this.id = id;
		this.name = name;
		this.caption = caption;
		this.description = description;
		this.isTransitive = isTransitive;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getHash() {
		return name.hashCode();
	}

	@Override
	public String getCaption() {
		return caption;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public boolean isTransitive() {
		return isTransitive;
	}
}
