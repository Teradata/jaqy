--------------------------------------------------------------------------
-- PERIOD data type test
--------------------------------------------------------------------------
.run setup.sql
CREATE DATABASE vagrant AS PERM=1e8;
DATABASE vagrant;

CREATE TABLE clobTable
(
	a  INTEGER,
	b  VARCHAR(100),
	c  CLOB
);

INSERT INTO clobTable VALUES (1, 'abc', 'deaasdf');
INSERT INTO clobTable VALUES (2, 'abcd', NULL);
INSERT INTO clobTable VALUES (3, NULL, 'a quick brown fox');

.debug resultset on
SELECT * FROM clobTable ORDER BY 1;
.debug resultset off

.export csv clob.csv
SELECT * FROM clobTable ORDER BY 1;
.os cat clob.csv
DELETE FROM clobTable;
.import csv -h --nafilter clob.csv
.importtable -c clobTable
SELECT * FROM clobTable ORDER BY 1;
.os rm -f clob.csv

.export csv -f3 clob.csv
SELECT * FROM clobTable ORDER BY 1;
.os cat clob.csv
DELETE FROM clobTable;
.import csv -h --nafilter -j3 clob.csv
.importtable -c clobTable
SELECT * FROM clobTable ORDER BY 1;
.os rm -f clob.csv

.export json clob.json
SELECT * FROM clobTable ORDER BY 1;
.os cat clob.json && echo
DELETE FROM clobTable;
.import json clob.json
INSERT INTO clobTable VALUES ({{a}}, {{b}}, {{c}});
SELECT * FROM clobTable ORDER BY 1;
.os rm -f clob.json

.export avro clob.avro
SELECT * FROM clobTable ORDER BY 1;
DELETE FROM clobTable;
.import avro clob.avro
.importtable -c clobTable
SELECT * FROM clobTable ORDER BY 1;
.os rm -f clob.avro

.export excel clob.xlsx
SELECT * FROM clobTable ORDER BY 1;
DELETE FROM clobTable;
.import excel -h clob.xlsx
.importtable -c clobTable
SELECT * FROM clobTable ORDER BY 1;
.os rm -f clob.xlsx

DELETE DATABASE vagrant;
DROP DATABASE vagrant;
