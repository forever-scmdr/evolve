package ecommander.fwk;

import ecommander.pages.ValidationResults;


/**
 * Абстрактный валидатор
 * @author EEEE
 *
 */
public abstract class ModelValidator {

	private ValidationResults results = new ValidationResults();
	
	protected ModelValidator() {

	}
	
	protected final void addError(String message, int lineNumber) {
		results.addError(lineNumber, message);
	}
	
	public boolean isSuccessful() {
		return results.isSuccessful();
	}
	
	public ValidationResults getResults() {
		return results;
	}
	
	public abstract void validate();
}
