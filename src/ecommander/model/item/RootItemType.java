package ecommander.model.item;

import ecommander.common.Strings;


/**
 * Корневой айтем для определенной группы ItemDescription
 * Корневой айтем определяет где хранятся его сабайтемы, каким группам пользователей разрешено его 
 * администрировать
 * Также корневой айтем хранит свой ID в базе данных, как обычный Item (НЕ ItemDescription)
 * @author EEEE
 *
 */
public class RootItemType extends ItemTypeContainer {
	
	public static final String ROOT_PREFIX = "_root_";
	
	private String group;
	private long id; // ID айтема в базоне, нужен для того, чтобы можно было создавать потомков корневых айтемов

	public RootItemType(String group, long itemId) {
		this.group = group;
		this.id = itemId;
	}
	/**
	 * Получить имя группы, которой принадлежит корневой элемент
	 * @return
	 */
	public String getGroup() {
		return group;
	}
	/**
	 * Получить ID айтема в БД, которому соответствует этот корневой элемент
	 * @return
	 */
	public long getItemId() {
		return id;
	}
	/**
	 * Получить путь (для файлов)
	 * @return
	 */
	public String getPredecessorPath() {
		return id + Strings.SLASH;
	}
	/**
	 * Создать название корневого элемента по его атрибутам
	 */
	public static String createRootName(String groupName) {
		return ROOT_PREFIX + groupName;
	}
	@Override
	public String getName() {
		return createRootName(group);
	}
	
}