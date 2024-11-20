package ecommander.pages.elements.variables;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

import ecommander.pages.elements.ExecutablePagePE;
/**
 * Неитерируемая версия переменной параметра страничного айтема
 * Метод output выводит не одно, а все значения параметра через запятую
 * 
 * @author E
 *
 */
public class StaticParameterVariablePE extends ParameterVariablePE {

	public StaticParameterVariablePE(String varId, String pageItemId, String paramName) {
		super(varId, pageItemId, paramName);
	}
	
	private StaticParameterVariablePE(ParameterVariablePE source, ExecutablePagePE parentPage) {
		super(source, parentPage);
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		return new StaticParameterVariablePE(this, parentPage);
	}

	@Override
	public String output() {
		return StringUtils.join(outputArray(), ',');
	}

	@Override
	public String writeInAnUrlFormat() throws UnsupportedEncodingException {
		if (!isMultiple())
			return super.writeInAnUrlFormat();
		return StaticVariablePE.writeMultipleVariableInAnUrlFormat(this);
	}
	
	
}
