--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.format csv

.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable(a INTEGER, b CLOB);

.import csv -h -j-1 lib/errorlob.csv
INSERT INTO MyTable VALUES (?, ?);

.import csv -h -k-1 lib/errorlob.csv
INSERT INTO MyTable VALUES (?, ?);

DROP TABLE MyTable;

.close
