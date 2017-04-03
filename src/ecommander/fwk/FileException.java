package ecommander.fwk;
/**
 * Исключение для ошибок обработки пользовательского фильтра
 * @author EEEE
 *
 */
public class FileException extends EcommanderException implements ErrorCodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7845957178977944631L;

	public FileException(String message, Throwable cause) {
		super(FILE_ERROR, message, cause);
	}

	public FileException(String arg0) {
		super(FILE_ERROR, arg0);
	}


	
}
