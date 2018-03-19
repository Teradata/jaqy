--------------------------------------------------------------------------
-- nullsort setting test
--------------------------------------------------------------------------
.set nullsort
.set nullsort asdf

.run ../common/postgresql_setup.sql

CREATE TABLE MyTable (a INTEGER, b VARCHAR(10000));

INSERT INTO MyTable VALUES (1, 'POINT(1 1)');
INSERT INTO MyTable VALUES (2, 'POINT(2 2)');
INSERT INTO MyTable VALUES (3, 'POINT(3 3)');
INSERT INTO MyTable VALUES (4, 'POINT(4 4)');
INSERT INTO MyTable VALUES (5, NULL);

.sort 2
SELECT * FROM MyTable ORDER BY a;

.set nullsort low
.sort 2
SELECT * FROM MyTable ORDER BY a;

.set nullsort high
.sort 2
SELECT * FROM MyTable ORDER BY a;

DROP TABLE MyTable;

.close
