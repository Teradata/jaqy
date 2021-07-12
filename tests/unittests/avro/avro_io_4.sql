--------------------------------------------------------------------------
-- avro import / export combo test
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE MyTable(a TINYINT, b NVARCHAR(100), c REAL);
.desc MyTable

INSERT INTO MyTable VALUES (1.00, N'a', 1.25);
INSERT INTO MyTable VALUES (2.00, N'甲', 2.45);
INSERT INTO MyTable VALUES (3.00, null, 3.33);

SELECT * FROM MyTable ORDER BY a;

.export avro file_io_1.avro
SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import avro file_io_1.avro
INSERT INTO MyTable VALUES (?, ?, ?);
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.import avro file_io_1.avro
.importschema
.importtable MyTable

.desc MyTable
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

CREATE TABLE MyTable(a DECIMAL(12,2), b NVARCHAR(100), c DECIMAL(12,2));
.desc MyTable
.import avro file_io_1.avro
INSERT INTO MyTable VALUES (?, ?, ?);
SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;

.os rm -f file_io_?.avro
