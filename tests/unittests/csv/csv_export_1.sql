--------------------------------------------------------------------------
-- .export csv test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200));

INSERT INTO MyTable VALUES (1, 'abc', 'def');
INSERT INTO MyTable VALUES (2, 'john', 'doe');
INSERT INTO MyTable VALUES (3, 'a"b', 'c"d');
INSERT INTO MyTable VALUES (4, 'a,b', 'c,d');
INSERT INTO MyTable VALUES (5, 'a''b', 'c''d');
INSERT INTO MyTable VALUES (6, 'a''",b', 'c''",d');
INSERT INTO MyTable VALUES (7, 'a	b', 'c,d');

.export csv

.export csv -c utf-8 file1.csv
SELECT * FROM MyTable ORDER BY a;
.os cat file1.csv

.export csv -type default -d | file2.csv
SELECT * FROM MyTable ORDER BY a;
.os cat file2.csv

.export csv -d | file3.csv
SELECT * FROM MyTable ORDER BY a;
.os cat file3.csv

.export csv file4.csv
SELECT * FROM MyTable ORDER BY a;
.os cat file4.csv

.close
.os rm -f file?.csv

