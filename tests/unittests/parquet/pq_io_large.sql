--------------------------------------------------------------------------
-- pq import / export combo test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

.@quiet on
.@list . . mytable
.@quiet off
.@if activityCount > 0
DROP TABLE mytable;
.end if

CREATE TABLE MyTable
(
	a  INTEGER,
	b  DOUBLE PRECISION,
	t1 VARCHAR(100),
	t2 VARCHAR(100)
);

INSERT INTO MyTable VALUES (1, 1, NULL, NULL);

.repeat 22
INSERT INTO MyTable
SELECT a + (SELECT MAX(a) FROM MyTable),
       b, NULL, NULL
FROM MyTable;

SELECT COUNT(*) FROM MyTable;

UPDATE MyTable
SET b = (a * 0.001),
    t1 = 'POINT(' || a || ' ' || a || ')',
	t2 = 'POINT(' || a || ' ' || sin(a * 0.001) || ')'
;

SELECT a, b, t1 FROM MyTable WHERE a < 5 ORDER BY 1;

-- set the fetch size to avoid memory exhausting by JDBC driver.
.set autocommit off
.set fetchsize 20000
.export pq --pagesize 2mb --blocksize 1mb --rowcount 20000 --padding 1000 pq_io_large.parquet.snappy
SELECT * FROM MyTable ORDER BY 1;

.os stat -c "%n: %s" pq_io_large.parquet.snappy

DELETE FROM MyTable;

.import pq pq_io_large.parquet.snappy
.importtable -c MyTable

.set autocommit on

SELECT COUNT(*) FROM MyTable;
SELECT a, b, t1 FROM MyTable WHERE a < 5 ORDER BY 1;

DROP TABLE MyTable;

.os rm -f pq_io_large.parquet.snappy
