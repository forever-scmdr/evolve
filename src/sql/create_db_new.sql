CREATE TABLE `assoc_ids` (
  `aid_id` TINYINT(3) UNSIGNED NOT NULL AUTO_INCREMENT,
  `aid_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`aid_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `item_ids` (
  `iid_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
  `iid_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`iid_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `param_ids` (
  `pid_param_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `pid_item_id` INT(10) UNSIGNED NOT NULL,
  `pid_param_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`pid_param_id`) USING BTREE,
  UNIQUE INDEX `UNIQUE` (`pid_item_id`, `pid_param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `item` (
  `i_id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `i_type_id` INT(10) UNSIGNED NOT NULL,
  `i_key` VARCHAR(100) NOT NULL,
  `i_t_key` VARCHAR(100) NOT NULL,
  `i_weight` INT(10) UNSIGNED NOT NULL DEFAULT '0',
  `i_params` LONGTEXT NULL,
  `i_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`i_id`, `i_weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `item_parent` (
  `ip_assoc_id` TINYINT(3) UNSIGNED NOT NULL,
  `ip_parent_id` BIGINT(20) UNSIGNED NOT NULL,
  `ip_child_id` BIGINT(20) UNSIGNED NOT NULL,
  `ip_child_supertype` INT(10) UNSIGNED NOT NULL,
  `ip_level` TINYINT(3) UNSIGNED NOT NULL,
  `ip_show` TINYINT(3) UNSIGNED NOT NULL,
  `ip_user` INT(10) UNSIGNED NOT NULL,
  `ip_group` TINYINT(3) UNSIGNED NOT NULL,
  PRIMARY KEY (`ip_child_id`, `ip_assoc_id`, `ip_show`, `ip_level`, `ip_parent_id`),
  INDEX `BASE_SEARCH` (`ip_parent_id`, `ip_child_supertype`, `ip_assoc_id`, `ip_show`, `ip_level`, `ip_group`, `ip_user`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `int_index` (
  `ii_param` INT(10) UNSIGNED NOT NULL,
  `ii_val` BIGINT(20) NOT NULL,
  `ii_type` INT(10) UNSIGNED NOT NULL,
  `ii_val_idx` TINYINT(3) UNSIGNED NOT NULL,
  `ii_item` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`ii_item`, `ii_param`, `ii_val_idx`),
  INDEX `BASE_SEARCH` (`ii_param`, `ii_val`) USING BTREE,
  INDEX `USER_SEARCH` (`ii_type`, `ii_param`, `ii_val`) USING BTREE,
  CONSTRAINT `DELETE_ITEM_INT` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `double_index` (
  `ii_param` INT(10) UNSIGNED NOT NULL,
  `ii_val` DOUBLE NOT NULL,
  `ii_type` INT(10) UNSIGNED NOT NULL,
  `ii_val_idx` TINYINT(3) UNSIGNED NOT NULL,
  `ii_item` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`ii_item`, `ii_param`, `ii_val_idx`),
  INDEX `BASE_SEARCH` (`ii_param`, `ii_val`) USING BTREE,
  INDEX `USER_SEARCH` (`ii_type`, `ii_param`, `ii_val`) USING BTREE,
  CONSTRAINT `DELETE_ITEM_DOUBLE` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `decimal_index` (
  `ii_param` INT(10) UNSIGNED NOT NULL,
  `ii_val` DECIMAL(27,6) NOT NULL,
  `ii_type` INT(10) UNSIGNED NOT NULL,
  `ii_val_idx` TINYINT(3) UNSIGNED NOT NULL,
  `ii_item` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`ii_item`, `ii_param`, `ii_val_idx`),
  INDEX `BASE_SEARCH` (`ii_param`, `ii_val`) USING BTREE,
  INDEX `USER_SEARCH` (`ii_type`, `ii_param`, `ii_val`) USING BTREE,
  CONSTRAINT `DELETE_ITEM_DECIMAL` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `string_index` (
  `ii_param` INT(10) UNSIGNED NOT NULL,
  `ii_val` VARCHAR(100) NOT NULL,
  `ii_type` INT(10) UNSIGNED NOT NULL,
  `ii_val_idx` TINYINT(3) UNSIGNED NOT NULL,
  `ii_item` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`ii_item`, `ii_param`, `ii_val_idx`),
  INDEX `BASE_SEARCH` (`ii_param`, `ii_val`) USING BTREE,
  INDEX `USER_SEARCH` (`ii_type`, `ii_param`, `ii_val`) USING BTREE,
  CONSTRAINT `DELETE_ITEM_STRING` FOREIGN KEY (`ii_item`) REFERENCES `item` (`i_id`) ON UPDATE CASCADE ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `user_group` (
  `ug_id` TINYINT(3) UNSIGNED NOT NULL AUTO_INCREMENT,
  `ug_name` VARCHAR(100) NOT NULL,
  PRIMARY KEY (`ug_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `users` (
  `u_id` INT(11) NOT NULL AUTO_INCREMENT,
  `u_group` TINYINT(4) NOT NULL,
  `u_login` VARCHAR(50) NOT NULL,
  `u_password` VARCHAR(50) NOT NULL,
  `u_description` VARCHAR(250) NOT NULL,
  PRIMARY KEY (`u_id`),
  INDEX `LOGIN` (`u_login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';


CREATE TABLE `model_xml` (
  `xml_id` INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
  `xml_name` VARCHAR(255) NOT NULL,
  `xml_xml` LONGTEXT NOT NULL,
  PRIMARY KEY (`xml_id`) USING BTREE,
  UNIQUE INDEX `FILE` (`xml_name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE='utf8_general_ci';