CREATE TABLE Account (
  userNo  				int NOT NULL AUTO_INCREMENT,
  userId 				varchar(100) NOT NULL UNIQUE,
  userPwd 				varchar(25) NOT NULL,
  adminYn 				char(1) NOT NULL DEFAULT 'N' /* Y N */,
  createdDateTime 		timestamp NOT NULL DEFAULT now(),
  modifiedDateTime 		timestamp DEFAULT 0,
  CONSTRAINT pk_Account PRIMARY KEY (userNo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE Defect (
	did	 				bigint NOT NULL AUTO_INCREMENT,
	toolName			varchar(100) NOT NULL,
	language			varchar(30) NOT NULL,
	checkerCode			varchar(100) NOT NULL,
	fileName			varchar(255) NOT NULL,
	modulePath			varchar(255),
	className			varchar(255),
	methodName			varchar(255),
  categoryName		varchar(255),
	severityCode		char(3) /* MAJ, MIN, CRC, ETC */,
	statusCode			char(3) /* NEW, ASN, REV, SLV, CLS, FIX, EXC */,
	message				varchar(2014),
	createdDateTime 	timestamp NOT NULL DEFAULT now(),
	modifiedDateTime 	timestamp DEFAULT 0,
	creatorNo			int,
	modifierNo			int,
	chargerNo			int,
	reviewerNo			int,
	approvalNo			int,
	CONSTRAINT pk_Defect PRIMARY KEY (did)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Occurence (
	oid					int NOT NULL AUTO_INCREMENT,
	did					bigint NOT NULL,
	startLine			int NOT NULL,
	endLine				int NOT NULL,
	charStart			int,
	charEnd				int,
	statusCode			char(3) /* NEW, ASN, REV, SLV, CLS, FIX, EXC */,
	code				varchar(255),
	variableName		varchar(255),
	stringValue			varchar(255),
	fieldName			varchar(255),
	message				varchar(2014),
	createdDateTime 	timestamp NOT NULL DEFAULT now(),
	modifiedDateTime 	timestamp DEFAULT 0,
	creatorNo			int,
	modifierNo			int,
	chargerNo			int,
	reviewerNo			int,
	approvalNo			int,
	CONSTRAINT pk_Occurence PRIMARY KEY (oid)
/*	FOREIGN KEY (localDid) REFERENCES Defect(localDid) ON DELETE CASCADE */
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE DefectFilter (
	fid					int NOT NULL AUTO_INCREMENT,
	toolName			varchar(100),
	language			varchar(30),
	checkerCode			varchar(100),
	fileName			varchar(255),
	modulePath			varchar(255),
	className			varchar(255),
	methodName			varchar(255),
	filterType			char(1) Not Null, /* F:False Alarm, E:Exclude Scope */
	createdDateTime 	timestamp NOT NULL DEFAULT now(),
	creatorNo			int,
	CONSTRAINT pk_DefectFilter PRIMARY KEY (fid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE Snapshot (
	id					bigint NOT NULL UNIQUE,
	groupId				int NOT NULL,
	createdDateTime		timestamp NOT NULL DEFAULT now(),
	creatorNo			int,
	CONSTRAINT pk_Snapshot PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SnapshotDefectMap (
	snapshotId			bigint NOT NULL,
	did	 				bigint NOT NULL,
	toolName			varchar(100) NOT NULL,
	language			varchar(30) NOT NULL,
	checkerCode			varchar(100) NOT NULL,
	fileName			varchar(255) NOT NULL,
	modulePath			varchar(255),
	className			varchar(255),
	methodName			varchar(255),
  categoryName		varchar(255),
	severityCode		char(3) /* MAJ, MIN, CRC, ETC */,
	statusCode			char(3) /* NEW, ASN, REV, SLV, CLS, FIX, EXC */,
	message				varchar(2014),
	createdDateTime 	timestamp NOT NULL DEFAULT now(),
	modifiedDateTime 	timestamp DEFAULT 0,
	creatorNo			int,
	modifierNo			int,
	chargerNo			int,
	reviewerNo			int,
	approvalNo			int,
	CONSTRAINT pk_SnapshotDefect PRIMARY KEY (snapshotId, did),
	FOREIGN KEY (snapshotId) REFERENCES Snapshot(id) ON DELETE CASCADE,
	FOREIGN KEY (did) REFERENCES Defect(did)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SnapshotOccurenceMap (
	snapshotId			bigint NOT NULL,
	did					bigint NOT NULL,
	startLine			int NOT NULL,
	endLine				int NOT NULL,
	charStart			int,
	charEnd				int,
	statusCode			char(3) /* NEW, ASN, REV, SLV, CLS, FIX, EXC */,
	variableName		varchar(255),
	stringValue			varchar(255),
	fieldName			varchar(255),
	message				varchar(2014),
	createdDateTime 	timestamp NOT NULL DEFAULT now(),
	modifiedDateTime 	timestamp DEFAULT 0,
	creatorNo			int,
	modifierNo			int,
	chargerNo			int,
	reviewerNo			int,
	approvalNo			int,

	CONSTRAINT pk_SnapshotOccurenceMap PRIMARY KEY (snapshotId, did, startLine, endLine, charStart, charEnd),
	FOREIGN KEY (snapshotId) REFERENCES Snapshot(id) ON DELETE CASCADE,
	FOREIGN KEY (did) REFERENCES Defect(did)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE SourceCodeMap (
	id					bigint NOT NULL AUTO_INCREMENT,
	fileName			varchar(255) NOT NULL,
	modulePath			varchar(255),
	snapshotId			bigint,
	sourceCode			mediumtext NOT NULL,
	createdDateTime 	timestamp NOT NULL DEFAULT now(),
	creatorNo			int,

	CONSTRAINT pk_SnapshotSourceCodeMap PRIMARY KEY (id),
	FOREIGN KEY (snapshotId) REFERENCES Snapshot(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE CodeMetrics (
	id					bigint NOT NULL AUTO_INCREMENT,
	snapshotId			bigint,
	fileName			varchar(255) NOT NULL,
	modulePath			varchar(255),
	metricName			varchar(255) NOT NULL,
	metricValue			varchar(255) NOT NULL,
	createdDateTime 	timestamp NOT NULL DEFAULT now(),
	creatorNo			int NOT NULL,
	lastYn				char(1) NOT NULL,

	CONSTRAINT pk_CodeMetrics PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE FunctionMetrics (
	id	bigint NOT NULL AUTO_INCREMENT,
  snapshotId 	bigint,
  fileName			varchar(255) NOT NULL,
	modulePath			varchar(255),
  functionName	 	varchar(255) NOT NULL,
  cc					varchar(255) NOT NULL,
  sloc				varchar(255) NOT NULL,
  callDepth			varchar(255) NOT NULL,
  createdDateTime		timestamp NOT NULL DEFAULT now(),
  creatorNo			int Not NULL,
  lastYn				char(1) NOT NULL,

  CONSTRAINT pk_FunctionMetrics PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE AnalysisLog (
	id					bigint NOT NULL AUTO_INCREMENT,
	fileName			varchar(255) NOT NULL,
	modulePath			varchar(255),
	analystNo			int NOT NULL,
	defectCriticalCount	int,
	defectMajorCount	int,
	defectMinorCount	int,
	defectCrcCount		int,
	defectEtcCount		int,
	createdDateTime 	timestamp NOT NULL DEFAULT now(),

	CONSTRAINT pk_AnalysisLog PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE AccessLog (
	id					bigint NOT NULL AUTO_INCREMENT,
	remote				varchar(2014),
	api					varchar(255) NOT NULL,
	method				varchar(20),
	query				varchar(4000),
	body				text,
	creatorNo			int,
	createdDateTime 	timestamp NOT NULL DEFAULT now(),

	CONSTRAINT pk_AccessLog PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE DefectGroup (
	id					int NOT NULL UNIQUE,
	groupName			varchar(100) NOT NULL,
	groupType			char(3) NOT NULL /* TEM, SNS, COM, PRD */,
	description			varchar(2014),
	createdDateTime		timestamp NOT NULL DEFAULT now(),
	creatorNo			int,
	CONSTRAINT pk_DefectGroup PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE DefectGroupMap (
	id					int,
	parentId			int,
	createdDateTime		timestamp NOT NULL DEFAULT now(),
	creatorNo			int,
	CONSTRAINT pk_DefectGroup PRIMARY KEY (id),
	FOREIGN KEY (id) REFERENCES DefectGroup(id) ON DELETE CASCADE,
	FOREIGN KEY (parentId) REFERENCES DefectGroup(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE SharedDataVersion (
	version				int NOT NULL,
	name				varchar(50) NOT NULL,	/* FalseAlarm, TargetFilter */
	modifiedDateTime	timestamp NOT NULL DEFAULT now(),
	modifierNo			int,
	description			varchar(2014),
	CONSTRAINT pk_SharedDataVersion PRIMARY KEY (version, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE Configure (
	codeKey				varchar(10) NOT NULL,
	codeValue			varchar(255) NOT NULL,
	codeName			varchar(2014),
	description			varchar(2014),
	CONSTRAINT pk_Configure PRIMARY KEY (codeKey, codeValue)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/* Account */
Insert INTO Account (userId, userPwd, adminYn, createdDateTime) VALUES ('admin', 'dex#0001', 'Y', now());
Insert INTO Account (userId, userPwd, adminYn, createdDateTime) VALUES ('user', 'dexter', 'N', now());

/* Configure */
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('db-version', '1.1.1', 'Dexter DB Version');

INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('severity', 'CRI', 'Critical');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('severity', 'MAJ', 'Major');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('severity', 'MIN', 'Minor');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('severity', 'CRC', 'CRC');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('severity', 'ETC', 'ETC');

INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('def-status', 'NEW', 'New');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('def-status', 'ASN', 'Assign');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('def-status', 'REV', 'Review');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('def-status', 'SLV', 'Solved');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('def-status', 'CLS', 'Close');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('def-status', 'FIX', 'Close');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('def-status', 'EXC', 'Exception');

INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('group-type', 'TEM', 'Team');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('group-type', 'SNS', 'Snapshot');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('group-type', 'COM', 'Component');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('group-type', 'PRD', 'Product');
INSERT INTO Configure (codeKey, codeValue, codeName) VALUES ('group-type', 'PRJ', 'Project');



/* DROP TABLES
drop table Configure;
drop table SharedDataVersion;
drop table DefectGroupMap;
drop table DefectGroup;
drop table AccessLog;
drop table AnalysisLog;
drop table CodeMetrics;
drop table FunctionMetrics;
drop table SourceCodeMap;
drop table SnapshotOccurenceMap;
drop table SnapshotDefectMap;
drop table Snapshot;
drop table DefectFilter;
drop table Occurence;
drop table Defect;
drop table Account;
*/
