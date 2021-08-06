--------------------------------------------------------------------------
-- pq import / export combo test
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE BinTable
(
	a  INTEGER,
	c1 BINARY(10),
	c2 VARBINARY(10),
	c3 BLOB,
	c4 ENUM('a','b','c') CHARACTER SET BINARY
);
.format table
.desc BinTable
.desc -s BinTable

INSERT INTO BinTable VALUES (1, X'deadbeef', X'deadbeef', X'deadbeef', 'a');
INSERT INTO BinTable VALUES (2, X'facefeed', X'facefeed', X'facefeed', 'b');
INSERT INTO BinTable VALUES (3, X'facefeed', X'face', X'feed', 'c');
INSERT INTO BinTable VALUES (4, NULL, NULL, NULL, NULL);

SELECT * FROM BinTable ORDER BY 1;

.export pq file_io_1.parquet
SELECT * FROM BinTable ORDER BY 1;

DELETE FROM BinTable;
.import pq file_io_1.parquet
INSERT INTO BinTable VALUES (?, ?, ?, ?, ?);
SELECT * FROM BinTable ORDER BY 1;
DROP TABLE BinTable;

.import pq file_io_1.parquet
.importtable BinTable
SELECT * FROM BinTable ORDER BY 1;
DROP TABLE BinTable;

.os rm -f file_io_?.parquet
