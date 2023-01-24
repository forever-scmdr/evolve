package ecommander.persistence.mappers;

/**
 * По сравнению с суперклассом, позволяет гененировать уникальные ID для созадваемых сеансовых айтемов
 * @author EEEE
 *
 */
public abstract class SessionObjectStorage extends ParametricObjectStorage {

	private long idBase = -100000;
	/**
	 * Генерирует ID для очередного вновь создаваемого сеансового айтема
	 * @return
	 */
	public long generateId() {
		return idBase--;
	}

}
