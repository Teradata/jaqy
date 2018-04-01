--------------------------------------------------------------------------
-- test exporting large amount of data
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE TextTable
(
	a  INTEGER,
	c1 TEXT
);

INSERT INTO TextTable VALUES (1, '1');

.repeat 16
INSERT INTO TextTable
SELECT a + (SELECT max(a) FROM TextTable),
       c1 || (SELECT c1 FROM TextTable WHERE a = (SELECT max(a) FROM TextTable))
FROM TextTable;

.set autocommit off
.set fetchsize 50
.export csv texttable.csv
SELECT * FROM TextTable ORDER BY a;

.set autocommit on
DROP TABLE TextTable;

.os wc -c texttable.csv
.os rm -f texttable.csv
.close
