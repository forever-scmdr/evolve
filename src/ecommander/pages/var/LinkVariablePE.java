package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;

/**
 * Переменная, которая используется в ссылках (элементах <link/>)
 * Created by E on 9/6/2017.
 */
public class LinkVariablePE extends VariablePE {

	public static final String LINK_VARIABLE = "link_variable";

	private String refItem;
	private String refParam;
	private String refVar;
	private String value;

	private Variable var;

	private LinkVariablePE(String varName, Style style, String refItem, String refParam, String refVar, String value) {
		super(varName, style);
		this.refItem = refItem;
		this.refParam = refParam;
		this.refVar = refVar;
		this.value = value;
	}

	public static LinkVariablePE createVarVar(String varName, Style style, String refVar) {
		return new LinkVariablePE(varName, style, null, null, refVar, null);
	}

	public static LinkVariablePE createCommon(String varName, String styleStr, String refItem, String refParam, String refVar, String value) {
		return new LinkVariablePE(varName, Style.getValue(styleStr), refItem, refParam, refVar, value);
	}

	@Override
	public Variable getVariable() {
		return var;
	}

	@Override
	public boolean isEmpty() {
		return var == null || var.isEmpty();
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		LinkVariablePE clone = new LinkVariablePE(name, style, refItem, refParam, refVar, value);
		// Ссылка на другую переменную
		if (StringUtils.isNotBlank(refVar)) {
			clone.var = parentPage.getVariable(refVar);
		}
		// Ссылка на айтем
		else if (StringUtils.isNotBlank(refItem)) {
			clone.var = new ItemVariable(refItem, refParam, parentPage);
			if (style == Style.translit)
				((ItemVariable) clone.var).setTranslit(true);
		}
		// Статическое значение
		else if (StringUtils.isNotBlank(value)) {
			clone.var = new StaticVariable(name, value);
		}
		return clone;
	}

	@Override
	public String getElementName() {
		return LINK_VARIABLE;
	}
}
