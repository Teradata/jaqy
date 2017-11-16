--------------------------------------------------------------------------
-- .repeat command test
--------------------------------------------------------------------------
.help limit
.limit
.limit 5
.limit asdf

.run ../common/postgresql_setup.sql

CREATE TABLE MyTable (a INTEGER, b VARCHAR(10000));

INSERT INTO MyTable VALUES (1, 'POINT(1 1)');

.repeat 5
INSERT INTO MyTable
SELECT a + (SELECT MAX(a) FROM MyTable),
       'POINT(' || (a + (SELECT MAX(a) FROM MyTable)) || ' ' || (a + (SELECT MAX(a) FROM MyTable)) || ')'
FROM MyTable;

.limit asdf
.limit
.limit -1234
.limit
.limit 1
.limit
SELECT * FROM MyTable ORDER BY a;

.limit 2
SELECT * FROM MyTable ORDER BY a;

.limit 12
SELECT * FROM MyTable ORDER BY b;

.limit 12
.sort -d 1
SELECT * FROM MyTable ORDER BY b;

DROP TABLE MyTable;

.close
