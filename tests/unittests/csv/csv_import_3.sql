--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:csvDB;create=true

-- INTEGER type
CREATE TABLE MyTable(a INTEGER, b INTEGER);

.import csv -d| lib/bad1.csv
INSERT INTO MyTable VALUES (?, ?);

DROP TABLE MyTable;

.close

