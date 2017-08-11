--------------------------------------------------------------------------
-- .export json test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200), d BLOB);

INSERT INTO MyTable VALUES (1, 'abc', 'def', X'deadbeef');
INSERT INTO MyTable VALUES (2, 'john', 'doe', null);
INSERT INTO MyTable VALUES (3, 'a"b', 'c"d', X'deadbeef');
INSERT INTO MyTable VALUES (4, 'a,b', 'c,d', X'deadbeef');
INSERT INTO MyTable VALUES (5, 'a''b', 'c''d', X'deadbeef');
INSERT INTO MyTable VALUES (6, 'a''",b', 'c''",d', X'deadbeef');
INSERT INTO MyTable VALUES (7, 'a	b', 'c,d', X'deadbeef');

.export json

.export json file1.json
SELECT * FROM MyTable ORDER BY a;
.os cat file1.json

.export json -b hex file2.json
SELECT * FROM MyTable ORDER BY a;
.os cat file2.json

.export json -c utf-16le -p on file3.json
SELECT * FROM MyTable ORDER BY a;

.export json -f bson file4.bson
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;
.close

.os rm -f file?.json file?.bson

