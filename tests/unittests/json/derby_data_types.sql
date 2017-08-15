--------------------------------------------------------------------------
-- test Apache Derby various data types
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:typesDB;create=true
.format json -p on -b hex

CREATE TABLE NumTable
(
	t1 SMALLINT,
	t2 INTEGER,
	t3 BIGINT,
	t4 FLOAT
);

INSERT INTO NumTable VALUES (-12345, -123456789, -1234567890, -1234.5);
INSERT INTO NumTable VALUES (12345, 123456789, 1234567890, 1234.5);

SELECT * FROM NumTable ORDER BY t1;
.export json num.json
SELECT * FROM NumTable ORDER BY t1;
DELETE FROM NumTable;
.import json num.json
INSERT INTO NumTable VALUES ({{T1}},{{T2}},{{T3}},{{T4}});
SELECT * FROM NumTable ORDER BY t1;
DROP TABLE NumTable;

-- Derby supports DECIMAL precision up to 31.
CREATE TABLE DecTable
(
	d1 DECIMAL(3,2),
	d2 DECIMAL(9,2),
	d3 DECIMAL(22,2),
	d4 DECIMAL(31,2)
);
INSERT INTO DecTable VALUES (-1.23, -1234567.89, -12345678901234567890.12, -12345678901234567890123456789.01);
INSERT INTO DecTable VALUES (1.23, 1234567.89, 12345678901234567890.12, 12345678901234567890123456789.01);

SELECT * FROM DecTable ORDER BY d1;
.export json dec.json
SELECT * FROM DecTable ORDER BY d1;
DELETE FROM DecTable;
.import json dec.json
INSERT INTO DecTable VALUES ({{D1}},{{D2}},{{D3}},{{D4}});
SELECT * FROM DecTable ORDER BY d1;
DROP TABLE DecTable;

CREATE TABLE StrTable
(
	s1 VARCHAR(255),
	s2 CHAR(1),
	s3 CHAR(5)
);

INSERT INTO StrTable VALUES ('aa', 'A', 'abcde');
INSERT INTO StrTable VALUES ('bbb', 'B', 'fghij');

SELECT * FROM StrTable ORDER BY s1;
.export json str.json
SELECT * FROM StrTable ORDER BY s1;
DELETE FROM StrTable;
.import json str.json
INSERT INTO StrTable VALUES ({{S1}},{{S2}},{{S3}});
SELECT * FROM StrTable ORDER BY s1;
DROP TABLE StrTable;

CREATE TABLE BinTable
(
	c1 INTEGER,
	c2 LONG VARCHAR,
	c3 LONG VARCHAR FOR BIT DATA
);

INSERT INTO BinTable VALUES (1, 'A str', X'deadbeef');
INSERT INTO BinTable VALUES (2, 'B str', X'facefeed');

SELECT * FROM BinTable ORDER BY c1;
.export json bin.json
SELECT * FROM BinTable ORDER BY c1;
DELETE FROM BinTable;
.import json bin.json
INSERT INTO BinTable VALUES ({{C1}},{{C2}},{{C3}});
SELECT * FROM BinTable ORDER BY c1;
DROP TABLE BinTable;

CREATE TABLE LobTable
(
	c1 INTEGER,
	c2 CLOB(1K),
	c3 BLOB(1K)
);

INSERT INTO LobTable VALUES (1, 'A clob', CAST(X'deadbeef' AS BLOB));
INSERT INTO LobTable VALUES (2, 'B clob', CAST(X'facefeed' AS BLOB));

SELECT * FROM LobTable ORDER BY c1;
.export json lob.json
SELECT * FROM LobTable ORDER BY c1;
DELETE FROM LobTable;
.import json lob.json
INSERT INTO LobTable VALUES ({{C1}},{{C2}},{{C3}});
SELECT * FROM LobTable ORDER BY c1;
DROP TABLE LobTable;

CREATE TABLE TimeTable
(
	c1 INTEGER,
	c2 DATE,
	c3 TIME,
	c4 TIMESTAMP
);

INSERT INTO TimeTable VALUES (1, '2001-02-03', '12:34:56', '2001-02-03 12:34:56');
INSERT INTO TimeTable VALUES (2, '2001-03-04', '12:34:56', '2001-03-04 12:34:56');

SELECT * FROM TimeTable ORDER BY c1;
.export json time.json
SELECT * FROM TimeTable ORDER BY c1;
DELETE FROM TimeTable;
.import json time.json
INSERT INTO TimeTable VALUES ({{C1}},{{C2}},{{C3}},{{C4}});
SELECT * FROM TimeTable ORDER BY c1;
DROP TABLE TimeTable;

CREATE TABLE XmlTable
(
	c1 INTEGER,
	c2 XML
);

INSERT INTO XmlTable VALUES (1, XMLPARSE(DOCUMENT '<abc>1234</abc>' PRESERVE WHITESPACE));
INSERT INTO XmlTable VALUES (2, XMLPARSE(DOCUMENT '<abc>2345</abc>' PRESERVE WHITESPACE));

SELECT c1, XMLSERIALIZE(c2 AS VARCHAR(100)) AS c2 FROM XmlTable ORDER BY c1;
.export json xml.json
SELECT c1, XMLSERIALIZE(c2 AS VARCHAR(100)) AS c2 FROM XmlTable ORDER BY c1;
DELETE FROM XmlTable;
.import json xml.json
INSERT INTO XMLTable VALUES ({{C1}},XMLPARSE(DOCUMENT CAST({{C2}} AS VARCHAR(100)) PRESERVE WHITESPACE));
SELECT c1, XMLSERIALIZE(c2 AS VARCHAR(100)) AS c2 FROM XmlTable ORDER BY c1;
DROP TABLE XmlTable;

.os rm -f *.json
.close
