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
		String ITEM = "item";
		String I_ID = "i_id"; // ID айтема (не типа айтема, а конкретного айтема)
		String I_SUPERTYPE = "i_supertype"; // уникальный ID супертипа (непользовательского типа) айтема (PK)
		String I_TYPE_ID = "i_type_id"; // уникальный ID типа айтема
		String I_KEY = "i_key"; // Название айтема, которое юзер видит в CMS
		String I_T_KEY = "i_t_key"; // Уникальный ключ айтема, который можно испльзовать для поиска вместо ID
		String I_PROTECTED = "i_protected"; // время последнего обновления айтема
		String I_PARAMS = "i_params"; // значения всех параметров айтема в виде XML
		String I_UPDATED = "i_updated"; // время последнего обновления айтема
		String I_STATUS = "i_status"; // Показывать (0), скрытый (1), айтем удален но можно восстановить (2), айтем удален безвозвратно (3)
		String I_USER = "i_user"; // Владелец айтема (0 - айтем принадлежит группе и не имеет владельца)
		String I_GROUP = "i_group"; // Группа-владелец айтема
	}

	/**
	 * Таблица, в которой хранится описание модели данных в виде XML
	 */
	interface ModelXML {
		String MODEL_XML = "model_xml";
		String XML_NAME = "xml_name";
		String XML_XML = "xml_xml";
	}
	/**
	 * Уникальные строковые ключи некоторых айтемов, которые можно передавать через URL с целью уникальной идентификации айтема
	 * @author E
	 *
	 */
	interface UniqueItemKeys {
		String UNIQUE_KEY = "unique_key";
		String UK_ID = "uk_item_id"; // ID айтема
		String UK_KEY = "uk_key"; // Уникальный текстовый ключ
	}
	/**
	 * Таблица, в которой хранятся пары родитель-потомок для всех возможных комбинаций айтемов родителей и айтемов потомков
	 * @author EEEE
	 *
	 */
	interface ItemParent {
		String ITEM_PARENT = "item_parent";
		String IP_ASSOC_ID = "ip_assoc_id"; // ID ассоциации
		String IP_PARENT_ID = "ip_parent_id"; // ID предка
		String IP_CHILD_ID = "ip_child_id"; // ID потомка
		String IP_CHILD_SUPERTYPE = "ip_child_supertype"; // Супертип (непользовательский тип) потомка
		String IP_PARENT_DIRECT = "ip_parent_direct"; // Уровень вложенности по отношению к предку (1 - прямой потомок, 0 - непрямой)
		String IP_WEIGHT = "ip_weight"; // Вес прямого потомка среди всех прямых потомков предка с заданной ассоциацией
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
		String INT_INDEX = "int_index";
		String DECIMAL_INDEX = "decimal_index";
		String DOUBLE_INDEX = "double_index";
		String STRING_INDEX = "string_index";
		String II_ITEM_ID = "ii_item"; // ID айтема (не типа айтема, а конкретного айтема)
		String II_ITEM_TYPE = "ii_type"; // ID типа айтема
		String II_PARAM = "ii_param"; // ID параметра айтема (каждый параметр имеет уникальный ID вне зависимости от уникальности его названия)
		String II_VALUE = "ii_val"; // Занчение
	}
	/**
	 * Таблица, в которой хранятся ID всех ассоциаций
	 * Она нужна для того, чтобы генерировать уникальные ID для ассоциаций
	 * @author E
	 *
	 */
	interface AssocIds {
		String ASSOC_IDS = "assoc_ids";
		String AID_ASSOC_NAME = "aid_name";
		String AID_ASSOC_ID = "aid_id";
	}
	/**
	 * Таблица, в которой хранятся ID всех айтемов
	 * Она нужна для того, чтобы генерировать уникальные ID для айтемов
	 * @author E
	 *
	 */
	interface ItemIds {
		String ITEM_IDS = "item_ids";
		String IID_ITEM_NAME = "iid_name";
		String IID_ITEM_ID = "iid_id";
	}
	/**
	 * Таблица, в которой хранятся ID всех айтемов
	 * Она нужна для того, чтобы генерировать уникальные ID для айтемов
	 * @author E
	 *
	 */
	interface ParamIds {
		String PARAM_IDS = "param_ids";
		String PID_ITEM_ID = "pid_item_id";
		String PID_PARAM_NAME = "pid_param_name";
		String PID_PARAM_ID = "pid_param_id";
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
		String GROUPS = "groups";
		String G_ID = "g_id";
		String G_NAME = "g_name";
	}
	
	/**
	 * Таблица юзеров и их принадлежность к группам
	 * @author E
	 */
	interface UsersTbl {
		String USER = "user";
		String U_ID = "u_id";
		String U_LOGIN = "u_login";
		String U_PASSWORD = "u_password";
		String U_DESCRIPTION = "u_description";
	}

	/**
	 * Группы, к которым принадлежать пользователь (один может принадлежать многим группам)
	 * и роли пользователей в этих группах (простой пользователь или админ)
	 */
	interface UserGroups {
		String USER_GROUP = "user_group";
		String UG_USER_ID = "ug_user_id";
		String UG_GROUP_ID = "ug_group_id";
		String UG_GROUP_NAME = "ug_group_name";
		String UG_ROLE = "ug_role";
	}

	/**************************************************************************************************
	 **                         ЛОГ ИЗМЕНЕНИЯ АЙЕТМОВ С COMPUTED ПАРАМТЕРАМИ
	 **************************************************************************************************/

	interface ComputedLog {
		String COMPUTED_LOG = "computed_log";
		String L_ITEM = "l_item";
	}
}