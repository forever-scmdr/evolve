package ecommander.common.exceptions;

import ecommander.pages.elements.ValidationResults;

public class ValidationException extends EcommanderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5010618375628890807L;

	private ValidationResults results;

	public ValidationException(String message, Throwable cause, ValidationResults results) {
		super(message, cause);
		this.results = results;
	}

	public ValidationException(String message, ValidationResults results) {
		super(message);
		this.results = results;
	}
	
	public ValidationResults getResults() {
		return results;
	}
}
