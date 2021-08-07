--------------------------------------------------------------------------
-- .export pq test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200) NOT NULL, c VARCHAR(200), d VARBYTE(200));

INSERT INTO MyTable VALUES (1, 'abc', null, X'deadbeef');
INSERT INTO MyTable VALUES (2, 'john', 'doe', null);
INSERT INTO MyTable VALUES (3, 'a"b', 'c"d', X'deadbeef');
INSERT INTO MyTable VALUES (4, 'a,b', 'c,d', X'deadbeef');
INSERT INTO MyTable VALUES (5, 'a''b', 'c''d', X'deadbeef');
INSERT INTO MyTable VALUES (6, 'a''",b', 'c''",d', X'deadbeef');
INSERT INTO MyTable VALUES (7, 'a	b', 'c,d', X'deadbeef');

.export pq
.export pq file1.parquet
.export
SELECT * FROM MyTable ORDER BY a;
.export pq -c null file2.parquet
SELECT * FROM MyTable ORDER BY a;
.export pq file3.parquet.gz
SELECT * FROM MyTable ORDER BY a;
.export pq -c snappy file4.parquet
SELECT * FROM MyTable ORDER BY a;
.export pq -c lzo file5.parquet
SELECT * FROM MyTable ORDER BY a;
.export pq -c brotli file6.parquet
.export pq -c lz4 file7.parquet
SELECT * FROM MyTable ORDER BY a;
.export pq -c zstd file8.parquet
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;
.close

.os rm -f file?.parquet
