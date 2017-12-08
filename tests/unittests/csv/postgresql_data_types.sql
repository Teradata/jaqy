--------------------------------------------------------------------------
-- test PostgreSQL various data types
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql
.debug resultset on
.debug preparedstatement on
.format json -p on -b hex

CREATE TABLE NumTable
(
	a  INTEGER,
	c1 SMALLINT,
	c2 INTEGER,
	c3 BIGINT,
	c4 FLOAT,
	c5 DOUBLE PRECISION
);

INSERT INTO NumTable VALUES (1, -12345, -123456789, -1234567890, -1234.5, -4.5e15);
INSERT INTO NumTable VALUES (2, 12345, 123456789, 1234567890, 1234.5, 4.5e15);
INSERT INTO NumTable VALUES (3, NULL, NULL, NULL, NULL, NULL);

.export csv num.csv
SELECT * FROM NumTable ORDER BY a;
DELETE FROM NumTable;
.import csv -h -f num.csv
INSERT INTO NumTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}}, {{c4}}, {{c5}});
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

.export csv dec.csv
SELECT * FROM DecTable ORDER BY a;
DELETE FROM DecTable;
.import csv -h -f dec.csv
INSERT INTO DecTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}}, {{c4}});
SELECT * FROM DecTable ORDER BY a;

DROP TABLE DecTable;

CREATE TABLE SerialTable
(
	a  INTEGER,
	c1 SMALLSERIAL,
	c2 SERIAL,
	c3 BIGSERIAL
);

INSERT INTO SerialTable (a) VALUES (1);
INSERT INTO SerialTable (a) VALUES (2);

.export csv serial.csv
SELECT * FROM SerialTable ORDER BY a;
DELETE FROM SerialTable;
.import csv -h -f serial.csv
INSERT INTO SerialTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}});
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

INSERT INTO StrTable VALUES (1, 'aa', 'A', 'abcde', 'aaaaa');
INSERT INTO StrTable VALUES (2, 'bbb', 'B', 'fghij', 'bbbbb');
INSERT INTO StrTable VALUES (3, NULL, NULL, NULL, NULL);

.export csv src.csv
SELECT * FROM StrTable ORDER BY a;
DELETE FROM StrTable;
.import csv -h -f src.csv
INSERT INTO StrTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}}, {{c4}});
SELECT * FROM StrTable ORDER BY a;

DROP TABLE StrTable;

CREATE TABLE BinTable
(
	a  INTEGER,
	c1 BYTEA
);

INSERT INTO BinTable VALUES (1, E'\\xdeadbeef');
INSERT INTO BinTable VALUES (2, E'\\xfacefeed');
INSERT INTO BinTable VALUES (3, NULL);

.export csv bin.csv
SELECT * FROM BinTable ORDER BY a;
DELETE FROM BinTable;
.import csv -h -f bin.csv
INSERT INTO BinTable VALUES ({{a}}, {{c1}});
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

INSERT INTO TimeTable VALUES (1, '2001-02-03', '12:34:56', '12:34:56-08:00', '2001-02-03 12:34:56', '2001-02-03 12:34:56-08:00');
INSERT INTO TimeTable VALUES (2, '2001-03-04', '12:34:56', '12:34:56+08:00', '2001-03-04 12:34:56', '2001-02-03 12:34:56+08:00');
INSERT INTO TimeTable VALUES (3, NULL, NULL, NULL, NULL, NULL);

.export csv time.csv
SELECT * FROM TimeTable ORDER BY a;
DELETE FROM TimeTable;
.import csv -h -f time.csv
INSERT INTO TimeTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}}, {{c4}}, {{c5}});
SELECT * FROM TimeTable ORDER BY a;

DROP TABLE TimeTable;

CREATE TABLE XmlTable
(
	a  INTEGER,
	c1 XML
);

INSERT INTO XmlTable VALUES (1, '<abc>1234</abc>');
INSERT INTO XmlTable VALUES (2, '<abc>2345</abc>');
INSERT INTO XmlTable VALUES (3, NULL);

.export csv xml.csv
SELECT * FROM XmlTable ORDER BY a;
DELETE FROM XmlTable;
.import csv -h -f xml.csv
INSERT INTO XmlTable VALUES ({{a}}, {{c1}});
SELECT * FROM XmlTable ORDER BY a;

DROP TABLE XmlTable;

CREATE TABLE JsonTable
(
	a  INTEGER,
	c1 JSON
);

INSERT INTO JsonTable VALUES (1, '{"abc":"def"}');
INSERT INTO JsonTable VALUES (2, '[123, 456, true, null, "hello"]');

.export csv j.csv
SELECT * FROM JsonTable ORDER BY a;
DELETE FROM JsonTable;
.import csv -h -f j.csv
INSERT INTO JsonTable VALUES ({{a}}, {{c1}});
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

INSERT INTO GeoTable VALUES (1, '(1,2)', '((1,1),(2,2))', '((1,1),(2,2))', '((1,1),(2,2))', '((0,0),(0,1),(1,1),(1,0))','<(0,0),1>');
INSERT INTO GeoTable VALUES (2, '(2,2)', '((2,2),(3,3))', '((2,2),(3,3))', '((0,0),(0,1),(1,1),(1,0),(0,0))', '((0,0),(0,1),(1,1),(1,0),(0,0))','<(0,0),1>');
INSERT INTO GeoTable VALUES (3, NULL, NULL, NULL, NULL, NULL, NULL);

.export csv geo.csv
SELECT * FROM GeoTable ORDER BY a;
DELETE FROM GeoTable;
.import csv -h -f geo.csv
INSERT INTO GeoTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}}, {{c4}}, {{c5}}, {{c6}});
SELECT * FROM GeoTable ORDER BY a;

DROP TABLE GeoTable;

CREATE TABLE NetTable
(
	a  INTEGER,
	c1 CIDR,
	c2 INET,
	c3 MACADDR
);

INSERT INTO NetTable VALUES (1, '192.168.100.128/25', '127.0.0.1', '08:00:2b:01:02:03');
INSERT INTO NetTable VALUES (2, '192.168/24', '192.168.1.1', '08-00-2b-01-02-03');
INSERT INTO NetTable VALUES (3, NULL, NULL, NULL);

.export csv net.csv
SELECT * FROM NetTable ORDER BY a;
DELETE FROM NetTable;
.import csv -h -f net.csv
INSERT INTO NetTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}});
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

INSERT INTO RangeTable VALUES (1, '[1, 2]', '[12345,67890]', '[123.45,678.90]', '[2010-01-01 14:30, 2010-01-01 15:30]', '[2010-01-01 14:30-08:00, 2010-01-01 15:30-08:00]', '[2001-02-03,2002-03-04]');
INSERT INTO RangeTable VALUES (2, '[1, 2)', '[12345,67890)', '[123.45,678.90)', '[2010-01-01 14:30, 2010-01-01 15:30)', '[2010-01-01 14:30-08:00, 2010-01-01 15:30-08:00)', '[2001-02-03,2002-03-04)');
INSERT INTO RangeTable VALUES (3, NULL, NULL, NULL, NULL, NULL, NULL);

.export csv range.csv
SELECT * FROM RangeTable ORDER BY a;
DELETE FROM RangeTable;
.import csv -h -f range.csv
INSERT INTO RangeTable VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}}, {{c4}}, {{c5}}, {{c6}});
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

INSERT INTO MiscTable1 VALUES (1, true, B'101', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 12.34);
INSERT INTO MiscTable1 VALUES (2, false, B'1010', 'a0ee-bc99-9c0b-4ef8-bb6d-6bb9-bd38-0a11', 23.45);
INSERT INTO MiscTable1 VALUES (3, NULL, NULL, NULL, NULL);

.export csv misc1.csv
SELECT * FROM MiscTable1 ORDER BY a;
DELETE FROM MiscTable1;
-- DOUBLE PRECISION cannot be converted to MONEY directly.  Need to cast to
-- TEXT and then MONEY.
.import csv -h -f misc1.csv
INSERT INTO MiscTable1 VALUES ({{a}}, {{c1}}, {{c2}}, {{c3}}, {{c4}}::text::money);
SELECT * FROM MiscTable1 ORDER BY a;

DROP TABLE MiscTable1;

CREATE TABLE MiscTable2
(
	a  INTEGER,
	c1 TSVECTOR,
	c2 TSQUERY
);

INSERT INTO MiscTable2 VALUES (1, 'a fat cat sat on a mat and ate a fat rat', 'fat & rat');
INSERT INTO MiscTable2 VALUES (2, 'a fat cat sat on a mat and ate a fat rat', 'fat & rat');
INSERT INTO MiscTable2 VALUES (3, NULL, NULL);

.export csv misc2.csv
SELECT * FROM MiscTable2 ORDER BY a;
DELETE FROM MiscTable2;
.import csv -h -f misc2.csv
INSERT INTO MiscTable2 VALUES ({{a}}, {{c1}}, {{c2}});
SELECT * FROM MiscTable2 ORDER BY a;

DROP TABLE MiscTable2;

.os rm -f *.csv
.close
