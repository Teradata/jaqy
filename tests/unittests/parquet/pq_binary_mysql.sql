--------------------------------------------------------------------------
-- pq import / export combo test
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE MyTable(a INTEGER, b binary(10), e varbinary (10));
.desc MyTable

INSERT INTO MyTable VALUES (1, X'dead', X'deadbeef');
INSERT INTO MyTable VALUES (2, NULL, X'face');
INSERT INTO MyTable VALUES (3, X'beef', X'feed');

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

