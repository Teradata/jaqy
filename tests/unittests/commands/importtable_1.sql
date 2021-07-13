--------------------------------------------------------------------------
-- .importtable command test
--------------------------------------------------------------------------
.help importtable
.importtable
.importtable MyTable2

.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200), d BLOB);

INSERT INTO MyTable VALUES (1, 'abc', 'def', X'deadbeef');
INSERT INTO MyTable VALUES (2, 'john', 'doe', null);
INSERT INTO MyTable VALUES (3, 'a"b', 'c"d', X'deadbeef');
INSERT INTO MyTable VALUES (4, 'a,b', 'c,d', X'deadbeef');
INSERT INTO MyTable VALUES (5, 'a''b', 'c''d', X'deadbeef');
INSERT INTO MyTable VALUES (6, 'a''",b', 'c''",d', X'deadbeef');
INSERT INTO MyTable VALUES (7, 'a	b', 'c,d', X'deadbeef');

.export csv file1.csv
SELECT * FROM MyTable ORDER BY a;

.importtable
.importtable MyTable2

.import csv -h file1.csv
.importtable MyTable2
SELECT * FROM MyTable2 ORDER BY a;

DELETE FROM MyTable2;
.import csv -h file1.csv
.importtable MyTable2
SELECT * FROM MyTable2 ORDER BY a;

DELETE FROM MyTable2;
.import csv -h file1.csv
.importtable -c MyTable2
SELECT * FROM MyTable2 ORDER BY a;

DROP TABLE MyTable2;
.import csv -h file1.csv
.importtable -c MyTable2
SELECT * FROM MyTable2 ORDER BY a;

DROP TABLE MyTable;
DROP TABLE MyTable2;

.os rm -f file1.csv

