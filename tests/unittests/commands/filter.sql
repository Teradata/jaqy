--------------------------------------------------------------------------
-- .filter command test
--------------------------------------------------------------------------
.help filter
.filter

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

-- unfiltered
SELECT * FROM MyTable ORDER BY a;

.filter

-- various filters
.filter a > 3 and a < 10
SELECT * FROM MyTable ORDER BY a;
.filter a between 3 and 10
SELECT * FROM MyTable ORDER BY a;

.filter a = 3 and b LIKE 'point'
SELECT * FROM MyTable ORDER BY a;
.eval interpreter.setCaseInsensitive (true)
.filter a = 3 and b LIKE 'point'
SELECT * FROM MyTable ORDER BY a;

.filter a < ANY (2, 3, 4)
SELECT * FROM MyTable ORDER BY a;
-- SOME is the same as ANY
.filter a < SOME (2, 3, 4)
SELECT * FROM MyTable ORDER BY a;
.filter a < ALL (2, 3, 4)
SELECT * FROM MyTable ORDER BY a;

.eval var myfunc = function(a,b) { return a + b; }
.filter a > myfunc(3,4) and a < 10
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.close
