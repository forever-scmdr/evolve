package ecommander.common.exceptions;
/**
 * Исключение для ошибок обработки пользовательского фильтра
 * @author EEEE
 *
 */
public class FilterProcessException extends EcommanderException {

	public FilterProcessException(String message, Throwable cause) {
		super(message, cause);
	}

	public FilterProcessException(Throwable cause) {
		super(cause);
	}

	public FilterProcessException(String arg0) {
		super(arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3441587136013535949L;
	
}
