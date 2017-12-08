--------------------------------------------------------------------------
-- .import related commands test
--------------------------------------------------------------------------
.help import
.help importschema
.help importtable
.import
.importschema
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
.importschema
.importtable MyTable2
.import csv -h file1.csv
.importtable MyTable2
SELECT * FROM MyTable2 ORDER BY a;

.import csv -h file1.csv
.importschema
.importschema -s
.importtable
.importtable MyTable3
SELECT * FROM MyTable3 ORDER BY a;

DROP TABLE MyTable;
DROP TABLE MyTable2;
DROP TABLE MyTable3;

.close
.os rm -f file1.csv

