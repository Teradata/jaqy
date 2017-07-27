--------------------------------------------------------------------------
-- Transaction test
--------------------------------------------------------------------------
.run setup.sql
.autocommit off

CREATE DATABASE vagrant;
USE vagrant;

CREATE TABLE MyTable
(
	a	INTEGER PRIMARY KEY,
	b	VARCHAR(200)
);

BT;
INSERT INTO MyTable VALUES (1, '1');
INSERT INTO MyTable VALUES (2, '2');
INSERT INTO MyTable VALUES (3, '3');
INSERT INTO MyTable VALUES (4, '4');
INSERT INTO MyTable VALUES (5, '5');
ET;

SELECT * FROM MyTable ORDER BY a, b;

BT;
DELETE FROM MyTable WHERE a > 2;
SELECT * FROM MyTable ORDER BY a, b;
ROLLBACK;

SELECT * FROM MyTable ORDER BY a, b;

DROP TABLE MyTable;
DROP DATABASE vagrant;

.close

