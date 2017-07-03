package ecommander.persistence.itemquery;

import ecommander.persistence.common.TemplateQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
/**
 * Базовый интерфейс для одного критерия фильтра
 * @author EEEE
 *
 */
interface FilterCriteria {
	/**
	 * Создать часть SQL запроса, за которую отвечает данный критерий
	 * @param query - строка, к которой присоединяется SQL. Она может быть null, тогда создается новый StringBuilder
	 * @return
	 */
	void appendQuery(TemplateQuery query);
	/**
	 * Проверяет, является ли критерий заполненным (пригодным для применения)
	 * @return
	 */
	boolean isNotBlank();
	/**
	 * Должен ли фильтр вернуть пустое множество (бывает когда не задан обяательный критерий поиска)
	 * @return
	 */
	boolean isEmptySet();
	/**
	 * Создать часть Lucene запроса, за которую отвечает данный критерий
	 * @param queryBuilder
	 * @param occur
	 * @return
	 */
	BooleanQuery.Builder appendLuceneQuery(BooleanQuery.Builder queryBuilder, BooleanClause.Occur occur);
}