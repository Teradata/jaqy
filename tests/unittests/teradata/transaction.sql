--------------------------------------------------------------------------
-- Transaction test
--------------------------------------------------------------------------
.run setup.sql
.set autocommit off

CREATE DATABASE vagrant AS PERM=1e8;
ET;
DATABASE vagrant;
ET;

CREATE TABLE MyTable
(
	a	INTEGER,
	b	VARCHAR(200)
);
ET;

BT;
INSERT INTO MyTable VALUES (1, '1');
INSERT INTO MyTable VALUES (2, '2');
INSERT INTO MyTable VALUES (3, '3');
INSERT INTO MyTable VALUES (4, '4');
INSERT INTO MyTable VALUES (5, '5');
ET;

SELECT * FROM MyTable ORDER BY a, b;
ET;

BT;
DELETE FROM MyTable WHERE a > 2;
SELECT * FROM MyTable ORDER BY a, b;
ROLLBACK;

SELECT * FROM MyTable ORDER BY a, b;
ET;

DELETE DATABASE vagrant;
ET;
DROP DATABASE vagrant;
ET;

.close
