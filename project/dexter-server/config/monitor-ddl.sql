CREATE TABLE `ProjectInfo` (
  `pid` int(11) NOT NULL AUTO_INCREMENT,
  `projectYear` int(11) NOT NULL,
  `projectType` varchar(100) NOT NULL,
  `projectName` varchar(100) NOT NULL,
  `requester` varchar(100) NOT NULL,
  `dbName` varchar(100) NOT NULL,
  `hostIP` varchar(100) NOT NULL,
  `portNumber` int(11) NOT NULL DEFAULT '0',
  `groupName` varchar(100) NOT NULL,
  `language` varchar(100) NOT NULL,
  `createdDateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`pid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


CREATE TABLE `WeeklyStatus` (
  `wid` int(11) NOT NULL AUTO_INCREMENT,
  `pid` int(11) NOT NULL,
  `year` int(11) NOT NULL,
  `week` int(11) NOT NULL,
  `day` int(11) NOT NULL,
  `allDefectCount` int(11) NOT NULL DEFAULT '0',
  `allNew` int(11) NOT NULL DEFAULT '0',
  `allFix` int(11) NOT NULL DEFAULT '0',
  `allDis` int(11) DEFAULT NULL,
  `criNew` int(11) NOT NULL DEFAULT '0',
  `criFix` int(11) NOT NULL DEFAULT '0',
  `criDis` int(11) DEFAULT NULL,
  `majNew` int(11) NOT NULL DEFAULT '0',
  `majFix` int(11) NOT NULL DEFAULT '0',
  `majDis` int(11) DEFAULT NULL,
  `minNew` int(11) NOT NULL DEFAULT '0',
  `minFix` int(11) NOT NULL DEFAULT '0',
  `minDis` int(11) DEFAULT NULL,
  `crcNew` int(11) NOT NULL DEFAULT '0',
  `crcFix` int(11) NOT NULL DEFAULT '0',
  `crcDis` int(11) DEFAULT NULL,
  `etcNew` int(11) NOT NULL DEFAULT '0',
  `etcFix` int(11) NOT NULL DEFAULT '0',
  `etcDis` int(11) DEFAULT NULL,
  `createdDateTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `userCount` int(11) DEFAULT NULL,
  PRIMARY KEY (`wid`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;


CREATE TABLE `DexterUserList` (
	`listNumber` int(11) NOT NULL AUTO_INCREMENT,
    `userId`	 varchar(100),
    `userName`	 varchar(100),
	`userLab`	varchar(100),
	`dexterYn`	varchar(10),
	`reason` varchar(200),
    `ide` varchar(100),
    `language` varchar(50),
 PRIMARY KEY (`listNumber`)
)
 ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 ;