/*
 * Created on 29.09.2007
 */
package ecommander.common.exceptions;

/**
 * @author E
 */
public class EcommanderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EcommanderException(String message, Throwable cause) {
		super(message, cause);
	}

	public EcommanderException(Throwable cause) {
		super(cause);
	}

	/**
	 * 
	 */
	public EcommanderException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public EcommanderException(String arg0) {
		super(arg0);
	}

}
