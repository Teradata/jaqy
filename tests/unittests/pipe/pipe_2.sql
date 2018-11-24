--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE TextTable
(
	a  INTEGER,
	c1 TEXT
);

INSERT INTO TextTable VALUES (1, '1');

.repeat 10
INSERT INTO TextTable
SELECT a + (SELECT max(a) FROM TextTable),
       c1 || (SELECT c1 FROM TextTable WHERE a = (SELECT max(a) FROM TextTable))
FROM TextTable;

INSERT INTO XmlTable
SELECT a, CAST('<a>' || c1 || '</a>' AS XML)
FROM TextTable;

.session new
.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE TextTable
(
	a  INTEGER,
	c1 MEDIUMTEXT
);

.session 0

.set autocommit off
.set fetchsize 50
.export pipe
SELECT * FROM TextTable ORDER BY a;

.session 1

.import pipe
.set batchsize 500
INSERT INTO TextTable VALUES (?, ?);

SELECT COUNT(*) FROM TextTable;

SELECT * FROM TextTable WHERE a = 40;

.session 0
.set autocommit on
DROP TABLE TextTable;
.close
.session 1
DROP TABLE TextTable;
.close
