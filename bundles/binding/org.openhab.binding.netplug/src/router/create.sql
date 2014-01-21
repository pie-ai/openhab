CREATE TABLE `commands` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device` int(11) DEFAULT NULL,
  `command` varchar(64) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `executed_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `devices` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `netplug_id` varchar(64) DEFAULT NULL,
  `last_access` timestamp NULL DEFAULT NULL,
  `secret` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `netplug_id_UNIQUE` (`netplug_id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `status` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `device` int(11) DEFAULT NULL,
  `status` varchar(64) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `retrieved_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;