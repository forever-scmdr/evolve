CREATE TABLE `DomainAbstract` (
  `D_NAME` varchar(50) NOT NULL,
  `D_PARAM_TYPE` varchar(30) NOT NULL,
  `D_CAPTION` varchar(100) NOT NULL,
  `D_DESCRIPTION` text,
  `D_PARAM_FORMAT` varchar(30) default NULL,
  PRIMARY KEY  (`D_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Item` (
  `I_TYPE_NAME` varchar(50) NOT NULL,
  `I_ID` bigint(8) unsigned NOT NULL auto_increment,
  `I_PARENT_ID` bigint(1) unsigned NOT NULL,
  `I_TREE_LEFT` bigint(8) NOT NULL,
  `I_TREE_LEVEL` bigint(8) NOT NULL,
  `I_TREE_RIGHT` bigint(8) NOT NULL,
  `I_CHILD_INDEX` bigint(8) NOT NULL default '0',
  PRIMARY KEY  (`I_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ItemAbstract` (
  `IA_TYPE_NAME` varchar(50) NOT NULL,
  `IA_CAPTION` varchar(100) default NULL,
  `IA_DESCRIPTION` text,
  `IA_KEY_PARAM` varchar(50) default NULL,
  PRIMARY KEY  (`IA_TYPE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ItemExtensions` (
  `IE_TYPE_NAME` varchar(30) NOT NULL,
  `IA_EXTENSION_TYPE_NAME` varchar(30) NOT NULL,
  PRIMARY KEY  (`IE_TYPE_NAME`,`IA_EXTENSION_TYPE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ItemParameterAbstract` (
  `IRA_TYPE_NAME` varchar(50) NOT NULL,
  `IRA_PARAM_NAME` varchar(50) NOT NULL,
  `IRA_PARAM_TYPE` varchar(20) NOT NULL,
  `IRA_PARAM_QUANTIFIER` tinyint(1) NOT NULL,
  `IRA_PARAM_GROUP` tinyint(1) NOT NULL,
  `IRA_PARAM_CAPTION` varchar(100) default NULL,
  `IRA_PARAM_DESCRIPTION` text,
  `IRA_PARAM_DOMAIN` varchar(50) default NULL,
  `IRA_PARAM_FORMAT` varchar(20) default NULL,
  PRIMARY KEY  (`IRA_TYPE_NAME`,`IRA_PARAM_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ItemParentAbstract` (
  `IPA_TYPE_NAME` varchar(50) NOT NULL,
  `IPA_PARENT_TYPE_NAME` varchar(50) NOT NULL,
  `IPA_QUANTIFIER` tinyint(1) NOT NULL,
  `IPA_PROPERTY` tinyint(1) NOT NULL default '0',
  `IPA_PERSISTENCE` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`IPA_TYPE_NAME`,`IPA_PARENT_TYPE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Permissions` (
  `Id` int(6) unsigned NOT NULL,
  PRIMARY KEY  (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `UserGroup` (
  `U_ID` bigint(8) unsigned NOT NULL auto_increment,
  `U_NAME` varchar(50) NOT NULL,
  PRIMARY KEY  (`U_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Users` (
  `US_GROUP` varchar(50) NOT NULL,
  `US_LOGIN` varchar(50) NOT NULL,
  `US_PASSWORD` varchar(50) default NULL,
  `US_DESCRIPTION` text,
  `US_ID` bigint(8) NOT NULL auto_increment,
  PRIMARY KEY  (`US_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;