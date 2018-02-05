--------------------------------------------------------------------------
-- .fetchsize command test
--------------------------------------------------------------------------
.help fetchsize
.fetchsize

.run ../common/postgresql_setup.sql

.fetchsize

CREATE TABLE TextTable
(
	a  INTEGER,
	c1 TEXT
);

INSERT INTO TextTable VALUES (1, '1');

.repeat 5
INSERT INTO TextTable
SELECT a + (SELECT max(a) FROM TextTable),
       c1 || (SELECT c1 FROM TextTable WHERE a = (SELECT max(a) FROM TextTable))
FROM TextTable;

.fetchsize -1
.fetchsize asdf
.fetchsize 0
.fetchsize 10
.fetchsize
.format csv
SELECT * FROM TextTable ORDER BY a;

DROP TABLE TextTable;
.close
.run ../common/postgresql_setup.sql
.fetchsize
.close
