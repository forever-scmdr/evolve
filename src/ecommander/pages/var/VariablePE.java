package ecommander.pages.var;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import ecommander.pages.PageElement;
import ecommander.pages.ValidationResults;
import org.apache.commons.lang3.StringUtils;

import ecommander.fwk.Strings;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.PageElementContainer;
import org.apache.xerces.util.XMLChar;

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
	public enum Style {
		path, query, translit
	}
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public interface VariableContainer {
		void addVariable(VariablePE variablePE);
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

	public final boolean isStyleTranslit() {
		return style == Style.translit;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * Получить переменную, которая
	 * @return
	 */
	protected abstract Variable getVariable();

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
			((VariableContainer)container).addVariable(this);
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
		StringBuilder result = new StringBuilder();
		if (style == Style.path) {
			for (String value : getVariable().getLocalValues()) {
				result.append(COMMON_DELIMITER).append(name).append(COMMON_DELIMITER).append(URLEncoder.encode(value, Strings.SYSTEM_ENCODING));
			}
		} else if (style == Style.query) {
			for (String value : getVariable().getLocalValues()) {
				result.append(AMP_SIGN).append(name).append(EQ_SIGN).append(URLEncoder.encode(value, Strings.SYSTEM_ENCODING));
			}
		} else if (style == Style.translit) {
			for (String value : getVariable().getLocalValues()) {
				result.append(COMMON_DELIMITER).append(URLEncoder.encode(value, Strings.SYSTEM_ENCODING));
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
		if (StringUtils.isBlank(name) && name.startsWith("$"))
			results.addError(elementPath + " > " + getKey(), "user defined variable can not start with $ sign. $ is reserved for predefined variables");
		if (!XMLChar.isValidName(name))
			results.addError(elementPath + " > " + getKey(), "variable name is not a valid XML element name");
	}

}