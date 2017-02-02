package ecommander.common.exceptions;
/**
 * Юзер с таким userName уже существует
 * @author EEEE
 *
 */
public class SpecificCommandException extends EcommanderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5583894018968145590L;
	private String pageName;
	
	public SpecificCommandException() {
		super();
	}

	public SpecificCommandException(String pageName, String message) {
		super(message);
		this.pageName = pageName;
	}

	public String getPageName() {
		return pageName;
	}
	
}
