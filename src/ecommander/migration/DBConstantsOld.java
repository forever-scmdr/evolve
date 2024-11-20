/*
 * Created on 09.08.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ecommander.migration;

/**
 * Содержит константы для всех таблиц, которые не генерятся динамически, 
 * и все остальные константы. которые нужны для работы с базоном
 * @author E
 *
 */
public class DBConstantsOld
{
	
	public static final String ROOT_PREFIX = "_root_";
	public static final byte QUANTIFIER_SINGLE = 1;
	public static final byte QUANTIFIER_MULTIPLE = 2;
	public static final byte VIRTUALITY_REAL = 4;
	public static final byte VIRTUALITY_VIRTUAL = 8;
	
	public static final byte VIRTUALITY_MASK = 12;
	public static final byte QUANTIFIER_MASK = 3;
	
	/**************************************************************************************************
	 **                                 АЙТЕМЫ И ПАРАМЕТРЫ
	 **************************************************************************************************/
	
	/**
	 * Таблица абстрактого айтема.
	 * Содержит только ID и название и описание айтема
	 * @author E
	 */
	public interface ItemAbstract
	{
		String TABLE = "ItemAbstract";
		String ITEM_TYPE_NAME = "IA_TYPE_NAME"; // уникальное название типа айтема (имя тэга в XML)
		String ITEM_TYPE_ID = "IA_TYPE_ID"; // уникальный ID айтема
		String CAPTION = "IA_CAPTION"; // Простое текстовое название
		String DESCRIPTION = "IA_DESCRIPTION"; // Простое текстовое описание
		String KEY_PARAMETER = "IA_KEY_PARAM"; // Ключевой параметр (главный, который характеризует айтем)
		String PARAMETER_ORDER = "IA_PARAMETER_ORDER"; // Порядок следования параметров айтема, разделенные запятой
		String FLAGS = "IA_FLAGS"; // Является ли айтем виртуальным, т. е. нужно ли для него создавать отдельную таблицу
	}

	/**
	 * Таблица наследования айтемов.
	 * Содержит название айтема и название его предка
	 * @author EEEE
	 */
	public interface ItemExtensions
	{
		String TABLE = "ItemExtensions";
		String ITEM_TYPE_NAME = "IE_TYPE_NAME"; // название айтема
		String EXTENSION_TYPE_NAME = "IE_EXTENSION_TYPE_NAME"; // название прямого потомка айтема в иерархии наследования
	}

	/**
	 * Таблица, которая отражает родственные связи айтемов и их множественность
	 * Содержит только ID и ID родителя, а также квантифаер, проперти и персистенс
	 * @author E
	 */	
	public interface ItemParentAbstract
	{
		String TABLE = "ItemParentAbstract";
		String ITEM_TYPE_NAME = "IPA_TYPE_NAME"; // уникальное название типа айтема (имя тэга в XML)
		String PARENT_TYPE_NAME = "IPA_PARENT_TYPE_NAME"; // название предка айтема (типа айтема)
		String SUBITEM_FLAGS = "IPA_FLAGS"; // Одиночный или множественный сабайтем, виртуальный или реальный
	}
	
	/**
	 * Таблица абстрактных параметров айтема
	 * @author E
	 */
	public interface ItemParameterAbstract
	{
		String TABLE = "ItemParameterAbstract";
		String ITEM_TYPE_ID = "IRA_TYPE_ID"; // уникальное имя типа айтема, которому принадлежит этот параметр
		String PARAMETER_NAME = "IRA_PARAM_NAME";
		String PARAMETER_ID = "IRA_PARAM_ID"; // Численный ID параметра для ускорения поиска по таблице ItemIndex
		String PARAMETER_TYPE = "IRA_PARAM_TYPE"; // тип данных паратеметра
		String PARAMETER_QUANTIFIER = "IRA_PARAM_QUANTIFIER";
		String PARAMETER_CAPTION = "IRA_PARAM_CAPTION";
		String PARAMETER_DESCRIPTION = "IRA_PARAM_DESCRIPTION";
		String PARAMETER_FORMAT = "IRA_PARAM_FORMAT"; // формат параметра
		String PARAMETER_DOMAIN = "IRA_PARAM_DOMAIN"; // название домена
		//String PARAMETER_INDEX = "IRA_PARAM_INDEX"; // Порядковый номер параметра, для удобства
		//String PARAMETER_SORT_DIRECTION = "PARAM_SORT_DIRECTION"; // если множественный параметр, то направление сортировки
		//сделать отдельно во фреймворке для вывода параметров (уровень страницы а не модели данных)
	}
	
	/**
	 * Конкретный айтем
	 * Таблица служит в основном для геенрации уникальных ID айтемов и для хранения родственных связей айтемов
	 * PK - TYPE_NAME и PARENT_ID, т. к. в разных разделах могут быть одинаковые сабайтемы
	 * 
	 * Есть два типа айтемов, обычные и ссылочные.
	 * Обычные айтемы имеют соответствующие записи в таблице парамтеров
	 * Айтемы-ссылки хранят ссылку на ID другого айтема. Таким образом, один и тот же айтем может участвовать в разных
	 * иерархиях (разных каталогах, отношениях parent-child)
	 * При создании нормального айтема REF_ID = ID
	 * REF_ID всегда используется для объединения с таблицами парамтеров и таблицами сабайтемов (т. к. ссылки сами по себе не содержат сабайтемы)
	 * ID используется для удаления айтема
	 * 
	 * @author E
	 */
	public interface Item
	{
		String TABLE = "Item";
		String TYPE_NAME = "I_TYPE_NAME"; // уникальное имя типа айтема (UNIQUE)
		String TYPE_ID = "I_TYPE_ID"; // уникальный ID типа айтема (PK)
		String KEY = "I_KEY"; // Название айтема, которое юзер видит в CMS
		String TRANSLIT_KEY = "I_T_KEY"; // Название айтема, которое юзер видит в CMS
		String ID = "I_ID"; // ID айтема (не типа айтема, а конкретного айтема)
		String DIRECT_PARENT_ID = "I_PARENT_ID"; // ID непосредственного предка айтема
		String REF_ID = "I_REF_ID"; // Для нормальных айтемов REF_ID = ID, а для айтемов-ссылок - ID айтема, на который идет ссылка
		String INDEX_WEIGHT = "I_WEIGHT"; // порядковый номер (вес с разницей 100 по умолчаню) в списке всех потомков одного родителя (для сортировки)
		String OWNER_GROUP_ID = "I_OWNER_GROUP_ID"; // Группа владельца айтема
		String OWNER_USER_ID = "I_OWNER_USER_ID"; // Юзер - владелец айтема
		String PRED_ID_PATH = "I_PRED_ID_PATH"; // Путь к текущему айтему через всех его предшественников (для работы с файлами)
		String PARAMS = "I_PARAMS"; // значения всех параметров айтема в виде XML
	}

	/**
	 * Таблица, в которой хранятся пары родитель-потомок для всех возможных комбинаций айтемов родителей и айтемов потомков
	 * @author EEEE
	 *
	 */
	public interface ItemParent {
		String TABLE = "ItemParent";
		String REF_ID = "IP_REF_ID";
		String PARENT_ID = "IP_PARENT_ID";
		String ITEM_ID = "IP_ITEM_ID";
		String PARENT_LEVEL = "IP_LEVEL"; // Уровень вложенности по отношению к предку
	}
	/**
	 * Параметры айтема
	 * 
	 * Всего есть 3 таблицы, по одной для каждого типа значений: строка, целое число, дробное число
	 * В этих таблицах поля называются одинаково, различается только название таблицы
	 * 
	 * ID айтема должен быть одинаковым во всех таблицах, в которых используется этот айтем
	 * Таблица хранит все значения всех параметров типа Integet и Long всех айтемов и используется только при поиске по фильтру 
	 * или при сортировке по значению параметра
	 * @author EEEE
	 * TODO <enhance> !!! Использовать в запросах фильтрации II_TYPE - меньше элементов остается после выбора из этой таблицы
	 */
	public interface ItemIndexes
	{
		String INT_TABLE_NAME = "IntIndex";
		String DOUBLE_TABLE_NAME = "DoubleIndex";
		String STRING_TABLE_NAME = "StringIndex";
		String REF_ID = "II_REF_ID"; // ID айтема (не типа айтема, а конкретного айтема)
		String ITEM_PARAM = "II_PARAM"; // ID параметра айтема (каждый параметр имеет уникальный ID вне зависимости от уникальности его названия)
		String ITEM_TYPE = "II_TYPE"; // ID типа айтема
		String VALUE = "II_VAL"; // Занчение (одн
	}
	
	/**************************************************************************************************
	 **                                        СТРАНИЦЫ
	 **************************************************************************************************/
	
	/**
	 * Для страниц нет таблиц в базе.
	 * Структура страниц берется из XML файла при старте сервера и кэшируется в 
	 * оперативной памяти в виде специальных объектов
	 */
	
	
	/**************************************************************************************************
	 **                                        Домены
	 **************************************************************************************************/
	
	/**
	 * Таблицы для доменов. Так как они отвечают только
	 * за представление информации и удобство пользователя и не участвуют в процессах бизнес логики, то расположены отдельно. 
	 */

	/**
	 * Таблица доменов без значений
	 * @author E
	 */
	public interface DomainAbstract
	{
		String TABLE = "DomainAbstract";
		String NAME = "D_NAME"; // уникальное имя домена
		String FORMAT = "D_FORMAT"; // формат домена, аналогично формату параметра. 
									// Нужен, т. к. домен иногда будет выводиться независимо от айтемов (в админке, например)
		String VIEW = "D_VIEW"; // Вид, в котором выводится домен (комбобокс, чекбокс, радиогруп)
	}

	/**
	 * Поскольку использовать домены будет только пользователь (они нужны только для удобства пользователя и в логике не участвуют),
	 * значения любых типов данных хранятся в виде строки
	 * @author E
	 */
	public interface DomainValues
	{
		String TABLE = "DomainValues";
		String DOMAIN_NAME = "DV_DOMAIN_NAME"; // название домена
		String VALUE = "DV_VALUE"; // значение домена
		String INDEX = "DV_INDEX"; // порядковый номер значения домена
	}

	/**************************************************************************************************
	 **                                        ЮЗЕРЫ
	 **************************************************************************************************/

	/**
	 * Для юзеров тоже надо делать отдельный файл, так же как и для айтемов и старниц.
	 * Всего получается 3 настроечных файла - юзеры, айтемы и страницы.
	 * В файле юзеров определяются группы, айтемы, которые могут просматривать либо редактировать эти группы
	 * и юзеры, которые входят в эти группы
	 * @author E
	 */

	/**
	 * Группы пользователей
	 * @author E
	 */
	public interface UserGroup
	{
		String TABLE = "UserGroup";
		String ID = "UG_ID";
		String NAME = "UG_NAME";
	}
	
	/**
	 * Таблица юзеров и их принадлежность к группам
	 * @author E
	 */
	public interface Users
	{
		String TABLE = "Users";
		String ID = "U_ID";
		String GROUP = "U_GROUP";
		String LOGIN = "U_LOGIN";
		String PASSWORD = "U_PASSWORD";
		String DESCRIPTION = "U_DESCRIPTION";
	}
	
//	/**
//	 * Таблица разрешений на каждый айтем, доступ к которому отличается от доступа по умолчанию
//	 * !!! НА БУДУЩЕЕ !!!
//	 * @author E
//	 */
//	public interface Permissions
//	{
//		String TABLE = "Permissions";
//		String ITEM_TYPE_ID = "S_ITEM_TYPE_ID";
//		String GROUP = "S_GROUP";
//		String PERMISSION = "S_PERMISSION"; // может быть только READ или WRITE. По умолчанию все айтемы только READ.
//	}
//	
}