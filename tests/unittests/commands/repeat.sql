--------------------------------------------------------------------------
-- .repeat command test
--------------------------------------------------------------------------
.help repeat
.repeat
.repeat 5

.run ../common/postgresql_setup.sql

CREATE TABLE MyTable (a INTEGER, b VARCHAR(10000));

INSERT INTO MyTable VALUES (1, 'POINT(1 1)');

.repeat
.repeat 0
.repeat 5
INSERT INTO MyTable
SELECT a + (SELECT MAX(a) FROM MyTable),
       'POINT(' || (a + (SELECT MAX(a) FROM MyTable)) || ' ' || (a + (SELECT MAX(a) FROM MyTable)) || ')'
FROM MyTable;

SELECT * FROM MyTable ORDER BY a;

.repeat ${5 - 3}
SELECT ${iteration} AS Test;

DROP TABLE MyTable;

.close
