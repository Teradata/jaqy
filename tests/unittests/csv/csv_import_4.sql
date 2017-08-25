--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:csvDB;create=true

CREATE TABLE MyTable (a INTEGER, b VARCHAR(100));

-- test csv without nan filter
.import csv -h on lib/import4.csv
INSERT INTO MyTable VALUES (?, ?);
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv with default nan filter
.import csv -h on -f lib/import4.csv
INSERT INTO MyTable VALUES (?, ?);
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv with a single NaN filter
.import csv -h on -f -v= lib/import4.csv
INSERT INTO MyTable VALUES (?, ?);
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv with a single NaN filter.  Last comma is ignored.
.import csv -h on -f -v=NaN, lib/import4.csv
INSERT INTO MyTable VALUES (?, ?);
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv with multiple NaN filter.  First comma is recognized.
.import csv -h on -f -v=,NaN lib/import4.csv
INSERT INTO MyTable VALUES (?, ?);
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;

.close

