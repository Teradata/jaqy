--------------------------------------------------------------------------
-- fetchsize setting test
--------------------------------------------------------------------------
.set fetchsize

.run ../common/postgresql_setup.sql

.set fetchsize

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

.set fetchsize -1
.set fetchsize asdf
.set fetchsize 0
.set fetchsize 10
.set fetchsize
.format csv
SELECT * FROM TextTable ORDER BY a;

DROP TABLE TextTable;
.close
.run ../common/postgresql_setup.sql
.set fetchsize
.close
