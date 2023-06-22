package ecommander.pages.elements;

import java.util.ArrayList;

public abstract class PageElementContainer implements PageElement {
	
	private ArrayList<PageElement> nested;
	
	public PageElementContainer() {
		this.nested = new ArrayList<PageElement>();
	}
	
	public final PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		PageElementContainer containerClone = createExecutableShallowClone(container, parentPage);
		for (PageElement element : nested) {
			containerClone.addElement(element.createExecutableClone(containerClone, parentPage));
		}
		return containerClone;
	}
	/**
	 * Добавить элемент
	 * @param element
	 */
	public void addElement(PageElement element) {
		nested.add(element);
	}
	/**
	 * Удалить элемент
	 * @param element
	 */
	public void removeElement(PageElement element) {
		nested.remove(element);
	}
	/**
	 * writeElement(Link), writeElement(PageItem), writeElement(Input)
	 * @return
	 */
	public ArrayList<PageElement> getAllNested() {
		return nested;
	}
	
	public boolean hasNested() {
		return nested.size() > 0;
	}
	/**
	 * Создать клон самого себя без вложенных элементов
	 * @return
	 */
	protected abstract PageElementContainer createExecutableShallowClone(PageElementContainer container, ExecutablePagePE parentPage);

	public final void validate(String elementPath, ValidationResults results) {
		if (validateShallow(elementPath, results)) {
			String path = elementPath + " > " + getKey();
			for (PageElement successor : nested) {
				successor.validate(path, results);
			}
		}
	}
	/**
	 * Проверка самого элемента, без вложенных
	 * Метод аналогичен методу validate
	 * @param elementPath
	 * @param errorMessages
	 */
	protected abstract boolean validateShallow(String elementPath, ValidationResults results);
}
