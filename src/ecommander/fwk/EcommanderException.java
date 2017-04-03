/*
 * Created on 29.09.2007
 */
package ecommander.fwk;

/**
 * @author E
 */
public class EcommanderException extends Exception {

	private int errorCode = 0;

	public EcommanderException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}
	/**
	 * @param arg0
	 */
	public EcommanderException(int errorCode, String arg0) {
		super(arg0);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
