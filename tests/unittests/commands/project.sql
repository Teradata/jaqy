--------------------------------------------------------------------------
-- .project command test
--------------------------------------------------------------------------
.help project
.project

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

UPDATE MyTable SET b = NULL WHERE a % 2 = 0;

SELECT * FROM MyTable ORDER BY a;

.project

.project b
SELECT * FROM MyTable ORDER BY a;

.filter a > 3 and a < 10
.project b
SELECT * FROM MyTable ORDER BY a;

.filter a > 3 and a < 10
.project b
.sort b DESC, a ASC
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.close
