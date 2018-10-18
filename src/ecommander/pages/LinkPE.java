package ecommander.pages;

import ecommander.fwk.ServerLogger;
import ecommander.pages.var.*;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * Для каждого айтема, который можно передавать и принимать по ссылке, должно быть спец имя (ID).
 * Когда описывается ссылка, то в ней указывается соответствие либо айтема на текущей странице, либо фильтра 
 * айтему на странице, на которую ведет ссылка.

<link name="show_device" target="device" copy-page-vars="yes">	// copy-page-vars - необязательный параметр, означает, что в ссылку надо 
																// скопировать все переменные текуще страницы (не надо переписывать по одной)
	<var name="some_var" value="eeee"> // переменная со статическим значением
	<var name="device" item="device"/> // переменная с динамическим значением - ID айтема
	<var name="device" item="device" parameter="producer"/> // переменная с динамическим значением - Значение параметра айтема
	<var name="filter_parameter_value_2" var="filter_parameter_value_1"/> // переменная с динамическим значением - Другая переменная
	
	<var ... style="translit"/> // Такая переменная может передаваться в формате транслита
	<var ... style="query"/> // Название и значение переменной передается в URL query (в формате page?name=value)
</link>

// Ссылка может просто вести на страницу, а может быть базовой ссылкой для сабмита формы, тогда она должна иметь соответствующий тип

<link name="submit_register" type="itemform"/>

 *
 * Имеет значение порядок следования переменных ссылки. Это нужно для того, чтобы можно было не использовать названия переменных 
 * (через ссылку передаются чисто значения, а принимающая страница (та, на которую ведет ссылка) также задает этот порядок следования)
 *
 * @author E
 */
public class LinkPE implements VariablePE.VariableContainer, PageElement {
	public static final String ELEMENT_NAME = "link";
	
	public static char QUESTION_SIGN = '?';
	public static final String ITEM_FORM_PREFIX = "itemform/";
	public static final String FILTER_PREFIX = "fil/";
	public static final String VAR_VARIABLE = "var"; // используестя в пользовательском фильтре
	
	public enum Type {
		normal,     // Простая ссылка - название старницы и далее параметры
		itemform,   // Ссылка, коротая начинается с префикса itemform
		filter,     // Ссылка, коротая начинается с префикса fil
		exclusive   // Ссылка без названия страницы, подразумевается, что первый параметр - айтем и у него есть страница по умолчанию
	}
	
	/**
	 * Интерфейс, который должны реализовывать контейнеры, обрабатывающие добавление ExecutableItemPE особым образом
	 * @author EEEE
	 */
	public interface LinkContainer {
		void addLink(LinkPE linkPE);
	}
	
	// URL страницы
	private ValueOrRef pageName;
	// Имя ссылки (для удобства)
	private String linkName;
	// Тип ссылки - нормальная ссылка (по умолчанию) или ссылка на сабмит формы (форма айтема (itemform) или форма набора айтемов (itemvars))
	private Type type = Type.normal;
	// Нужно ли копировать все переменные из страницы
	private boolean copyPageVars = false;
	// Названия переменных - значения переменных
	private LinkedHashMap<String, VariablePE> variables = new LinkedHashMap<>();
	// счетчик для создания фиктивных названий переменных в случае если у переменной нет имени
	private int counter = 0;
	/**
	 * Ссылка в модели страницы с неизвестными параметрами
	 * @param linkName
	 * @param pageNameVar
	 * @param type
	 * @param copyPageVars
	 */
	private LinkPE(String linkName, ValueOrRef pageNameVar, Type type, boolean copyPageVars) {
		this.linkName = linkName;
		this.pageName = pageNameVar;
		this.type = type;
		this.copyPageVars = copyPageVars;
	}
	/**
	 * Создание ссылки из строки. пришедшей от клиента в виде URL
	 * Подразумевается, что у страницы есть название и
	 * у каждой переменной path (включая translit) также есть название
	 * @param urlString
	 * @throws UnsupportedEncodingException
	 */
	private LinkPE(String urlString) throws UnsupportedEncodingException {
		if (StringUtils.isBlank(urlString)) {
			return;
		}
		// Строка разбивается на path и query
		String path = urlString;
		String query = null;
		int questionIdx = urlString.indexOf(QUESTION_SIGN);
		if (questionIdx > 0) {
			path = urlString.substring(0, questionIdx);
			query = urlString.substring(questionIdx + 1);
		}
		// Строка разбивается на структурные единицы (переменные)
		String[] units = StringUtils.split(path, VariablePE.COMMON_DELIMITER);
		// Название страницы
		String pageName = units[0];
		this.pageName = ValueOrRef.newValue(pageName);
		PagePE page = PageModelRegistry.getRegistry().getPageModel(pageName);
		if (units.length > 1) {
			// Получаются все переменные по отдельности
			// Первая часть уже взята, поэтому i = 1
			for (int i = 1; i < units.length; i++) {
				String varName = units[i];
				i++; // Берем следующее значение после /
				// Если выход за пределы массива, значит неправильный формат URL
				if (i >= units.length) {
					ServerLogger.warn("Incorrect URL format in: " + urlString);
					break;
				}
				String varValue = URLDecoder.decode(units[i], "UTF-8");
				addParsedVariableValue(page, varName, varValue);
			}
		}
		// Разбор query части запроса
		if (!StringUtils.isBlank(query)) {
			String[] vars = StringUtils.split(query, VariablePE.AMP_SIGN);
			for (String varStr : vars) {
				int eqIdx = varStr.indexOf(VariablePE.EQ_SIGN);
				if (eqIdx > 0) {
					String varName = varStr.substring(0, eqIdx);
					String varValue = URLDecoder.decode(varStr.substring(eqIdx + 1), "UTF-8");
					addParsedVariableValue(page, varName, varValue);
				}
			}
		}
	}


	private void addParsedVariableValue(PagePE page, String varName, String varValue) {
		RequestVariablePE varPE = (RequestVariablePE) getVariablePE(varName);
		if (varPE == null) {
			RequestVariablePE initVar = page == null ? null : page.getInitVariablePE(varName);
			if (initVar != null) {
				varPE = new RequestVariablePE(varName, initVar.getScope(), initVar.getStyle());
				varPE.resetValue(varValue);
			} else {
				varPE = new RequestVariablePE(varName, varValue);
			}
			addVariablePE(varPE);
		} else {
			varPE.addValue(varValue);
		}
	}


	/**
	 * Создать ссылку на базе строки URL
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static LinkPE parseLink(String url) throws UnsupportedEncodingException {
		return new LinkPE(url);
	}
	/**
	 * Получить имя страницы
	 * @return
	 */
	public String getPageName() {
		return pageName.writeSingleValue();
	}

	public String getLinkName() {
		return linkName;
	}
	
	public void setType(Type type) {
		if (type != null)
			this.type = type;
	}
	/**
	 * Порядок сериализации: 
	 * 1. Повторяются строки 2-4 
	 * 2. Название айтема 
	 * 3. ID айтема 
	 * 4. Фильтр айтема
	 * 
	 * @return
	 */
	public String serialize() {
		StringBuilder path = new StringBuilder();
		StringBuilder query = new StringBuilder();
		// Если не обычная сслыка, начать с itemvars/ itemform/ или fil/
		if (type != Type.normal) {
			if (type == Type.filter)
				path.append(FILTER_PREFIX);
			else if (type == Type.itemform)
				path.append(ITEM_FORM_PREFIX);
		}
		// Название страницы (device/)
		if (type != Type.exclusive)
			path.append(getPageName());
		// Все переменные по порядку
		try {
			for (VariablePE var : variables.values()) {
				if ((var.getStyle() == VariablePE.Style.path || var.getStyle() == VariablePE.Style.translit) && !var.isEmpty())
					path.append(VariablePE.COMMON_DELIMITER).append(var.writeInAnUrlFormat());
				else if (var.getStyle() == VariablePE.Style.query/* && !var.isEmpty()*/)
					query.append(VariablePE.AMP_SIGN).append(var.writeInAnUrlFormat());
			}
		} catch (UnsupportedEncodingException e) {
			//
		}
		// Иногда название страницы уже может содержать знак ? в случае когда
		// имя страницы задается через target-var, а эта переменная содержит урл
		// с параметрами
		boolean hasQuestion = StringUtils.contains(path, '?');
		if (!hasQuestion)
			path.append(VariablePE.COMMON_DELIMITER);
		if (query.length() > 0) {
			if (hasQuestion)
				path.append(query);
			else
				path.append(QUESTION_SIGN).append(query.substring(1));
		}
		return path.toString();
	}
	/**
	 * Находит переменную по ее названию
	 * @param varName
	 * @return
	 */
	public VariablePE getVariablePE(String varName) {
		return variables.get(varName);
	}

	/**
	 * Получить переменную (не PE) по ее названию
	 * @param varName
	 * @return
	 */
	public Variable getVariable(String varName) {
		VariablePE varPE = variables.get(varName);
		if (varPE != null)
			return varPE.getVariable();
		return null;
	}
	/**
	 * Удалить переменную из ссылки
	 * @param varName
	 */
	public void removeVariable(String varName) {
		variables.remove(varName);
	}
	/**
	 * Плучить все переменные ссылки
	 * @return
	 */
	public Collection<VariablePE> getAllVariables() {
		return variables.values();
	}
	
	public final void addVariablePE(VariablePE variable) {
		if (StringUtils.isBlank(variable.getName())) {
			variables.put("var_" + counter++, variable);
		} else {
			if (variables.containsKey(variable.getName())) {
				variables.put(variable.getName() + counter++, variable);
			} else {
				variables.put(variable.getName(), variable);
			}
		}
	}
	/**
	 * Установить значение статической переменной в ссылке
	 * @param name
	 * @param value
	 */
	public final void addStaticVariable(String name, String value) {
		RequestVariablePE var = (RequestVariablePE) getVariablePE(name);
		if (var == null) {
			RequestVariablePE staticVar = new RequestVariablePE(name, value);
			addVariablePE(staticVar);
		} else
			var.addValue(value);
	}
	
	public PageElement createExecutableClone(PageElementContainer container, ExecutablePagePE parentPage) {
		LinkPE clone = new LinkPE(linkName, (ValueOrRef) pageName.getInited(parentPage), type, copyPageVars);
		if (container != null && container instanceof LinkContainer)
			((LinkContainer)container).addLink(clone);
		// Копировать переменные из страницы, если это надо
		if (copyPageVars) {
			for (Variable pageVar : parentPage.getAllVariables()) {
				if (!variables.containsKey(pageVar.getName()) && !StringUtils.startsWith(pageVar.getName(), "$")
						&& !(pageVar instanceof SessionStaticVariable) && !pageVar.isEmpty()) {
					VariablePE.Style style = VariablePE.Style.query;
					if (parentPage.getInitVariablePE(pageVar.getName()) != null)
						style = parentPage.getInitVariablePE(pageVar.getName()).getStyle();
					LinkVariablePE refVar = LinkVariablePE.createVarVar(pageVar.getName(), style, pageVar.getName());
					clone.addVariablePE((VariablePE) refVar.createExecutableClone(null, parentPage));
				}
			}
		}
		// Все переменные по порядку
		for (PageElement variable : variables.values()) {
			VariablePE varClone = (VariablePE)variable.createExecutableClone(null, parentPage);
			clone.addVariablePE(varClone);
		}
		return clone;
	}
	
	public static void main(String[] args) {
//		String[] result = "asd:v:ddd:l:ccc/ggg/fff:l:rrr/:v:hhh".split("(:l:)|/");
//		for (String string : result) {
//			System.out.println(string);
//		}
		System.out.println(Arrays.toString(StringUtils.splitByWholeSeparator("asd:v:ddd:l:ccc/ggg/fff:l:rrr/:v:hhh:l:", ":l:")));
	}
	public void validate(String elementPath, ValidationResults results) {
		// Есть ли название у ссылки
		if (StringUtils.isBlank(linkName))
			results.addError(elementPath + " > " + getKey(), "link name is not set");
		// Есть ли страница, на которую ссылается ссылка
		if (pageName.isValue()) {
			String name = pageName.writeSingleValue();
			if (StringUtils.isBlank(name) || !PageModelRegistry.pageExists(name))
				results.addError(elementPath + " > " + getKey(), "there is no '" + name + "' page in this site");
		}
		String path = elementPath + " > " + getKey();
		for (PageElement variable : variables.values()) {
			variable.validate(path, results);
		}
	}
	public String getKey() {
		return "Link '" + linkName + "'";
	}
	/**
	 * Создать ссылку на страницу, название которой задается явно
	 * @param linkName
	 * @param pageName
	 * @return
	 */
	public static LinkPE newDirectLink(String linkName, String pageName, boolean copyPageVars) {
		return new LinkPE(linkName, ValueOrRef.newValue(pageName), Type.normal, copyPageVars);
	}
	/**
	 * Создать ссылку на страницу, название которой передается через переменную
	 * @param linkName
	 * @param pageVarName
	 * @return
	 */
	public static LinkPE newVarLink(String linkName, String pageVarName, boolean copyPageVars) {
		return new LinkPE(linkName, ValueOrRef.newRef(pageVarName), Type.normal, copyPageVars);
	}
	
	public String getElementName() {
		return ELEMENT_NAME;
	}
	@Override
	public String toString() {
		return serialize();
	}

}
