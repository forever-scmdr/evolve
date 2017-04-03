package ecommander.fwk;

public class UserNotAllowedException extends EcommanderException implements ErrorCodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1251847409013406008L;

	public UserNotAllowedException(String message, Throwable cause) {
		super(USER_NOT_ALLOWED, message, cause);
	}

	public UserNotAllowedException(String arg0) {
		super(USER_NOT_ALLOWED, arg0);
	}
}
