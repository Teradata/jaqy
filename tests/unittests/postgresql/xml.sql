--------------------------------------------------------------------------
-- test XML data type
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

CREATE TABLE xmlTable
(
	a  INTEGER,
	b  XML
);

.format table
.desc xmlTable
.desc -s xmlTable

INSERT INTO xmlTable VALUES (1, '<doc a="a">1</doc>');
INSERT INTO xmlTable VALUES (2, NULL);
INSERT INTO xmlTable VALUES (3, '<doc a="c">3</doc>');

.debug resultset on
SELECT * FROM xmlTable ORDER BY a;
.debug resultset off

.export csv myxml.csv
SELECT * FROM xmlTable ORDER BY a;
.os cat myxml.csv
DELETE FROM xmlTable;
.import csv -h -f myxml.csv
.importtable -c xmlTable
SELECT * FROM xmlTable ORDER BY a;

DROP TABLE xmlTable;
.close
.os rm -f myxml.csv
