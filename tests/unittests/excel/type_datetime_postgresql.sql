--------------------------------------------------------------------------
-- date/time export / import test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE MyTable(a INTEGER, b DATE, c TIME, d TIMESTAMP);

INSERT INTO MyTable VALUES (1, '2001-01-01', '01:02:03', '2001-01-01 01:02:03');
INSERT INTO MyTable VALUES (2, '2001-01-02', null, '2001-02-01 01:02:03');
INSERT INTO MyTable VALUES (3, '2001-01-03', '03:02:03', '2001-03-01 01:02:03');
INSERT INTO MyTable VALUES (4, null, '04:02:03', '2001-04-01 01:02:03');
INSERT INTO MyTable VALUES (5, '2001-01-05', '05:02:03', null);
INSERT INTO MyTable VALUES (6, '2001-01-06', '06:02:03', '2001-06-01 01:02:03');

SELECT * FROM MyTable ORDER BY a;

.export excel datetime.xlsx
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import excel -h datetime.xlsx
INSERT INTO MyTable VALUES (?, ?, ?, ?);
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.import excel -h datetime.xlsx
.importtable MyTable
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.import excel -h -d1 -t2 -s3 datetime.xlsx
.importtable MyTable
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.os rm -f datetime.xlsx

