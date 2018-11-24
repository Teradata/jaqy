--------------------------------------------------------------------------
-- test MySQL various data types
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;
.format csv

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

SELECT * FROM NumTable ORDER BY a;
.export csv NumTable.csv
SELECT * FROM NumTable ORDER BY a;
DELETE FROM NumTable;
.import csv -h -f NumTable.csv
INSERT INTO NumTable VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
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

INSERT INTO DecTable VALUES (1, -1.23, -1234567.89, -12345678901234567890.12, -12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (2, 1.23, 1234567.89, 12345678901234567890.12, 12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (3, NULL, NULL, NULL, NULL);

SELECT * FROM DecTable ORDER BY a;
.export csv DecTable.csv
SELECT * FROM DecTable ORDER BY a;
DELETE FROM DecTable;
.import csv -h -f DecTable.csv
INSERT INTO DecTable VALUES (?, ?, ?, ?, ?);
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

INSERT INTO StrTable VALUES (1, 'aa', 'A', 'abcde', 'aaaaa', 'one');
INSERT INTO StrTable VALUES (2, 'bbb', 'B', 'fghij', 'bbbbb', 'two');
INSERT INTO StrTable VALUES (3, NULL, NULL, NULL, NULL, NULL);

SELECT * FROM StrTable ORDER BY a;
.export csv StrTable.csv
SELECT * FROM StrTable ORDER BY a;
DELETE FROM StrTable;
.import csv -h -f StrTable.csv
INSERT INTO StrTable VALUES (?, ?, ?, ?, ?, ?);
SELECT * FROM StrTable ORDER BY a;

DROP TABLE StrTable;

CREATE TABLE BinTable
(
	a  INTEGER,
	c1 BIT(64),
	c2 BINARY(10),
	c3 VARBINARY(10),
	c4 BLOB,
	c5 ENUM('a','b','c') CHARACTER SET BINARY
);

INSERT INTO BinTable VALUES (1, 12, X'deadbeef', X'deadbeef', X'deadbeef', 'a');
INSERT INTO BinTable VALUES (2, 80, X'facefeed', X'facefeed', X'facefeed', 'b');
INSERT INTO BinTable VALUES (3, NULL, NULL, NULL, NULL, NULL);

SELECT * FROM BinTable ORDER BY a;
.export csv BinTable.csv
SELECT * FROM BinTable ORDER BY a;
DELETE FROM BinTable;
-- Because MySQL does not provide the type information, the import will fail
-- or incorrect for binary types.
.import csv -h -f BinTable.csv
INSERT INTO BinTable VALUES (?, ?, ?, ?, ?, ?);
SELECT * FROM BinTable ORDER BY a;

DROP TABLE BinTable;

CREATE TABLE TimeTable
(
	a  INTEGER,
	c1 DATE,
	c2 TIME,
	c3 DATETIME,
	c4 TIMESTAMP
);

INSERT INTO TimeTable VALUES (1, '2001-02-03', '12:34:56', '2001-02-03 12:34:56', '2001-02-03 12:34:56');
INSERT INTO TimeTable VALUES (2, '2001-03-04', '12:34:56', '2002-03-04 12:34:56', '2001-03-04 12:34:56');
INSERT INTO TimeTable VALUES (3, NULL, NULL, NULL, '2001-03-04 12:34:56');

SELECT * FROM TimeTable ORDER BY a;
.export csv TimeTable.csv
SELECT * FROM TimeTable ORDER BY a;
DELETE FROM TimeTable;
.import csv -h -f TimeTable.csv
INSERT INTO TimeTable VALUES (?, ?, ?, ?, ?, ?);
SELECT * FROM TimeTable ORDER BY a;

DROP TABLE TimeTable;

CREATE TABLE YearTable
(
	a  INTEGER,
	c1 YEAR
);

INSERT INTO YearTable VALUES (1, 2001);
INSERT INTO YearTable VALUES (2, 2002);
INSERT INTO YearTable VALUES (3, NULL);

SELECT * FROM YearTable ORDER BY a;
.export csv YearTable.csv
SELECT * FROM YearTable ORDER BY a;
DELETE FROM YearTable;
.import csv -h -f YearTable.csv
INSERT INTO YearTable VALUES (?, ?);
SELECT * FROM YearTable ORDER BY a;

DROP TABLE YearTable;

.close
.os rm -f *.csv
