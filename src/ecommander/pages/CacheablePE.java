package ecommander.pages;
/**
 * Интерфейс для элементов страницы, которые могут быть закешированы,
 * т.е. элементы, которые требуют дополнительных затратных действий при выполнении страницы
 * 
 * Примеры кешируемых элементов страницы - айтем (ExecutableItemPE) и фильтр (FilterPE)
 * @author E
 *
 */
public interface CacheablePE extends PageElement {
	/**
	 * Является ли этот элемент кешируемым, т.е. должно ли его содержимое кешироваться после загрузки
	 * @return
	 */
	boolean isCacheable();
	/**
	 * Получить уникальный ID кеша этого элемента (ID должен быть уникальным среди всех кешей)
	 * @return
	 */
	String getCacheableId();
	/**
	 * Установить полученное из кеша содержимое страничного элемента
	 * @param cache
	 */
	void setCachedContents(String cache);
	/**
	 * Получить кешированное содержимое страничного элемента
	 * @return
	 */
	String getCachedContents();
}
