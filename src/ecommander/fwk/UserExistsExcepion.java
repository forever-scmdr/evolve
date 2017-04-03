package ecommander.fwk;
/**
 * Юзер с таким userName уже существует
 * @author EEEE
 *
 */
public class UserExistsExcepion extends EcommanderException implements ErrorCodes {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5583894018968145590L;
	private String userName;

	public UserExistsExcepion(String userName) {
		super(USER_ALREADY_EXISTS, userName);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
	
}
