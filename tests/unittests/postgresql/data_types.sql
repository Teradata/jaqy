--------------------------------------------------------------------------
-- test PostgreSQL various data types
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
.debug resultset on

CREATE TABLE NumTable
(
	a  INTEGER,
	c1 SMALLINT,
	c2 INTEGER,
	c3 BIGINT,
	c4 FLOAT,
	c5 DOUBLE PRECISION
);
.format table
.desc NumTable

INSERT INTO NumTable VALUES (1, -12345, -123456789, -1234567890, -1234.5, -4.5e15);
INSERT INTO NumTable VALUES (2, 12345, 123456789, 1234567890, 1234.5, 4.5e15);
INSERT INTO NumTable VALUES (3, NULL, NULL, NULL, NULL, NULL);

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

INSERT INTO DecTable VALUES (1, -1.23, -1234567.89, -12345678901234567890.12, -12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (2, 1.23, 1234567.89, 12345678901234567890.12, 12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (3, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM DecTable ORDER BY a;
.format json -p on
SELECT * FROM DecTable ORDER BY a;

DROP TABLE DecTable;

CREATE TABLE SerialTable
(
	a  INTEGER,
	c1 SMALLSERIAL,
	c2 SERIAL,
	c3 BIGSERIAL
);
.format table
.desc SerialTable

INSERT INTO SerialTable (a) VALUES (1);
INSERT INTO SerialTable (a) VALUES (2);

.format csv
SELECT * FROM SerialTable ORDER BY a;
.format json -p on
SELECT * FROM SerialTable ORDER BY a;

DROP TABLE SerialTable;

CREATE TABLE StrTable
(
	a  INTEGER,
	c1 VARCHAR(255),
	c2 CHAR(1),
	c3 CHAR(5),
	c4 TEXT
);
.format table
.desc StrTable

INSERT INTO StrTable VALUES (1, 'aa', 'A', 'abcde', 'aaaaa');
INSERT INTO StrTable VALUES (2, 'bbb', 'B', 'fghij', 'bbbbb');
INSERT INTO StrTable VALUES (2, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM StrTable ORDER BY a;
.format json -p on
SELECT * FROM StrTable ORDER BY a;

DROP TABLE StrTable;

CREATE TABLE BinTable
(
	a  INTEGER,
	c2 BYTEA
);
.format table
.desc BinTable

INSERT INTO BinTable VALUES (1, E'\\xdeadbeef');
INSERT INTO BinTable VALUES (2, E'\\xfacefeed');
INSERT INTO BinTable VALUES (3, NULL);

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
	c3 TIME WITH TIME ZONE,
	c4 TIMESTAMP,
	c5 TIMESTAMP WITH TIME ZONE
);
.format table
.desc TimeTable

INSERT INTO TimeTable VALUES (1, '2001-02-03', '12:34:56', '12:34:56-08:00', '2001-02-03 12:34:56', '2001-02-03 12:34:56-08:00');
INSERT INTO TimeTable VALUES (2, '2001-03-04', '12:34:56', '12:34:56+08:00', '2001-03-04 12:34:56', '2001-02-03 12:34:56+08:00');
INSERT INTO TimeTable VALUES (3, NULL, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM TimeTable ORDER BY a;
.format json -p on
SELECT * FROM TimeTable ORDER BY a;

DROP TABLE TimeTable;

CREATE TABLE XmlTable
(
	a  INTEGER,
	c1 XML
);
.format table
.desc XmlTable

INSERT INTO XmlTable VALUES (1, '<abc>1234</abc>');
INSERT INTO XmlTable VALUES (2, '<abc>2345</abc>');
INSERT INTO XmlTable VALUES (3, NULL);

.format csv
SELECT * FROM XmlTable ORDER BY a;
.format json -p on
SELECT * FROM XmlTable ORDER BY a;

DROP TABLE XmlTable;

CREATE TABLE JsonTable
(
	a  INTEGER,
	c1 JSON
);
.format table
.desc JsonTable

INSERT INTO JsonTable VALUES (1, '{"abc":"def"}');
INSERT INTO JsonTable VALUES (2, '[123, 456, true, null, "hello"]');
INSERT INTO JsonTable VALUES (3, NULL);

.format csv
SELECT * FROM JsonTable ORDER BY a;
.format json -p on
SELECT * FROM JsonTable ORDER BY a;

DROP TABLE JsonTable;

CREATE TABLE GeoTable
(
	a  INTEGER,
	c1 POINT,
	c2 LSEG,
	c3 BOX,
	c4 PATH,
	c5 POLYGON,
	c6 CIRCLE
);
.format table
.desc GeoTable

INSERT INTO GeoTable VALUES (1, '(1,2)', '((1,1),(2,2))', '((1,1),(2,2))', '((1,1),(2,2))', '((0,0),(0,1),(1,1),(1,0))', '<(0,0),1>');
INSERT INTO GeoTable VALUES (2, '(2,2)', '((2,2),(3,3))', '((2,2),(3,3))', '((0,0),(0,1),(1,1),(1,0),(0,0))', '((0,0),(0,1),(1,1),(1,0),(0,0))', '<(0,0),1>');
INSERT INTO GeoTable VALUES (3, NULL, NULL, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM GeoTable ORDER BY a;
.format json -p on
SELECT * FROM GeoTable ORDER BY a;

DROP TABLE GeoTable;

CREATE TABLE NetTable
(
	a  INTEGER,
	c1 CIDR,
	c2 INET,
	c3 MACADDR
);
.format table
.desc NetTable

INSERT INTO NetTable VALUES (1, '192.168.100.128/25', '127.0.0.1', '08:00:2b:01:02:03');
INSERT INTO NetTable VALUES (2, '192.168/24', '192.168.1.1', '08-00-2b-01-02-03');
INSERT INTO NetTable VALUES (3, NULL, NULL, NULL);

.format csv
SELECT * FROM NetTable ORDER BY a;
.format json -p on
SELECT * FROM NetTable ORDER BY a;

DROP TABLE NetTable;

CREATE TABLE RangeTable
(
	a  INTEGER,
	c1 INT4RANGE,
	c2 INT8RANGE,
	c3 NUMRANGE,
	c4 TSRANGE,
	c5 TSTZRANGE,
	c6 DATERANGE
);
.format table
.desc RangeTable

INSERT INTO RangeTable VALUES (1, '[1, 2]', '[12345,67890]', '[123.45,678.90]', '[2010-01-01 14:30, 2010-01-01 15:30]', '[2010-01-01 14:30-08:00, 2010-01-01 15:30-08:00]', '[2001-02-03,2002-03-04]');
INSERT INTO RangeTable VALUES (2, '[1, 2)', '[12345,67890)', '[123.45,678.90)', '[2010-01-01 14:30, 2010-01-01 15:30)', '[2010-01-01 14:30-08:00, 2010-01-01 15:30-08:00)', '[2001-02-03,2002-03-04)');
INSERT INTO RangeTable VALUES (3, NULL, NULL, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM RangeTable ORDER BY a;
.format json -p on
SELECT * FROM RangeTable ORDER BY a;

DROP TABLE RangeTable;

CREATE TABLE MiscTable1
(
	a  INTEGER,
	c1 BOOLEAN,
	c2 BIT VARYING(8),
	c3 UUID,
	c4 MONEY
);
.format table
.desc MiscTable1

INSERT INTO MiscTable1 VALUES (1, true, B'101', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 12.34);
INSERT INTO MiscTable1 VALUES (2, false, B'1010', 'a0ee-bc99-9c0b-4ef8-bb6d-6bb9-bd38-0a11', 23.45);
INSERT INTO MiscTable1 VALUES (2, NULL, NULL, NULL, NULL);

.format csv
SELECT * FROM MiscTable1 ORDER BY a;
.format json -p on
SELECT * FROM MiscTable1 ORDER BY a;

DROP TABLE MiscTable1;

CREATE TABLE MiscTable2
(
	a  INTEGER,
	c1 TSVECTOR,
	c2 TSQUERY
);
.format table
.desc MiscTable2

INSERT INTO MiscTable2 VALUES (1, 'a fat cat sat on a mat and ate a fat rat', 'fat & rat');
INSERT INTO MiscTable2 VALUES (2, 'a fat cat sat on a mat and ate a fat rat', 'fat & rat');
INSERT INTO MiscTable2 VALUES (2, NULL, NULL);

.format csv
SELECT * FROM MiscTable2 ORDER BY a;
.format json -p on
SELECT * FROM MiscTable2 ORDER BY a;

DROP TABLE MiscTable2;

.close
