package ecommander.pages;



/**
 * Все сущности, которые присутствуют в модели страниц, такие, как сами модели страницы,
 * ссылки, фильтры, айтемы и т. д. реализуют этот интерфейс
 * @author EEEE
 *
 */
public interface PageElement {
	/**
	 * Создать клон элемента
	 * @return
	 */
	PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage);
	/**
	 * Проверить элемент на правильность (отсутствие ошибок)
	 * @param elementPath - путь к текущему элементу от корневого
	 * @param results
	 */
	void validate(String elementPath, ValidationResults results);
	/**
	 * Получить название элемента в читаемом виде
	 * @return
	 */
	String getKey();
	/**
	 * Получить имя класа элемента (например, ссылка)
	 * @return
	 */
	String getElementName();
}
