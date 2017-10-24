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
.sort -d 2 -a 1
SELECT * FROM MyTable ORDER BY a;

.sort -d=b -d 1
SELECT * FROM MyTable ORDER BY a;

.sort -d="asdf"
SELECT * FROM MyTable ORDER BY a;

.sort -d=a
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.close
