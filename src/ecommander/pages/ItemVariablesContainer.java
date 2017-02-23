package ecommander.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * Класс, представляющий информацию, которая отослана пользователем с помощью формы (или с помощью GET запроса,
 * не важно) и имеет отношение к определенным айтемам модели данных (Item)
 * Определяется в модели страниц через input
	Начало формы (котрая должна быть в html страницы)
	<item name="some_device" quantifier="single" id="item_id">
		<input name="check" id="item_id"/>
		<input name="quantity" id="item_id"/>
	</item>
	<item name="some_shop" quantifier="single" id="shop_id">
		<input name="comment" id="shop_id"/>
	</item>
	.......
	Конец формы
 * @author EEEE
 *
 */
public class ItemVariablesContainer implements Serializable {
	private static final long serialVersionUID = 329324718894163450L;
	/**
	 * Один айтем, и переменные, которые к нему относятся
	 * @author EEEE
	 *
	 */
	public static class ItemVariables implements Serializable {
		private static final long serialVersionUID = 2490841001963267396L;

		private long itemId;
		private int typeId;
		private ParameterValues variables;
		
		protected ItemVariables(long itemId, int itemTypeIs) {
			this.itemId = itemId;
			this.typeId = itemTypeIs;
			this.variables = new ParameterValues();
		}
		/**
		 * Добавить значение
		 * @param name
		 * @param value
		 */
		protected void addValue(String name, String value) {
			variables.add(name, value);
		}
		/**
		 * Получить значение по названию переменной
		 * @param name
		 * @return
		 */
		public String getValue(String name) {
			return variables.getString(name);
		}
		/**
		 * Получить несколько значений
		 * @param name
		 * @return
		 */
		public ArrayList<String> getValues(String name) {
			ArrayList<String> values = new ArrayList<String>();
			for (Object val : variables.getList(name)) {
				values.add((String) val);
			}
			return values;
		}
		/**
		 * Является ли значение множественным
		 * @param name
		 * @return
		 */
		public boolean isMultiple(String name) {
			return variables.isMultiple(name);
		}
		
		public Collection<String> getPostedInputs() {
			ArrayList<String> inputNames = new ArrayList<String>();
			for (Object paramId : variables.getExtraNames()) {
				inputNames.add((String) paramId);
			}
			return inputNames;
		}
		
		public long getItemId() {
			return itemId;
		}	
		
		public int getItemTypeId() {
			return typeId;
		}
	}

	// Оботражение (ID (Long) => ItemVariables) - все посты для айтемов, которые присутствуют в форме
	private HashMap<Long, ItemVariables> itemPosts = new HashMap<Long, ItemVariables>();
	private HashMap<String, String> extra; // дополнительные параметры запроса (НЕ параметры айтема)
	
	public ItemVariablesContainer(HttpServletRequest request) {
		Map<String, String[]> params = request.getParameterMap();
		for (String paramName : params.keySet()) {
			String[] paramValues = params.get(paramName);
			for (String value : paramValues) {
				addPost(paramName, value);
			}
		}
	}
	/**
	 * Метод, который получает название и значение одного параметра формы (из URL) и создает или изменяет на его основе объект ItemPost, для
	 * соответствующего айтема
	 * @param urlParamName
	 * @param urlParamValue
	 */
	private void addPost(String urlParamName, String urlParamValue) {
		String[] parts = UrlParameterFormatConverter.splitInputName(urlParamName);
		try {
			if (parts.length == 3) {
				long itemId = Long.parseLong(parts[2]);
				ItemVariables post = null;
				if (itemPosts.containsKey(itemId)) {
					post = itemPosts.get(itemId);
				} else {
					post = new ItemVariables(itemId, Integer.parseInt(parts[1]));
					itemPosts.put(itemId, post);
				}
				post.addValue(parts[0], urlParamValue);
				return;
			}
		} catch (NumberFormatException e) {
			// ничего не делать
		}
		// Если выполнение дошло досюда, просто добавить отправленное значение в extra
		if (!StringUtils.isBlank(urlParamValue)) {
			if (extra == null)
				extra = new HashMap<String, String>();
			extra.put(urlParamName, urlParamValue);
		}
	}
	/**
	 * Получить все посты для всех айтемов в рамках одной формы (ItemPost)
	 * @return
	 */
	public ArrayList<ItemVariables> getItemPosts() {
		return new ArrayList<ItemVariables>(itemPosts.values());
	}
	/**
	 * Получить посты для одного айтема
	 * @param itemId
	 * @return
	 */
	public ItemVariables getItemPost(long itemId) {
		return itemPosts.get(itemId);
	}
	/**
	 * Вернуть дополнительное поле
	 * @param name
	 * @return
	 */
	public String getExtra(String name) {
		if (extra == null)
			return null;
		return extra.get(name);
	}
	/**
	 * Вренуть дополнительное поле или значение по умолчанию, если поле не заполнено
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String getExtraDefault(String name, String defaultValue) {
		String val = getExtra(name);
		if (StringUtils.isBlank(val))
			return defaultValue;
		return val;
	}
	/**
	 * Проверка, есть ли доп. поле
	 * @param name
	 * @return
	 */
	public boolean containsExtra(String name) {
		return extra != null && extra.containsKey(name);
	}
}
