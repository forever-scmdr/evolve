package ecommander.fwk;

import ecommander.pages.ValidationResults;
import net.sf.saxon.trans.Err;

public class ValidationException extends EcommanderException implements ErrorCodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5010618375628890807L;

	private ValidationResults results;

	public ValidationException(String message, Throwable cause, ValidationResults results) {
		super(VALIDATION_FAILED, message, cause);
		this.results = results;
	}

	public ValidationException(String message, ValidationResults results) {
		super(VALIDATION_FAILED, message);
		this.results = results;
	}
	
	public ValidationResults getResults() {
		return results;
	}
}
