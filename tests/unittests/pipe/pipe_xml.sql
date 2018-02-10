--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------

.run ../common/postgresql_setup.sql
CREATE TABLE XmlTable
(
	a  INTEGER,
	c1 XML
);

INSERT INTO XmlTable VALUES (1, '<abc>1</abc>');
INSERT INTO XmlTable VALUES (2, '<abc>12</abc>');
INSERT INTO XmlTable VALUES (3, '<abc>123</abc>');
INSERT INTO XmlTable VALUES (4, '<abc>1234</abc>');
INSERT INTO XmlTable VALUES (5, '<abc>12345</abc>');
INSERT INTO XmlTable VALUES (6, NULL);

-- MySQL Test
.session new
.run ../common/mysql_setup.sql
USE vagrant;
CREATE TABLE XmlTable
(
	a  INTEGER,
	c1 MEDIUMTEXT
);

.session 0

.export pipe
.fetchsize 2
SELECT * FROM XmlTable ORDER BY a;

.session 1
.import pipe
.batchsize 2
INSERT INTO XmlTable VALUES (?, ?);

SELECT * FROM XmlTable ORDER BY a;

DROP TABLE XmlTable;
.close

-- SQLite Test
.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE XmlTable
(
	a  INTEGER,
	c1 TEXT
);

.session 0

.export pipe
.fetchsize 2
SELECT * FROM XmlTable ORDER BY a;

.session 1
.import pipe
.batchsize 3000
INSERT INTO XmlTable VALUES (?, ?);

SELECT * FROM XmlTable ORDER BY a;

DROP TABLE XmlTable;
.close

.session 0
DROP TABLE XmlTable;
.close
