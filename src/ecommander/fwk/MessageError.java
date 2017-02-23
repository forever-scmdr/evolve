package ecommander.fwk;

/**
 * Ошбика, которая не является критической.
 * При ее возникновении пользователю выдается нормальная страница 
 * (которая должна была выводится если бы не было ошибок), но с возможностью вывода
 * сообщения об ошибке
 * @author EEEE
 *
 */
public class MessageError extends EcommanderException {

	private static final long serialVersionUID = -4719592998720627367L;

	private String message;
	
	public MessageError() {
		super();
	}

	public MessageError(String logMessage, String userMessage) {
		super(logMessage);
		message = userMessage;
	}
	
	public String getUserErrorMessage() {
		return message;
	}
}
