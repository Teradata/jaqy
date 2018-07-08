--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.format csv

.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable(a INTEGER, b INTEGER, c INTEGER, d VARCHAR(100), e VARCHAR(100), f VARBYTE(100), g CLOB, h BLOB);

.import csv -h -f -j5 -k6 -j7 -k8 lib/null.csv
INSERT INTO MyTable VALUES (?, ?, ?, ?, ?, ?, ?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;
DROP TABLE MyTable;

.close

.run ../common/derby_setup.sql
.open derby:memory:csvDB;create=true

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b INTEGER, c INTEGER, d VARCHAR(100), e VARCHAR(100), f VARCHAR(100) FOR BIT DATA, g CLOB, h BLOB);

.import csv -h -f -j5 -k6 -j7 -k8 lib/null.csv
INSERT INTO MyTable VALUES (?, ?, ?, ?, ?, ?, ?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;
DROP TABLE MyTable;

.close

.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE MyTable(a INTEGER, b INTEGER, c INTEGER, d VARCHAR(100), e VARCHAR(100), f VARBINARY(100), g TEXT, h BLOB);

.import csv -h -f -j5 -k6 -j7 -k8 lib/null.csv
INSERT INTO MyTable VALUES (?, ?, ?, ?, ?, ?, ?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;
DROP TABLE MyTable;

.close

.run ../common/postgresql_setup.sql

CREATE TABLE MyTable(a INTEGER, b INTEGER, c INTEGER, d VARCHAR(100), e VARCHAR(100), f bytea, g TEXT, h bytea);

.import csv -h -f -j5 -k6 -j7 -k8 lib/null.csv
INSERT INTO MyTable VALUES (?, ?, ?, ?, ?, ?, ?, ?);

SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;
DROP TABLE MyTable;

.close
