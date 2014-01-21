CREATE TABLE `devices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `netplug_id` varchar(64) DEFAULT NULL,
  `last_access` timestamp NULL DEFAULT NULL,
  `secret` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `netplug_id_UNIQUE` (`netplug_id`)
) ENGINE=MyISAM AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;


CREATE TABLE `commands` (
  `id` int(11) NOT NULL,
  `device` int(11) DEFAULT NULL,
  `command` varchar(64) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `executed_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
