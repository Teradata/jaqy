--------------------------------------------------------------------------
-- .# command test
--------------------------------------------------------------------------
.help #
.#
.# 5
.5

.run ../common/postgresql_setup.sql

.1

CREATE TABLE MyTable (a INTEGER, b VARCHAR(10000));

INSERT INTO MyTable VALUES (1, 'POINT(1 1)');

INSERT INTO MyTable
SELECT a + (SELECT MAX(a) FROM MyTable),
       'POINT(' || (a + (SELECT MAX(a) FROM MyTable)) || ' ' || (a + (SELECT MAX(a) FROM MyTable)) || ')'
FROM MyTable;

.#
.# -1
.# 2
.2

SELECT * FROM MyTable ORDER BY a;

SELECT 1234;
.# ${5 - 3}


SELECT ${iteration} AS Test;
.# ${5 - 3}

DROP TABLE MyTable;

.close
