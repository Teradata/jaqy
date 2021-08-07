--------------------------------------------------------------------------
-- pq import / export test
--------------------------------------------------------------------------
.run ../common/sqlserver_setup.sql

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b INTEGER, c VARCHAR(200), d VARBINARY(200));

INSERT INTO MyTable VALUES (1, null, null, null);

.export pq file_io_1.parquet
SELECT * FROM MyTable ORDER BY 1;
DROP TABLE MyTable;

.import pq file_io_1.parquet
.importschema
.importtable MyTable
.desc MyTable
SELECT * FROM MyTable;
DROP TABLE MyTable;

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b INTEGER, c VARCHAR(200), d VARBINARY(200));
INSERT INTO MyTable VALUES (1, 1, 'ab', CONVERT(VARBINARY, 0xdead));
INSERT INTO MyTable VALUES (2, null, 'abcd', CONVERT(VARBINARY, 0xdeadbeef));
INSERT INTO MyTable VALUES (3, 3, 'cd', CONVERT(VARBINARY, 0xbeef));
INSERT INTO MyTable VALUES (4, 4, 'cdef', CONVERT(VARBINARY, 0xfacefeed));
SELECT * FROM MyTable ORDER BY 1;

.export pq file_io_2.parquet
SELECT * FROM MyTable ORDER BY 1;
DROP TABLE MyTable;
.import pq file_io_2.parquet
.importschema
.importtable MyTable
.desc MyTable
SELECT * FROM MyTable;
DROP TABLE MyTable;

.close
.os rm -f file*.parquet

