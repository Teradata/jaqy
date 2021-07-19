--------------------------------------------------------------------------
-- excel export / import test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql
.open derby:memory:identityDB;create=true

CREATE TABLE MyTable(a INTEGER, b VARCHAR(100), c BOOLEAN, d REAL);

INSERT INTO MyTable VALUES (1, 'This', false, 1.0);
INSERT INTO MyTable VALUES (2, 'is', true, null);
INSERT INTO MyTable VALUES (3, '一二三', null, 3.0);
INSERT INTO MyTable VALUES (4, null, false, 4.0);
INSERT INTO MyTable VALUES (5, '', false, 5.0);

.desc MyTable
SELECT * FROM MyTable ORDER BY a;

.export excel file_io_3.xlsx
SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import excel -h file_io_3.xlsx
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.import excel -h file_io_3.xlsx
.importtable MyTable
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

CREATE TABLE MyTable(a INTEGER, b VARCHAR(100), c INTEGER, d REAL);
.import excel -h file_io_3.xlsx
INSERT INTO MyTable VALUES (?, ?, ?, ?);
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.os rm -f file_io_3.xlsx

