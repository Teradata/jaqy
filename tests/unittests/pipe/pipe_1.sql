--------------------------------------------------------------------------
-- test pipe
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE NumTable
(
	a  INTEGER,
	c1 TINYINT,
	c2 BOOLEAN,
	c3 SMALLINT,
	c4 MEDIUMINT,
	c5 INTEGER,
	c6 BIGINT,
	c7 FLOAT,
	c8 DOUBLE
);

INSERT INTO NumTable VALUES (1, -127, true, -12345, -123456, -123456789, -1234567890, -1234.5, -4.5e15);
INSERT INTO NumTable VALUES (2, 127, false, 12345, 1234567, 123456789, 1234567890, 1234.5, 4.5e15);
INSERT INTO NumTable VALUES (3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

.session new
.run ../common/postgresql_setup.sql

CREATE TABLE NumTable
(
	a  INTEGER,
	c1 INTEGER,
	c2 BOOLEAN,
	c3 SMALLINT,
	c4 INTEGER,
	c5 INTEGER,
	c6 BIGINT,
	c7 FLOAT,
	c8 DOUBLE PRECISION
);

.session 0
.export pipe
SELECT * FROM NumTable ORDER BY a;
.session 1
.import pipe
INSERT INTO NumTable VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);

SELECT * FROM NumTable ORDER BY a;

.session 0
DROP TABLE NumTable;
.close
.session 1
DROP TABLE NumTable;
.close
