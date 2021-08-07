--------------------------------------------------------------------------
-- .import pq test
--------------------------------------------------------------------------
.run ../common/derby_setup.sql

.open derby:memory:pqDB;create=true
.format csv

CREATE TABLE MyTable(a INTEGER PRIMARY KEY, b VARCHAR(200) NOT NULL, c VARCHAR(200), d VARCHAR(200) FOR BIT DATA);

.debug preparedstatement on

.import pq
.import pq data/file1.parquet
.import
INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}}, {{d}});
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import pq https://github.com/Teradata/jaqy/raw/master/tests/unittests/parquet/data/file1.parquet
.import pq data/file1.parquet.gz
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import pq data/file1.parquet.snappy
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

.import pq data/file1.parquet.zstd
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY a;
DELETE FROM MyTable;

DROP TABLE MyTable;
