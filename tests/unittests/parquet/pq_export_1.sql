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
.export pq --pagesize -1 dummy.pq
.export pq --pagesize asdf dummy.pq
.export pq --blocksize -1 dummy.pq
.export pq --blocksize asdf dummy.pq
.export pq --rowcount -1 dummy.pq
.export pq --rowcount asdf dummy.pq
.export pq --padding -1 dummy.pq
.export pq --padding asdf dummy.pq
.export pq --pagesize 1 --blocksize 1 --rowcount 1 --padding 1 file9.parquet.snappy
.export pq --pagesize 2mb --blocksize 1mb --rowcount 20000 --padding 1000 file9.parquet.snappy
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;
.close

.os rm -f file?.parquet*
