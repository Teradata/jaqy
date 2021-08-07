--------------------------------------------------------------------------
-- pq import / export combo test
--------------------------------------------------------------------------
.run ../common/sqlserver_setup.sql

CREATE TABLE MyTable(a INTEGER, b BIT, c REAL, d FLOAT, e varbinary (100));
.desc MyTable

.export pq file_io_1.parquet
SELECT * FROM MyTable ORDER BY a;

INSERT INTO MyTable VALUES (1, 1, 1.1, 1.1, CONVERT(VARBINARY, 0xdeadbeef));
INSERT INTO MyTable VALUES (2, 0, 1.2, 1.2, CONVERT(VARBINARY, 0xdeadbeef));
INSERT INTO MyTable VALUES (3, 1, 1.32, 1.33, CONVERT(VARBINARY, 0xfacefeed));
INSERT INTO MyTable VALUES (4, 0, 2.54, 2.55, CONVERT(VARBINARY, 0xfacefeed));

SELECT * FROM MyTable ORDER BY a;

.export pq file_io_2.parquet
SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import pq file_io_1.parquet
INSERT INTO MyTable VALUES (?, ?, ?, ?, ?);
SELECT * FROM MyTable ORDER BY a;

.import pq file_io_2.parquet
INSERT INTO MyTable VALUES (?, ?, ?, ?, ?);
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.import pq file_io_1.parquet
.importschema
.importtable MyTable

.desc MyTable
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.import pq file_io_2.parquet
.importschema
.importtable MyTable

.desc MyTable
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.os rm -f file_io_?.parquet

