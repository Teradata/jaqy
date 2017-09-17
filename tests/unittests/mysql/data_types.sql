--------------------------------------------------------------------------
-- test MySQL various data types
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;
.debug resultset on

CREATE TABLE NumTable
(
	a  INTEGER,
	c1 BIT(64),
	c2 TINYINT,
	c3 BOOLEAN,
	c4 SMALLINT,
	c5 MEDIUMINT,
	c6 INTEGER,
	c7 BIGINT,
	c8 FLOAT,
	c9 DOUBLE
);
.format table
.desc NumTable
.desc -s NumTable

INSERT INTO NumTable VALUES (1, 12, -127, true, -12345, -123456, -123456789, -1234567890, -1234.5, -4.5e15);
INSERT INTO NumTable VALUES (2, 80, 127, false, 12345, 1234567, 123456789, 1234567890, 1234.5, 4.5e15);
INSERT INTO NumTable VALUES (3, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM NumTable ORDER BY a;
.format json -p on
SELECT * FROM NumTable ORDER BY a;

DROP TABLE NumTable;

CREATE TABLE DecTable
(
	a  INTEGER,
	c1 DECIMAL(3,2),
	c2 DECIMAL(9,2),
	c3 DECIMAL(22,2),
	c4 DECIMAL(31,2)
);
.format table
.desc DecTable
.desc -s DecTable

INSERT INTO DecTable VALUES (1, -1.23, -1234567.89, -12345678901234567890.12, -12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (2, 1.23, 1234567.89, 12345678901234567890.12, 12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (3, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM DecTable ORDER BY a;
.format json -p on
SELECT * FROM DecTable ORDER BY a;

DROP TABLE DecTable;

CREATE TABLE StrTable
(
	a  INTEGER,
	c1 VARCHAR(255),
	c2 CHAR(1),
	c3 CHAR(5),
	c4 TEXT,
	c5 SET('one', 'two')
);
.format table
.desc StrTable
.desc -s StrTable

INSERT INTO StrTable VALUES (1, 'aa', 'A', 'abcde', 'aaaaa', 'one');
INSERT INTO StrTable VALUES (2, 'bbb', 'B', 'fghij', 'bbbbb', 'two');
INSERT INTO StrTable VALUES (3, NULL, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM StrTable ORDER BY a;
.format json -p on
SELECT * FROM StrTable ORDER BY a;

DROP TABLE StrTable;

CREATE TABLE BinTable
(
	a  INTEGER,
	c1 BINARY(10),
	c2 VARBINARY(10),
	c3 BLOB,
	c4 ENUM('a','b','c') CHARACTER SET BINARY
);
.format table
.desc BinTable
.desc -s BinTable

INSERT INTO BinTable VALUES (1, X'deadbeef', X'deadbeef', X'deadbeef', 'a');
INSERT INTO BinTable VALUES (2, X'facefeed', X'facefeed', X'facefeed', 'b');
INSERT INTO BinTable VALUES (3, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM BinTable ORDER BY a;
.format json -p on
SELECT * FROM BinTable ORDER BY a;

DROP TABLE BinTable;

CREATE TABLE TimeTable
(
	a  INTEGER,
	c1 DATE,
	c2 TIME,
	c3 DATETIME,
	c4 TIMESTAMP,
	c5 YEAR
);
.format table
.desc TimeTable

INSERT INTO TimeTable VALUES (1, '2001-02-03', '12:34:56', '2001-02-03 12:34:56', '2001-02-03 12:34:56', 2001);
INSERT INTO TimeTable VALUES (2, '2001-03-04', '12:34:56', '2002-03-04 12:34:56', '2001-03-04 12:34:56', 2002);
INSERT INTO TimeTable VALUES (3, NULL, NULL, NULL, '2001-03-04 12:34:56', NULL);

.format csv
SELECT * FROM TimeTable ORDER BY a;
.format json -p on
SELECT * FROM TimeTable ORDER BY a;

DROP TABLE TimeTable;
.close
