--------------------------------------------------------------------------
-- pq import / export combo test
--------------------------------------------------------------------------
.run ../common/sqlserver_setup.sql

CREATE TABLE MyTable(a INTEGER, b binary(10), e varbinary (10));
.desc MyTable

INSERT INTO MyTable VALUES (1, CONVERT(BINARY, 0xdead), CONVERT(VARBINARY, 0xdeadbeef));
INSERT INTO MyTable VALUES (2, NULL, CONVERT(VARBINARY, 0xface));
INSERT INTO MyTable VALUES (3, CONVERT(BINARY, 0xbeef), CONVERT(VARBINARY, 0xfeed));

SELECT * FROM MyTable ORDER BY a;

.debug resultset on
.export pq file_binary.parquet.snappy
SELECT * FROM MyTable ORDER BY a;
.debug resultset off

DELETE FROM MyTable;

.debug preparedstatement on
.import pq file_binary.parquet.snappy
INSERT INTO MyTable VALUES (?, ?, ?);
.debug preparedstatement off

SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;
.os rm -f file_binary.parquet.snappy

