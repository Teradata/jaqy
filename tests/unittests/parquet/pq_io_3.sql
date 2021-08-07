--------------------------------------------------------------------------
-- pq import / export combo test
--------------------------------------------------------------------------
.run ../common/sqlserver_setup.sql

CREATE TABLE MyTable(a DECIMAL(12,2), b NVARCHAR(100));
.desc MyTable

INSERT INTO MyTable VALUES (1.00, N'a');
INSERT INTO MyTable VALUES (2.00, N'甲');
INSERT INTO MyTable VALUES (3.00, null);

SELECT * FROM MyTable ORDER BY a;

.export pq file_io_1.parquet
SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import pq file_io_1.parquet
INSERT INTO MyTable VALUES (?, ?);
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.import pq file_io_1.parquet
.importschema
.importtable MyTable

.desc MyTable
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

CREATE TABLE MyTable(a DECIMAL(12,2), b NVARCHAR(100));
.desc MyTable

INSERT INTO MyTable VALUES (1.00, N'a');
INSERT INTO MyTable VALUES (2.00, N'甲乙');
INSERT INTO MyTable VALUES (3.00, N'丙');
INSERT INTO MyTable VALUES (4.00, N'甲乙');

SELECT * FROM MyTable ORDER BY a;

.export pq file_io_2.parquet
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.import pq file_io_2.parquet
.importschema
.importtable MyTable

.desc MyTable
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.os rm -f file_io_?.parquet
