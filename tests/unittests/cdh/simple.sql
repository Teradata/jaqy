--------------------------------------------------------------------------
-- simple DDL tests
--------------------------------------------------------------------------
.run setup.sql

DROP TABLE MyTable;

CREATE TABLE MyTable
(
	a	INT,
	b	VARCHAR(200)
);

INSERT INTO MyTable VALUES (1, '1');
INSERT INTO MyTable VALUES (2, '2');
INSERT INTO MyTable VALUES (3, '3');
INSERT INTO MyTable VALUES (4, '4');
INSERT INTO MyTable VALUES (5, '5');

SELECT * FROM MyTable ORDER BY a, b;
