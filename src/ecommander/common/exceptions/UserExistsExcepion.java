package ecommander.common.exceptions;
/**
 * Юзер с таким userName уже существует
 * @author EEEE
 *
 */
public class UserExistsExcepion extends EcommanderException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5583894018968145590L;
	private String userName;
	
	public UserExistsExcepion() {
		super();
	}

	public UserExistsExcepion(String userName) {
		super(userName);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
	
}
