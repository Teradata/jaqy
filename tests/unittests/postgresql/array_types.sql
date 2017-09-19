--------------------------------------------------------------------------
-- test PostgreSQL various array data types
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
.debug resultset on

CREATE TABLE NumTable
(
	a  INTEGER,
	c1 SMALLINT[],
	c2 INTEGER[],
	c3 BIGINT[],
	c4 FLOAT[],
	c5 DOUBLE PRECISION[]
);

INSERT INTO NumTable VALUES (1, '{-12345}', '{-123456789}', '{-1234567890}', '{-1234.5}', '{-4.5e15}');
INSERT INTO NumTable VALUES (2, '{12345}', '{123456789}', '{1234567890}', '{1234.5}', '{4.5e15}');

.format csv
SELECT * FROM NumTable ORDER BY a;
.format json -p on
SELECT * FROM NumTable ORDER BY a;

DROP TABLE NumTable;

CREATE TABLE DecTable
(
	a  INTEGER,
	c1 DECIMAL(3,2)[],
	c2 DECIMAL(9,2)[],
	c3 DECIMAL(22,2)[],
	c4 DECIMAL(31,2)[]
);
INSERT INTO DecTable VALUES (1, '{-1.23}', '{-1234567.89}', '{-12345678901234567890.12}', '{-12345678901234567890123456789.01}');
INSERT INTO DecTable VALUES (2, '{1.23}', '{1234567.89}', '{12345678901234567890.12}', '{12345678901234567890123456789.01}');

.format csv
SELECT * FROM DecTable ORDER BY a;
.format json -p on
SELECT * FROM DecTable ORDER BY a;

DROP TABLE DecTable;

CREATE TABLE StrTable
(
	a  INTEGER,
	c1 VARCHAR(255)[],
	c2 CHAR(1)[],
	c3 CHAR(5)[],
	c4 TEXT[]
);

INSERT INTO StrTable VALUES (1, '{aa}', '{A}', '{abcde}', '{aaaaa}');
INSERT INTO StrTable VALUES (2, '{bbb}', '{B}', '{fghij}', '{bbbbb}');

.format csv
SELECT * FROM StrTable ORDER BY a;
.format json -p on
SELECT * FROM StrTable ORDER BY a;

DROP TABLE StrTable;

CREATE TABLE BinTable
(
	a  INTEGER,
	c1 BYTEA[]
);

INSERT INTO BinTable VALUES (1, '{\\xdeadbeef}');
INSERT INTO BinTable VALUES (2, '{\\xfacefeed}');

.format csv
SELECT * FROM BinTable ORDER BY c1;
.format json -p on
SELECT * FROM BinTable ORDER BY c1;

DROP TABLE BinTable;

CREATE TABLE TimeTable
(
	a  INTEGER,
	c1 DATE[],
	c2 TIME[],
	c3 TIME WITH TIME ZONE[],
	c4 TIMESTAMP[],
	c5 TIMESTAMP WITH TIME ZONE[]
);

INSERT INTO TimeTable VALUES (1, '{2001-02-03}', '{12:34:56}', '{12:34:56-08:00}', '{2001-02-03 12:34:56}', '{2001-02-03 12:34:56-08:00}');
INSERT INTO TimeTable VALUES (2, '{2001-03-04}', '{12:34:56}', '{12:34:56+08:00}', '{2001-03-04 12:34:56}', '{2001-02-03 12:34:56+08:00}');

.format csv
SELECT * FROM TimeTable ORDER BY c1;
.format json -p on
SELECT * FROM TimeTable ORDER BY c1;

DROP TABLE TimeTable;

.close
