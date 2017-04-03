package ecommander.fwk;
/**
 * Исключение для ошибок обработки пользовательского фильтра
 * @author EEEE
 *
 */
public class FilterProcessException extends EcommanderException implements ErrorCodes {

	public FilterProcessException(String message, Throwable cause) {
		super(FILTER_FORMAT_ERROR, message, cause);
	}

	public FilterProcessException(String arg0) {
		super(FILTER_FORMAT_ERROR, arg0);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3441587136013535949L;
	
}
