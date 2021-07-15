--------------------------------------------------------------------------
-- excel export / import test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE MyTable(a INTEGER, b DATE, c TIME, d TIMESTAMP, e BOOL, f REAL);

INSERT INTO MyTable VALUES (1, '2001-01-01', '01:02:03', '2001-01-01 01:02:03', false, 1.0);
INSERT INTO MyTable VALUES (2, '2001-01-02', null, '2001-02-01 01:02:03', true, 2.0);
INSERT INTO MyTable VALUES (3, '2001-01-03', '03:02:03', '2001-03-01 01:02:03', null, 3.0);
INSERT INTO MyTable VALUES (4, null, '04:02:03', '2001-04-01 01:02:03', true, null);
INSERT INTO MyTable VALUES (5, '2001-01-05', '05:02:03', null, false, 5.0);

INSERT INTO MyTable VALUES (6, '2001-01-06', '06:02:03', '2001-06-01 01:02:03', false, 6.0);

SELECT * FROM MyTable ORDER BY a;

.export excel file_io_2.xlsx
SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import excel -h file_io_2.xlsx
INSERT INTO MyTable VALUES (?, ?, ?, ?, ?, ?);

SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.import excel -h file_io_2.xlsx
.importtable MyTable

SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.import excel -h -d1 -t2 -s3 file_io_2.xlsx
.importtable MyTable

SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.os rm -f file_io_2.xlsx

