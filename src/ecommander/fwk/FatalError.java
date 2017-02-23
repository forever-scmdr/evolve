package ecommander.fwk;

/**
 * Ошибка, которая не совместима с дальнейшей работой приложения
 * При ее возникновении выводится отдельное окно с ошибкой
 * @author EEEE
 *
 */
public class FatalError extends EcommanderException {

	private static final long serialVersionUID = 1849938205052253584L;

	public FatalError() {
		super();
	}

	public FatalError(String arg0) {
		super(arg0);
	}
	
}
