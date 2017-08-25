--------------------------------------------------------------------------
-- test PostgreSQL various data types
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
.format table

CREATE TABLE NumTable
(
	c1 SMALLINT,
	c2 INTEGER,
	c3 BIGINT,
	c4 FLOAT,
	c5 DOUBLE PRECISION
);

INSERT INTO NumTable VALUES (-12345, -123456789, -1234567890, -1234.5, -4.5e15);
INSERT INTO NumTable VALUES (12345, 123456789, 1234567890, 1234.5, 4.5e15);

SELECT * FROM NumTable ORDER BY c1;

DROP TABLE NumTable;

CREATE TABLE DecTable
(
	c1 DECIMAL(3,2),
	c2 DECIMAL(9,2),
	c3 DECIMAL(22,2),
	c4 DECIMAL(31,2)
);
INSERT INTO DecTable VALUES (-1.23, -1234567.89, -12345678901234567890.12, -12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (1.23, 1234567.89, 12345678901234567890.12, 12345678901234567890123456789.01);

SELECT * FROM DecTable ORDER BY c1;
DROP TABLE DecTable;

CREATE TABLE SerialTable
(
	c1 SMALLSERIAL,
	c2 SERIAL,
	c3 BIGSERIAL,
	c4 INTEGER
);

INSERT INTO SerialTable (C4) VALUES (1);
INSERT INTO SerialTable (C4) VALUES (2);

SELECT * FROM SerialTable ORDER BY c1;
DROP TABLE SerialTable;

CREATE TABLE StrTable
(
	c1 VARCHAR(255),
	c2 CHAR(1),
	c3 CHAR(5),
	c4 TEXT
);

INSERT INTO StrTable VALUES ('aa', 'A', 'abcde', 'aaaaa');
INSERT INTO StrTable VALUES ('bbb', 'B', 'fghij', 'bbbbb');

SELECT * FROM StrTable ORDER BY c1;
DROP TABLE StrTable;

CREATE TABLE BinTable
(
	c1 INTEGER,
	c2 BYTEA
);

INSERT INTO BinTable VALUES (1, E'\\xdeadbeef');
INSERT INTO BinTable VALUES (2, E'\\xfacefeed');

SELECT * FROM BinTable ORDER BY c1;
DROP TABLE BinTable;

CREATE TABLE TimeTable
(
	c1 INTEGER,
	c2 DATE,
	c3 TIME,
	c4 TIME WITH TIME ZONE,
	c5 TIMESTAMP,
	c6 TIMESTAMP WITH TIME ZONE
);

INSERT INTO TimeTable VALUES (1, '2001-02-03', '12:34:56', '12:34:56-08:00', '2001-02-03 12:34:56', '2001-02-03 12:34:56-08:00');
INSERT INTO TimeTable VALUES (2, '2001-03-04', '12:34:56', '12:34:56+08:00', '2001-03-04 12:34:56', '2001-02-03 12:34:56+08:00');

SELECT * FROM TimeTable ORDER BY c1;
DROP TABLE TimeTable;

CREATE TABLE XmlTable
(
	c1 INTEGER,
	c2 XML
);

INSERT INTO XmlTable VALUES (1, '<abc>1234</abc>');
INSERT INTO XmlTable VALUES (2, '<abc>2345</abc>');

SELECT * FROM XmlTable ORDER BY c1;
DROP TABLE XmlTable;

CREATE TABLE JsonTable
(
	c1 INTEGER,
	c2 JSON
);

INSERT INTO JsonTable VALUES (1, '{"abc":"def"}');
INSERT INTO JsonTable VALUES (2, '[123, 456, true, null, "hello"]');

SELECT * FROM JsonTable ORDER BY c1;
DROP TABLE JsonTable;

CREATE TABLE GeoTable
(
	c1 INTEGER,
	c2 POINT,
	c3 LSEG,
	c4 BOX,
	c5 PATH,
	c6 POLYGON,
	c7 CIRCLE
);

INSERT INTO GeoTable VALUES (1, '(1,2)', '((1,1),(2,2))', '((1,1),(2,2))', '((1,1),(2,2))', '((0,0),(0,1),(1,1),(1,0))','<(0,0),1>');
INSERT INTO GeoTable VALUES (2, '(2,2)', '((2,2),(3,3))', '((2,2),(3,3))', '((0,0),(0,1),(1,1),(1,0),(0,0))', '((0,0),(0,1),(1,1),(1,0),(0,0))','<(0,0),1>');

SELECT * FROM GeoTable ORDER BY c1;
DROP TABLE GeoTable;

CREATE TABLE NetTable
(
	c1 INTEGER,
	c2 CIDR,
	c3 INET,
	c4 MACADDR
);

INSERT INTO NetTable VALUES (1, '192.168.100.128/25', '127.0.0.1', '08:00:2b:01:02:03');
INSERT INTO NetTable VALUES (2, '192.168/24', '192.168.1.1', '08-00-2b-01-02-03');

SELECT * FROM NetTable ORDER BY c1;
DROP TABLE NetTable;

CREATE TABLE RangeTable
(
	c1 INTEGER,
	c2 INT4RANGE,
	c3 INT8RANGE,
	c4 NUMRANGE,
	c5 TSRANGE,
	c6 TSTZRANGE,
	c7 DATERANGE
);

INSERT INTO RangeTable VALUES (1, '[1, 2]', '[12345,67890]', '[123.45,678.90]', '[2010-01-01 14:30, 2010-01-01 15:30]', '[2010-01-01 14:30-08:00, 2010-01-01 15:30-08:00]', '[2001-02-03,2002-03-04]');
INSERT INTO RangeTable VALUES (2, '[1, 2)', '[12345,67890)', '[123.45,678.90)', '[2010-01-01 14:30, 2010-01-01 15:30)', '[2010-01-01 14:30-08:00, 2010-01-01 15:30-08:00)', '[2001-02-03,2002-03-04)');

SELECT * FROM RangeTable ORDER BY c1;
DROP TABLE RangeTable;

CREATE TABLE MiscTable1
(
	c1 INTEGER,
	c2 BOOLEAN,
	c3 BIT VARYING(8),
	c4 UUID,
	c5 MONEY
);

INSERT INTO MiscTable1 VALUES (1, true, B'101', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 12.34);
INSERT INTO MiscTable1 VALUES (2, false, B'1010', 'a0ee-bc99-9c0b-4ef8-bb6d-6bb9-bd38-0a11', 23.45);

SELECT * FROM MiscTable1 ORDER BY c1;
DROP TABLE MiscTable1;

CREATE TABLE MiscTable2
(
	c1 INTEGER,
	c2 TSVECTOR,
	c3 TSQUERY
);

INSERT INTO MiscTable2 VALUES (1, 'a fat cat sat on a mat and ate a fat rat', 'fat & rat');
INSERT INTO MiscTable2 VALUES (2, 'a fat cat sat on a mat and ate a fat rat', 'fat & rat');

SELECT * FROM MiscTable2 ORDER BY c1;
DROP TABLE MiscTable2;

.close
