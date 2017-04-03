/*
 * Created on 09.08.2007
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ecommander.persistence.mappers;

/**
 * Содержит константы для всех таблиц, которые не генерятся динамически, 
 * и все остальные константы. которые нужны для работы с базоном
 * @author E
 *
 */
public interface DBConstants
{

	/**************************************************************************************************
	 **                                 АЙТЕМЫ И ПАРАМЕТРЫ
	 **************************************************************************************************/
	
	/**
	 * Конкретный айтем
	 * Таблица служит в основном для генерации уникальных ID айтемов и для хранения родственных связей айтемов
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
	interface ItemTbl
	{
		String TABLE = "item";
		String ID = "i_id"; // ID айтема (не типа айтема, а конкретного айтема)
		String TYPE_ID = "i_type_id"; // уникальный ID типа айтема (PK)
		String KEY = "i_key"; // Название айтема, которое юзер видит в CMS
		String TRANSLIT_KEY = "i_t_key"; // Уникальный ключ айтема, который можно испльзовать для поиска вместо ID
		String PROTECTED = "i_protected"; // время последнего обновления айтема
		String PARAMS = "i_params"; // значения всех параметров айтема в виде XML
		String UPDATED = "i_updated"; // время последнего обновления айтема
		String STATUS = "ip_status"; // Показывать (0), скрытый (1), айтем удален но можно восстановить (2), айтем удален безвозвратно (3)
		String USER_ID = "i_user"; // Владелец айтема (0 - айтем принадлежит группе и не имеет владельца)
		String GROUP_ID = "i_group"; // Группа-владелец айтема
	}

	/**
	 * Таблица, в которой хранится описание модели данных в виде XML
	 */
	interface ModelXML {
		String TABLE = "model_xml";
		String NAME = "xml_name";
		String XML = "xml_xml";
	}
	/**
	 * Уникальные строковые ключи некоторых айтемов, которые можно передавать через URL с целью уникальной идентификации айтема
	 * @author E
	 *
	 */
	interface UniqueItemKeys {
		String TABLE = "unique_key";
		String ID = "uk_item_id"; // ID айтема
		String KEY = "uk_key"; // Уникальный текстовый ключ
	}
	/**
	 * Таблица, в которой хранятся пары родитель-потомок для всех возможных комбинаций айтемов родителей и айтемов потомков
	 * @author EEEE
	 *
	 */
	interface ItemParent {
		String TABLE = "item_parent";
		String ASSOC_ID = "ip_assoc_id"; // ID ассоциации
		String PARENT_ID = "ip_parent_id"; // ID предка
		String CHILD_ID = "ip_child_id"; // ID потомка
		String CHILD_SUPERTYPE = "ip_child_supertype"; // Супертип (непользовательский тип) потомка
		String PARENT_LEVEL = "ip_level"; // Уровень вложенности по отношению к предку (0 - прямой потомок, 1 - непрямой)
		String WEIGHT = "ip_weight"; // Вес прямого потомка среди всех прямых потомков предка с заданной ассоциацией
	}
	/**
	 * Параметры айтема
	 * 
	 * Всего есть 4 таблицы, по одной для каждого типа значений: строка, целое число, дробное число, ассоциация
	 * В этих таблицах поля называются одинаково, различается только название таблицы
	 * 
	 * ID айтема должен быть одинаковым во всех таблицах, в которых используется этот айтем
	 * Таблица хранит все значения всех параметров типа Integet и Long всех айтемов и используется только при поиске по фильтру 
	 * или при сортировке по значению параметра
	 * @author EEEE
	 */
	interface ItemIndexes {
		String INT_TABLE_NAME = "int_index";
		String DECIMAL_TABLE_NAME = "decimal_index";
		String DOUBLE_TABLE_NAME = "double_index";
		String STRING_TABLE_NAME = "string_index";
		String ITEM_ID = "ii_item"; // ID айтема (не типа айтема, а конкретного айтема)
		String ITEM_TYPE = "ii_type"; // ID типа айтема
		String ITEM_PARAM = "ii_param"; // ID параметра айтема (каждый параметр имеет уникальный ID вне зависимости от уникальности его названия)
		String VALUE = "ii_val"; // Занчение
	}
	/**
	 * Таблица, в которой хранятся ID всех ассоциаций
	 * Она нужна для того, чтобы генерировать уникальные ID для ассоциаций
	 * @author E
	 *
	 */
	interface AssocIds {
		String TABLE = "assoc_ids";
		String ASSOC_NAME = "aid_name";
		String ASSOC_ID = "aid_id";
	}
	/**
	 * Таблица, в которой хранятся ID всех айтемов
	 * Она нужна для того, чтобы генерировать уникальные ID для айтемов
	 * @author E
	 *
	 */
	interface ItemIds {
		String TABLE = "item_ids";
		String ITEM_NAME = "iid_name";
		String ITEM_ID = "iid_id";
	}
	/**
	 * Таблица, в которой хранятся ID всех айтемов
	 * Она нужна для того, чтобы генерировать уникальные ID для айтемов
	 * @author E
	 *
	 */
	interface ParamIds {
		String TABLE = "param_ids";
		String ITEM_ID = "pid_item_id";
		String PARAM_NAME = "pid_param_name";
		String PARAM_ID = "pid_param_id";
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
	 **                                        ЮЗЕРЫ
	 **************************************************************************************************/


	/**
	 * Группы пользователей
	 * @author E
	 */
	interface Group {
		String TABLE = "group";
		String ID = "g_id";
		String NAME = "g_name";
	}
	
	/**
	 * Таблица юзеров и их принадлежность к группам
	 * @author E
	 */
	interface UsersTbl {
		String TABLE = "users";
		String ID = "u_id";
		String LOGIN = "u_login";
		String PASSWORD = "u_password";
		String DESCRIPTION = "u_description";
	}

	/**
	 * Группы, к которым принадлежать пользователь (один может принадлежать многим группам)
	 * и роли пользователей в этих группах (простой пользователь или админ)
	 */
	interface UserGroups {
		String TABLE = "user_groups";
		String USER_ID = "ug_user_id";
		String GROUP_ID = "ug_group_id";
		String GROUP_NAME = "ug_group_name";
		String ROLE = "ug_role";
	}
}