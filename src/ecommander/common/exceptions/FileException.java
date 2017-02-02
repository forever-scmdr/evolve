package ecommander.common.exceptions;
/**
 * Исключение для ошибок обработки пользовательского фильтра
 * @author EEEE
 *
 */
public class FileException extends EcommanderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7845957178977944631L;

	public FileException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileException(Throwable cause) {
		super(cause);
	}

	public FileException(String arg0) {
		super(arg0);
	}


	
}
