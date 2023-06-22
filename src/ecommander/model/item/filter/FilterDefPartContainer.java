package ecommander.model.item.filter;

import java.util.LinkedList;

import ecommander.common.exceptions.EcommanderException;
import ecommander.controllers.output.XmlDocumentBuilder;
/**
 * Контейнер для частей фильтра
 * Реализует все необходимые операции - добавление части, удаление, изменение порядка следования и возможно другие
 * @author EEEE
 *
 */
public abstract class FilterDefPartContainer extends FilterDefPart {

	protected LinkedList<FilterDefPart> parts;

	protected FilterDefPartContainer() {
		parts = new LinkedList<FilterDefPart>();
	}
	/**
	 * Удалить прямого потомка
	 * Этот метод вызывается автоматически (не в ручную)
	 * @param part
	 */
	void deleteAncestor(FilterDefPart part) {
		parts.removeFirstOccurrence(part);
		filter.unregisterPart(part.getId());
	}
	/**
	 * Добавить прямого потомка
	 * @param part
	 */
	public void addPart(FilterDefPart part) {
		parts.add(part);
		part.register(this, filter);
	}
	
	@Override
	final void outputXML(XmlDocumentBuilder doc) {
		outputStartTag(doc);
		for (FilterDefPart part : parts)
			part.outputXML(doc);
		doc.endElement();
	}
	
	@Override
	protected final void visit(FilterDefinitionVisitor visitor) throws EcommanderException {
		visitSelf(visitor);
		for (FilterDefPart part : parts) {
			part.visit(visitor);
		}
	}
	/**
	 * Вывод нужного начального тэга
	 * @param doc
	 */
	protected abstract void outputStartTag(XmlDocumentBuilder doc);
	/**
	 * Посещение конкретного потомка
	 * @param visitor
	 */
	protected abstract void visitSelf(FilterDefinitionVisitor visitor) throws EcommanderException;
}
