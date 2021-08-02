--------------------------------------------------------------------------
-- PERIOD data type test
--------------------------------------------------------------------------
.run ../common/teradata_setup.sql
CREATE DATABASE vagrant AS PERM=1e8;
DATABASE vagrant;

CREATE TABLE jsonTable
(
	a  INTEGER,
	b  JSON
);

INSERT INTO jsonTable VALUES (1, '{"a":"abc","b":1}');
INSERT INTO jsonTable VALUES (2, NULL);
INSERT INTO jsonTable VALUES (3, '{"a":"defg","b":3}');

.debug resultset on
SELECT * FROM jsonTable ORDER BY 1;
.debug resultset off

.export csv myjson.csv
SELECT * FROM jsonTable ORDER BY 1;
.os cat myjson.csv
DELETE FROM jsonTable;
.import csv -h --nafilter myjson.csv
.importtable -c jsonTable
SELECT * FROM jsonTable ORDER BY 1;
.os rm -f myjson.csv

.export json myjson.json
SELECT * FROM jsonTable ORDER BY 1;
.os cat myjson.json && echo
DELETE FROM jsonTable;
.import json myjson.json
INSERT INTO jsonTable VALUES ({{a}}, {{b}});
SELECT * FROM jsonTable ORDER BY 1;
.os rm -f myjson.json

.export avro myjson.avro
SELECT * FROM jsonTable ORDER BY 1;
DELETE FROM jsonTable;
.import avro myjson.avro
.importtable -c jsonTable
SELECT * FROM jsonTable ORDER BY 1;
.os rm -f myjson.avro

.export excel myjson.xlsx
SELECT * FROM jsonTable ORDER BY 1;
DELETE FROM jsonTable;
.import excel -h myjson.xlsx
.importtable -c jsonTable
SELECT * FROM jsonTable ORDER BY 1;
.os rm -f myjson.xlsx

DELETE DATABASE vagrant;
DROP DATABASE vagrant;
