# CREATE statement for itemabstract
CREATE TABLE  `assoc_ids` (
  `aid_id` int NOT NULL auto_increment,
  `aid_name` varchar(255) NOT NULL,
  PRIMARY KEY  USING BTREE (`aid_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# CREATE statement for itemabstract
CREATE TABLE  `item_ids` (
  `iid_id` int NOT NULL auto_increment,
  `iid_name` varchar(255) NOT NULL,
  PRIMARY KEY  USING BTREE (`iid_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# CREATE statement for itemparameterabstract
CREATE TABLE  `param_ids` (
  `pid_param_id` int NOT NULL auto_increment,
  `pid_item_id` int NOT NULL,
  `pid_param_name` varchar(50) NOT NULL,
  PRIMARY KEY  USING BTREE (`pid_param_id`),
  UNIQUE KEY `UNIQUE` (`pid_item_id`,`pid_param_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# CREATE statement for itemabstract
CREATE TABLE  `model_xml` (
  `xml_id` int NOT NULL auto_increment,
  `xml_name` varchar(255) NOT NULL,
  `xml_xml` LONGTEXT NOT NULL,
  PRIMARY KEY  USING BTREE (`xml_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# CREATE statement for item
CREATE TABLE `item` (
  `i_id` bigint(8) unsigned NOT NULL AUTO_INCREMENT,
  `i_type_id` int NOT NULL,
  `i_key` varchar(100) DEFAULT NULL,
  `i_t_key` varchar(100) DEFAULT NULL,
  `i_weight` int unsigned NOT NULL DEFAULT '0',
  `i_params` LONGTEXT,
  `i_updated` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`i_id`, `i_weight`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;