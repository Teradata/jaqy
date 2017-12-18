--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:csvDB;create=true

-- VARCHAR type
CREATE TABLE MyTable(a VARCHAR(200) PRIMARY KEY, b VARCHAR(200));

.debug preparedstatement on

.import csv
.import csv -d asdf

-- test csv with header
.import csv -h lib/import1.csv
.importschema
.importschema -s
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv with header
.import csv lib/import2.csv
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;

-- INTEGER type
CREATE TABLE MyTable(a INTEGER, b INTEGER);
-- test csv with header
.import csv -h lib/import1.csv
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv forgetting to skip header
.import csv lib/import1.csv
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;

.close

