package ecommander.pages.var;

import ecommander.pages.ExecutablePagePE;
import org.apache.commons.lang3.StringUtils;

/**
 * Переменная, которая используется в ссылках (элементах <link/>)
 * Created by E on 9/6/2017.
 */
public class LinkVariablePE extends VariablePE {

	private String refItem;
	private String refParam;
	private String refVar;
	private String value;

	private Variable var;

	public LinkVariablePE(String varName, Style style, String refItem, String refParam, String refVar, String value) {
		super(varName, style);
		this.refItem = refItem;
		this.refParam = refParam;
		this.refVar = refVar;
		this.value = value;
	}

	@Override
	protected Variable getVariable() {
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
			clone.var = parentPage.getVariable(refVar).getVariable();
		}
		// Ссылка на айтем
		else if (StringUtils.isNotBlank(refItem)) {
			clone.var = new ItemVariable(parentPage, refItem, refParam);
		}
		// Статическое значение
		else if (StringUtils.isNotBlank(value)) {
			clone.var = new StaticVariable(name, value);
		}
		return clone;
	}
}
