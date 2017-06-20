--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.help import
.import

.open derby:memory:myDB;create=true

-- VARCHAR type
CREATE TABLE MyTable(a VARCHAR(200) PRIMARY KEY, b VARCHAR(200));

.debug preparedstatement on

-- test csv with header
.import csv -h on lib\import1.csv
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv with header
.import csv -h off lib\import2.csv
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;

-- INTEGER type
CREATE TABLE MyTable(a INTEGER, b INTEGER);
-- test csv with header
.import csv -h on lib\import1.csv
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

-- test csv forgetting to skip header
.import csv -h off lib\import1.csv
INSERT INTO MyTable VALUES (?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;
