--------------------------------------------------------------------------
-- test table format
--------------------------------------------------------------------------
.help format
.run ../common/derby_setup.sql
.open derby:memory:typesDB;create=true
.format table
.format
.format table -r asdf
.format table -c asdf
.format table -m asdf
.format table -r -1
.format table -r 1000

CREATE TABLE NumTable
(
	t1 SMALLINT,
	t2 INTEGER,
	t3 BIGINT,
	t4 FLOAT
);

INSERT INTO NumTable VALUES (-12345, -123456789, -1234567890, -1234.5);
INSERT INTO NumTable VALUES (12345, 123456789, 1234567890, 1234.5);

.format table -a off
SELECT * FROM NumTable ORDER BY t1;
.format table -a on
SELECT * FROM NumTable ORDER BY t1;
.format table -b on
SELECT * FROM NumTable ORDER BY t1;
.format table -b off
SELECT * FROM NumTable ORDER BY t1;
.format table -a on -c -1
SELECT * FROM NumTable ORDER BY t1;
.format table -a on -c 20
SELECT * FROM NumTable ORDER BY t1;
.format table -a off -m -1
SELECT * FROM NumTable ORDER BY t1;
.format table -a off -m 4
SELECT * FROM NumTable ORDER BY t1;
DROP TABLE NumTable;

.format table
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
DROP TABLE TimeTable;

CREATE TABLE XmlTable
(
	c1 INTEGER,
	c2 XML
);

INSERT INTO XmlTable VALUES (1, XMLPARSE(DOCUMENT '<abc>1234</abc>' PRESERVE WHITESPACE));
INSERT INTO XmlTable VALUES (2, XMLPARSE(DOCUMENT '<abc>2345</abc>' PRESERVE WHITESPACE));

SELECT c1, XMLSERIALIZE(c2 AS VARCHAR(100)) AS c2 FROM XmlTable ORDER BY c1;
DROP TABLE XmlTable;

.close
