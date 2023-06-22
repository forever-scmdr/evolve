package ecommander.pages.elements.variables;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.Strings;
import ecommander.pages.elements.ExecutablePagePE;
import ecommander.pages.elements.PageElement;
import ecommander.pages.elements.PageElementContainer;
import ecommander.pages.elements.ValidationResults;

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
	public static final String AMP_SIGN_ENCODED = "%26";
	public static final int AMP_SIGN_ENCODED_LENGTH = AMP_SIGN_ENCODED.length();
	
	private static final String DEFAULT_NAME = "_unnamed_";

	/**
	 * path - переменная и значение записываются path URLа (например, http://cool.com/devices/device/1001/)
	 * query - переменная и значение записываются query URLа (например, http://cool.com/devices/?device=1001)
	 * translit - транслитерация ключа (key) айтема в path URLа (например, http://cool.com/devices/televizor_1001/)
	 * @author E
	 *
	 */
	public static enum Style {
		path, query, translit
	}
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public static interface VariableContainer {
		void addVariable(VariablePE variablePE);
	}
	
	// Когда эта переменная присутствует в модели страницы не для загрузки, pageModel == null
	// Когда эта переменная добавляется к ссылке , принадлежащей уже ExecutablePageModel, pageModel становится не null
	protected ExecutablePagePE pageModel;
	protected String name = DEFAULT_NAME;
	protected Style style = Style.query; // Стиль передачи переменной в URL
	
	public VariablePE(String varName) {
		this.name = varName;
	}
	/**
	 * Конструктор для клонирования
	 * @param linkName
	 * @param parentPage
	 * @param transliterable
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

	public final boolean isStyleTranslit() {
		return style == Style.translit;
	}
	
	public void setStyle(Style style) {
		if (style != null)
			this.style = style;
	}

	public String getName() {
		return name;
	}

	public abstract String output();
	
	public abstract List<String> outputArray();
	
	public abstract boolean isEmpty();
	/**
	 * Может ли переменная хранить множество значений
	 * @return
	 */
	public abstract boolean isMultiple();
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
			((VariableContainer)container).addVariable(this);
	}
	/**
	 * Создание куска URL, который отвечает за эту переменную, например
 		/device_field/Маркировка шита
	 * в случае транслита
	 * /markirovka_shita
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String writeInAnUrlFormat() throws UnsupportedEncodingException {
		if (isMultiple()) {
			 return writeMultipleVariableInAnUrlFormat(this);
		} else {
			if (style == Style.path)
				return getName() + COMMON_DELIMITER + URLEncoder.encode(output(), Strings.SYSTEM_ENCODING);
			if (style == Style.query)
				return getName() + EQ_SIGN + URLEncoder.encode(output(), Strings.SYSTEM_ENCODING);
			// В случае транслита
			return URLEncoder.encode(output(), Strings.SYSTEM_ENCODING);
		}
	}

	/**
	 * Метод, который выводит значения некоторой СТАТИЧЕСКОЙ (неитерируемой) переменной.
	 * Значения выводятся в виде  имя=значение_1&имя=значение_2&имя=значение_3  т.е. одно имя - много значений (для стиля query)
	 * @param var
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	protected static String writeMultipleVariableInAnUrlFormat(VariablePE var) throws UnsupportedEncodingException {
		String result = new String();
		if (var.getStyle() == Style.path) {
			for (String value : var.outputArray()) {
				result += COMMON_DELIMITER + var.getName() + COMMON_DELIMITER + URLEncoder.encode(value, Strings.SYSTEM_ENCODING);
			}
		} else if (var.getStyle() == Style.query) {
			for (String value : var.outputArray()) {
				result += AMP_SIGN + var.getName() + EQ_SIGN + URLEncoder.encode(value, Strings.SYSTEM_ENCODING);
			}
		} else if (var.getStyle() == Style.translit) {
			for (String value : var.outputArray()) {
				result += COMMON_DELIMITER + URLEncoder.encode(value, Strings.SYSTEM_ENCODING);
			}
		}
		if (StringUtils.isBlank(result))
			return result;
		return result.substring(1);
	}
	
	public String getKey() {
		return "Variable '" + getName() + "'";
	}
	
	public String getElementName() {
		return ELEMENT_NAME;
	}
	
	public void validate(String elementPath, ValidationResults results) {
		if (StringUtils.isBlank(name) && name.startsWith("$"))
			results.addError(elementPath + " > " + getKey(), "user defined variable can not start with $ sign. $ is reserved for predefined variables");
	}
	
}