package ecommander.pages.var;

import ecommander.fwk.Strings;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElement;
import ecommander.pages.PageElementContainer;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Страничная переменная
 * @author EEEE
 * TODO <fix> добавить валидацию, имя должно начинаться буквы или подчеркивания
 */
public abstract class VariablePE implements PageElement {
	public static final String ELEMENT_NAME = "var";
	
	public static final char COMMON_DELIMITER = '/';
	public static final char EQ_SIGN = '=';
	public static final char AMP_SIGN = '&';
	public static final char NEGATIVE = '-';
	public static final String AMP_SIGN_ENCODED = "%26";
	public static final int AMP_SIGN_ENCODED_LENGTH = AMP_SIGN_ENCODED.length();
	
	private static final String DEFAULT_NAME = "_unnamed_";

	/**
	 * path - переменная и значение записываются path URLа (например, http://cool.com/devices/device/1001/)
	 * query - переменная и значение записываются query URLа (например, http://cool.com/devices/?device=1001)
	 * key - транслитерация ключа (key) айтема в path URLа (например, http://cool.com/devices/televizor_1001/)
	 * keypath -    транслитерация ключа (key) айтема и всех ключей предыдущих по иерархии вложенности айтемов
	 *              в path URLа (например, http://cool.com/devices/televizor_1001/)
	 * @author E
	 *
	 */
	public enum Style {
		path, query, key, keypath;
		public static Style getValue(String val) {
			if (StringUtils.equalsAnyIgnoreCase(val, "keypath", "key-path", "key_path"))
				return keypath;
			if (StringUtils.equalsIgnoreCase("path", val))
				return path;
			if (StringUtils.equalsAnyIgnoreCase(val, "key", "translit", "key-unique"))
				return key;
			if (StringUtils.isBlank(val) || StringUtils.equalsIgnoreCase("query", val))
				return query;
			throw new IllegalArgumentException("there is no Link Style value for '" + val + "' string");
		}
	}
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public interface VariableContainer {
		void addVariablePE(VariablePE variablePE);
	}
	
	// Когда эта переменная присутствует в модели страницы не для загрузки, pageModel == null
	// Когда эта переменная добавляется к ссылке , принадлежащей уже ExecutablePageModel, pageModel становится не null
	protected ExecutablePagePE pageModel;
	protected String name = DEFAULT_NAME;
	protected Style style; // Стиль передачи переменной в URL
	
	protected VariablePE(String varName, Style style) {
		this.name = varName;
		this.style = style;
	}
	/**
	 * Конструктор для клонирования
	 * @param var
	 * @param parentPage
	 */
	protected VariablePE(VariablePE var, ExecutablePagePE parentPage) {
		this.name = var.name;
		this.pageModel = parentPage;
		this.style = var.style;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public final Style getStyle() {
		return style;
	}

	public final boolean isStyleKey() {
		return style == Style.key || style == Style.keypath;
	}

	public final boolean isStyleKeyPath() {
		return style == Style.keypath;
	}

	public final boolean isStyleQuery() {
		return style == Style.query;
	}

	public final boolean isStylePath() {
		return style == Style.path;
	}

	public String getName() {
		return name;
	}

	/**
	 * Получить переменную, которая
	 * @return
	 */
	public abstract Variable getVariable();

	public abstract boolean isEmpty();
	/**
	 * Создать клон переменной определенного типа
	 * @return
	 */
	protected abstract VariablePE createVarClone(ExecutablePagePE parentPage);

	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		VariablePE clone = createVarClone(parentPage);
		clone.addToContainer(container);
		return clone;
	}
	
	protected void addToContainer(PageElementContainer container) {
		if (container != null)
			((VariableContainer)container).addVariablePE(this);
	}
	/**
	 * Создание куска URL, который отвечает за эту переменную, например
	 * /device_field/Маркировка шита
	 * Значения выводятся в виде  имя=значение_1&имя=значение_2&имя=значение_3  т.е. одно имя - много значений (для стиля query)
	 * в случае транслита
	 * /markirovka_shita
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String writeInAnUrlFormat() throws UnsupportedEncodingException {
		if (isEmpty() && style != Style.query)
			return Strings.EMPTY;
		StringBuilder result = new StringBuilder();
		if (style == Style.path) {
			for (String value : getVariable().getLocalValues()) {
				result.append(COMMON_DELIMITER).append(name).append(COMMON_DELIMITER).append(URLEncoder.encode(value, Strings.SYSTEM_ENCODING).replaceAll("\\+", "%20"));
			}
		} else if (style == Style.query) {
			if (isEmpty()) {
				result.append(AMP_SIGN).append(name).append(EQ_SIGN);
			} else {
				for (String value : getVariable().getLocalValues()) {
					result.append(AMP_SIGN).append(name).append(EQ_SIGN).append(URLEncoder.encode(value, Strings.SYSTEM_ENCODING).replaceAll("\\+", "%20"));
				}
			}
		} else if (isStyleKey()) {
			for (String value : getVariable().getLocalValues()) {
				result.append(COMMON_DELIMITER).append(URLEncoder.encode(value, Strings.SYSTEM_ENCODING).replaceAll("\\+", "%20"));
			}
		}
		if (StringUtils.isBlank(result))
			return result.toString();
		return result.substring(1);
	}

	public String getKey() {
		return "Variable '" + getName() + "'";
	}
	
	public String getElementName() {
		return ELEMENT_NAME;
	}
	
	public void validate(String elementPath, ValidationResults results) {
		if (StringUtils.startsWith(name,"$"))
			results.addError(elementPath + " > " + getKey(), "user defined variable can not start with $ sign. $ is reserved for predefined variables");
		if (!StringUtils.equals(Strings.createXmlElementName(name), name)) {
			if (StringUtils.startsWith(name, NEGATIVE + "")) {
				String posName = name.substring(1);
				if (StringUtils.equals(Strings.createXmlElementName(posName), posName))
					return;
			}
			results.addError(elementPath + " > " + getKey(), "variable name is not a valid XML element name");
		}
	}

	@Override
	public String toString() {
		return name + "(" + style + ") -> " + getVariable();
	}

	@Override
	public boolean equals(Object obj) {
		return ((VariablePE) obj).name.equalsIgnoreCase(this.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}
}