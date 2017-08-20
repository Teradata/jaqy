--------------------------------------------------------------------------
-- Transaction test
--------------------------------------------------------------------------
.run ../common/mysql_setup.sql
USE vagrant;

CREATE TABLE MyTable
(
	a	INTEGER PRIMARY KEY,
	b	VARCHAR(200)
);

.autocommit off
INSERT INTO MyTable VALUES (1, '1');
INSERT INTO MyTable VALUES (2, '2');
.commit

SELECT * FROM MyTable ORDER BY a;

INSERT INTO MyTable VALUES (3, '3');
INSERT INTO MyTable VALUES (4, '4');
.rollback

SELECT * FROM MyTable ORDER BY a;

.autocommit on

INSERT INTO MyTable VALUES (3, '4');
INSERT INTO MyTable VALUES (3, '4');
.rollback

SELECT * FROM MyTable ORDER BY a, b;

DROP TABLE MyTable;

.close

