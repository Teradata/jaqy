--------------------------------------------------------------------------
-- avro import / export combo test
--------------------------------------------------------------------------
.run ../common/sqlserver_setup.sql

CREATE TABLE MyTable(a INTEGER, b BIT, c FLOAT, d varbinary (100));
.desc MyTable

.export avro file_io_1.avro
SELECT * FROM MyTable ORDER BY a;

INSERT INTO MyTable VALUES (1, 1, 1.1, CONVERT(VARBINARY, 0xdeadbeef));
INSERT INTO MyTable VALUES (2, 0, 1.2, CONVERT(VARBINARY, 0xdeadbeef));
INSERT INTO MyTable VALUES (3, 1, 1.33, CONVERT(VARBINARY, 0xfacefeed));
INSERT INTO MyTable VALUES (4, 0, 2.55, CONVERT(VARBINARY, 0xfacefeed));

SELECT * FROM MyTable ORDER BY a;

.export avro file_io_2.avro
SELECT * FROM MyTable ORDER BY a;

DELETE FROM MyTable;

.import avro file_io_1.avro
INSERT INTO MyTable VALUES (?, ?, ?, ?);
SELECT * FROM MyTable ORDER BY a;

.import avro file_io_2.avro
INSERT INTO MyTable VALUES (?, ?, ?, ?);
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.import avro file_io_1.avro
.importschema
.importtable MyTable

.desc MyTable
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.import avro file_io_2.avro
.importschema
.importtable MyTable

.desc MyTable
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.os rm -f file_io_?.avro

