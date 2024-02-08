package ecommander.pages;
/**
 * Команды и айтемы, которые хранятся в коре старницы
 * @author E
 *
 */
public interface ExecutablePE {
	ResultPE execute() throws Exception;

	/**
	 * Ключ для отладки
	 * @return
	 */
	String getDebugKey();
}
