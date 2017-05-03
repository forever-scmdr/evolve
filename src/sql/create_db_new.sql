
-- ************************************************************
-- **********         Сама модель данных        ***************
-- ************************************************************

CREATE TABLE IF NOT EXISTS `model_xml` (
  `xml_id` int(4) NOT NULL AUTO_INCREMENT,
  `xml_name` varchar(255) NOT NULL,
  `xml_xml` longtext NOT NULL,
  PRIMARY KEY (`xml_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ************************************************************
-- **********      ID айтемов и параметров      ***************
-- ************************************************************

CREATE TABLE IF NOT EXISTS `assoc_ids` (
  `aid_id` int(4) NOT NULL AUTO_INCREMENT,
  `aid_name` varchar(100) NOT NULL,
  PRIMARY KEY (`aid_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `item_ids` (
  `iid_id` int(4) NOT NULL AUTO_INCREMENT,
  `iid_name` varchar(255) NOT NULL,
  PRIMARY KEY (`iid_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `param_ids` (
  `pid_param_id` int(4) NOT NULL AUTO_INCREMENT,
  `pid_item_id` int(4) NOT NULL,
  `pid_param_name` varchar(50) NOT NULL,
  PRIMARY KEY (`pid_param_id`) USING BTREE,
  UNIQUE KEY `UNIQUE` (`pid_item_id`,`pid_param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ************************************************************
-- **********   Айтемы и иерархия вложенности   ***************
-- ************************************************************


CREATE TABLE IF NOT EXISTS `item` (
  `i_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `i_type_id` int(10) unsigned NOT NULL,
  `i_key` varchar(100) NOT NULL,
  `i_t_key` varchar(100) NOT NULL,
  `i_status` tinyint(3) unsigned NOT NULL,
  `i_user` int(10) unsigned NOT NULL,
  `i_group` tinyint(3) unsigned NOT NULL,
  `i_protected` tinyint(3) unsigned NOT NULL,
  `i_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `i_params` longtext,
  PRIMARY KEY (`i_id`),
  KEY `MAIN` (`i_id`,`i_status`,`i_group`,`i_user`) USING BTREE,
  KEY `USER_AND_TYPE` (`i_group`,`i_user`,`i_type_id`,`i_status`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `item_parent` (
  `ip_assoc_id` tinyint(4) unsigned NOT NULL,
  `ip_parent_id` bigint(20) unsigned NOT NULL,
  `ip_child_id` bigint(20) unsigned NOT NULL,
  `ip_child_supertype` int(11) unsigned NOT NULL,
  `ip_parent_direct` tinyint(4) unsigned NOT NULL,
  `ip_weight` int(11) unsigned NOT NULL,
  PRIMARY KEY (`ip_child_id`,`ip_assoc_id`,`ip_parent_direct`,`ip_parent_id`) USING BTREE,
  KEY `MAIN` (`ip_parent_id`,`ip_assoc_id`,`ip_child_supertype`,`ip_parent_direct`,`ip_weight`) USING BTREE,
  KEY `MAX_WEIGHT` (`ip_parent_id`,`ip_assoc_id`,`ip_weight`) USING BTREE,
  CONSTRAINT `CHILD` FOREIGN KEY (`ip_child_id`) REFERENCES `item` (`i_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ************************************************************
-- **********         Индексные таблицы         ***************
-- ************************************************************


CREATE TABLE IF NOT EXISTS `decimal_index` (
  `ii_item` bigint(20) unsigned NOT NULL,
  `ii_type` int(10) unsigned NOT NULL,
  `ii_param` int(10) unsigned NOT NULL,
  `ii_val` decimal(27,6) NOT NULL,
  PRIMARY KEY (`ii_item`,`ii_param`,`ii_val`) USING BTREE,
  KEY `DI_MAIN` (`ii_param`,`ii_val`) USING BTREE,
  KEY `DI_USER_DEF` (`ii_type`,`ii_param`,`ii_val`) USING BTREE,
  CONSTRAINT `DI_ITEM` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `double_index` (
  `ii_item` bigint(20) unsigned NOT NULL,
  `ii_type` int(10) unsigned NOT NULL,
  `ii_param` int(10) unsigned NOT NULL,
  `ii_val` double NOT NULL,
  PRIMARY KEY (`ii_item`,`ii_param`,`ii_val`) USING BTREE,
  KEY `FI_MAIN` (`ii_param`,`ii_val`) USING BTREE,
  KEY `FI_USER_DEF` (`ii_type`,`ii_param`,`ii_val`) USING BTREE,
  CONSTRAINT `FI_ITEM` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `string_index` (
  `ii_item` bigint(20) unsigned NOT NULL,
  `ii_type` int(10) unsigned NOT NULL,
  `ii_param` int(10) unsigned NOT NULL,
  `ii_val` varchar(100) NOT NULL,
  PRIMARY KEY (`ii_item`,`ii_param`,`ii_val`) USING BTREE,
  KEY `SI_MAIN` (`ii_param`,`ii_val`) USING BTREE,
  KEY `SI_USER_DEF` (`ii_type`,`ii_param`,`ii_val`) USING BTREE,
  CONSTRAINT `SI_ITEM` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `int_index` (
  `ii_item` bigint(20) unsigned NOT NULL,
  `ii_type` int(10) unsigned NOT NULL,
  `ii_param` int(10) unsigned NOT NULL,
  `ii_val` bigint(20) NOT NULL,
  PRIMARY KEY (`ii_item`,`ii_param`,`ii_val`) USING BTREE,
  KEY `II_MAIN` (`ii_param`,`ii_val`) USING BTREE,
  KEY `II_USER_DEF` (`ii_type`,`ii_param`,`ii_val`) USING BTREE,
  CONSTRAINT `II_ITEM` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `unique_key` (
  `uk_item_id` bigint(20) unsigned NOT NULL,
  `uk_key` varchar(100) NOT NULL,
  PRIMARY KEY (`uk_item_id`),
  KEY `MAIN` (`uk_key`),
  CONSTRAINT `ITEM` FOREIGN KEY (`uk_item_id`) REFERENCES `item` (`i_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ************************************************************
-- **********        Пользователи и права       ***************
-- ************************************************************


CREATE TABLE IF NOT EXISTS `groups` (
  `g_id` tinyint(3) unsigned NOT NULL AUTO_INCREMENT,
  `g_name` varchar(100) NOT NULL,
  PRIMARY KEY (`g_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user` (
  `u_id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `u_login` varchar(100) NOT NULL,
  `u_password` varchar(100) NOT NULL,
  `u_description` text,
  PRIMARY KEY (`u_id`) USING BTREE,
  KEY `MAIN` (`u_login`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS `user_group` (
  `ug_user_id` int(10) unsigned NOT NULL,
  `ug_group_id` tinyint(3) unsigned NOT NULL,
  `ug_group_name` varchar(100) NOT NULL,
  `ug_role` tinyint(3) unsigned NOT NULL,
  PRIMARY KEY (`ug_user_id`,`ug_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;