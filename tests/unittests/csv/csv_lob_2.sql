--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.format csv

.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable(a INTEGER, b CLOB);

.import csv -h -j2 lib/errorlob.csv
INSERT INTO MyTable VALUES (?, ?);
DROP TABLE MyTable;

.close
