package ecommander.pages.variables;

import java.io.UnsupportedEncodingException;
import java.util.List;

import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ValidationResults;

/**
 * Получает значение из другой переменной этой страницы
 * @author EEEE
 */
public class ReferenceVariablePE extends VariablePE {
	
	private String varReference;
	
	public ReferenceVariablePE(String varId, String varReference) {
		super(varId);
		this.varReference = varReference;
	}

	private ReferenceVariablePE(ReferenceVariablePE source, ExecutablePagePE parentPage) {
		super(source, parentPage);
		this.varReference = source.varReference;
	}

	@Override
	public boolean isEmpty() {
		return pageModel.getVariable(varReference).isEmpty();
	}

	@Override
	protected VariablePE createVarClone(ExecutablePagePE parentPage) {
		return new ReferenceVariablePE(this, parentPage);
	}

	public void validate(String elementPath, ValidationResults results) {
		if (!varReference.startsWith("$") && pageModel.getVariable(varReference) == null)
			results.addError(elementPath + " > " + getKey(), "there is no '" + varReference + "' page variable in current page");
	}

	@Override
	public String output() {
		return pageModel.getVariable(varReference).output();
	}

	@Override
	public List<String> outputArray() {
		return pageModel.getVariable(varReference).outputArray();
	}

	@Override
	public boolean isMultiple() {
		return pageModel.getVariable(varReference).isMultiple();
	}

	@Override
	public String writeInAnUrlFormat() throws UnsupportedEncodingException {
		return pageModel.getVariable(varReference).writeInAnUrlFormat();
	}

}
