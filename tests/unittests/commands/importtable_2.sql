--------------------------------------------------------------------------
-- .importtable command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200));

INSERT INTO MyTable VALUES (1, 'abc', 'def');
INSERT INTO MyTable VALUES (2, 'john', 'doe');
INSERT INTO MyTable VALUES (3, 'a"b', 'c"d');
INSERT INTO MyTable VALUES (4, 'a,b', 'c,d');
INSERT INTO MyTable VALUES (5, 'a''b', 'c''d');
INSERT INTO MyTable VALUES (6, 'a''",b', 'c''",d');
INSERT INTO MyTable VALUES (7, 'a	b', 'c,d');

.export csv file1.csv
SELECT * FROM MyTable ORDER BY a;

CREATE TABLE MyTable2(a INTEGER PRIMARY KEY, b VARCHAR(200));

.import csv file1.csv
.importtable -c MyTable2
SELECT * FROM MyTable2 ORDER BY a;
DROP TABLE MyTable2;

CREATE TABLE MyTable2(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200), d INTEGER);

.import csv file1.csv
.importtable -c MyTable2
SELECT * FROM MyTable2 ORDER BY a;

DROP TABLE MyTable;
DROP TABLE MyTable2;

.os rm -f file1.csv

