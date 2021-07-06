--------------------------------------------------------------------------
-- .export csv test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200), c VARCHAR(200));

.format csv
SELECT * FROM MyTable;

.export csv dummy.csv
SELECT * FROM MyTable ORDER BY a;
.os cat dummy.csv
.os rm -f dummy.csv

DROP TABLE MyTable;

.close

