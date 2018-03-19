--------------------------------------------------------------------------
-- .sort command test
--------------------------------------------------------------------------
.help sort
.sort

.run ../common/postgresql_setup.sql

CREATE TABLE MyTable (a INTEGER, b VARCHAR(10000));

INSERT INTO MyTable VALUES (1, 'POINT(1 1)');
.repeat 4
INSERT INTO MyTable
SELECT a + (SELECT MAX(a) FROM MyTable),
       'POINT(' || (a + (SELECT MAX(a) FROM MyTable)) || ' ' || (a + (SELECT MAX(a) FROM MyTable)) || ')'
FROM MyTable;

INSERT INTO MyTable
SELECT a + (SELECT MAX(a) FROM MyTable),
       b
FROM MyTable;

.sort
.sort 2 desc, 1 asc
SELECT * FROM MyTable ORDER BY a;

.sort b desc, 1 desc
SELECT * FROM MyTable ORDER BY a;

.sort "asdf" desc
SELECT * FROM MyTable ORDER BY a;

.sort a desc
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.close
