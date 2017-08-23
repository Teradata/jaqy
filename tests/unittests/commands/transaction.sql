--------------------------------------------------------------------------
-- Transaction test
--------------------------------------------------------------------------
.help autocommit
.help commit
.help rollback
.commit
.rollback
.autocommit on
.autocommit off
.autocommit

.run ../common/mysql_setup.sql
USE vagrant;

.autocommit

CREATE TABLE MyTable
(
	a	INTEGER PRIMARY KEY,
	b	VARCHAR(200)
);

.autocommit off
.autocommit
INSERT INTO MyTable VALUES (1, '1');
INSERT INTO MyTable VALUES (2, '2');
.commit

SELECT * FROM MyTable ORDER BY a;

INSERT INTO MyTable VALUES (3, '3');
INSERT INTO MyTable VALUES (4, '4');
.rollback

SELECT * FROM MyTable ORDER BY a;

.autocommit on
.autocommit

INSERT INTO MyTable VALUES (3, '4');
INSERT INTO MyTable VALUES (3, '4');
.rollback

SELECT * FROM MyTable ORDER BY a, b;

DROP TABLE MyTable;

.close

