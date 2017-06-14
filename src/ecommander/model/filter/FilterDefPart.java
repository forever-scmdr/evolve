package ecommander.model.filter;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.XmlDocumentBuilder;



/**
 * Часть фильтра, имеющая смысл отдлельно сама по себе и к которой можно получить доступ отдельно с помощью ID
 * @author EEEE
 *
 */
public abstract class FilterDefPart {
	static final int NO_ID = -1;
	
	private int id = NO_ID;
	protected FilterDefPartContainer directParent;
	protected FilterDefinition filter;
	
	protected FilterDefPart() {

	}
	/**
	 * Зарегистрировать часть в фильтре. 
	 * Этот метод вызывается автоматически (не в ручную)
	 * @param directParent
	 * @param filter
	 */
	protected final void register(FilterDefPartContainer directParent, FilterDefinition filter) {
		this.directParent = directParent;
		this.filter = filter;
		id = filter.registerPart(this);
	}
	
	public final int getId() {
		return id;
	}
	/**
	 * Получить ID прямого предка, если предка нет, возвращается NO_ID (-1)
	 * @return
	 */
	public int getParentId() {
		if (directParent != null)
			return directParent.getId();
		return NO_ID;
	}
	/**
	 * Удалить часть
	 */
	public void delete() {
		directParent.deleteAncestor(this);
	}

	@Override
	public boolean equals(Object obj) {
		return id == ((FilterDefPart)obj).id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	/**
	 * Осуществить вывод части фильтра в документ
	 * @param doc
	 * @return
	 */
	abstract void outputXML(XmlDocumentBuilder doc);
	/**
	 * Посещение посетителем
	 * @param visitor
	 */
	protected abstract void visit(FilterDefinitionVisitor visitor) throws EcommanderException;
}
