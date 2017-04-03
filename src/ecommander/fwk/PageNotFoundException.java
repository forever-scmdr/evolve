package ecommander.fwk;
/**
 * Эксэпшен, если запрашиваемая страница не найдена в модели страниц
 * @author EEEE
 *
 */
public class PageNotFoundException extends EcommanderException implements ErrorCodes {

	
	public PageNotFoundException(String message) {
		super(PAGE_NOT_FOUND, message);
	}
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6943887219037084823L;

}
