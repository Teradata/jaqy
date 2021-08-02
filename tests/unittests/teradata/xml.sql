--------------------------------------------------------------------------
-- XML data type test
--------------------------------------------------------------------------
.run ../common/teradata_setup.sql
CREATE DATABASE vagrant AS PERM=1e8;
DATABASE vagrant;

CREATE TABLE xmlTable
(
	a  INTEGER,
	b  XML
);

INSERT INTO xmlTable VALUES (1, CREATEXML('<doc a="abc">1</doc>'));
INSERT INTO xmlTable VALUES (2, NULL);
INSERT INTO xmlTable VALUES (3, CREATEXML('<doc a="defg">3</doc>'));

.debug resultset on
SELECT * FROM xmlTable ORDER BY 1;
.debug resultset off

.export csv myxml.csv
SELECT * FROM xmlTable ORDER BY 1;
.os cat myxml.csv
DELETE FROM xmlTable;
.import csv -h --nafilter myxml.csv
.importtable -c xmlTable
SELECT * FROM xmlTable ORDER BY 1;
.os rm -f myxml.csv

.export avro myxml.avro
SELECT * FROM xmlTable ORDER BY 1;
DELETE FROM xmlTable;
.import avro myxml.avro
.importtable -c xmlTable
SELECT * FROM xmlTable ORDER BY 1;
.os rm -f myxml.avro

.export excel myxml.xlsx
SELECT * FROM xmlTable ORDER BY 1;
DELETE FROM xmlTable;
.import excel -h myxml.xlsx
.importtable -c xmlTable
SELECT * FROM xmlTable ORDER BY 1;
.os rm -f myxml.xlsx

.export json myxml.json
SELECT * FROM xmlTable ORDER BY 1;
.os cat myxml.json && echo
DELETE FROM xmlTable;
.import json myxml.json
INSERT INTO xmlTable VALUES ({{a}}, {{b}});
SELECT * FROM xmlTable ORDER BY 1;
.os rm -f myxml.json

DELETE DATABASE vagrant;
DROP DATABASE vagrant;
