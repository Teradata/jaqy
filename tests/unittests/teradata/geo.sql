--------------------------------------------------------------------------
-- ST_GEOMETRY data type test
--------------------------------------------------------------------------
.run ../common/teradata_setup.sql
CREATE DATABASE vagrant AS PERM=1e8;
DATABASE vagrant;

CREATE TABLE geoTable
(
	a  INTEGER,
	b  ST_GEOMETRY
);

INSERT INTO geoTable VALUES (1, 'POINT(1 1)');
INSERT INTO geoTable VALUES (2, NULL);
INSERT INTO geoTable VALUES (3, 'LINESTRING(1 1, 2 2, 3 3)');

.debug resultset on
SELECT * FROM geoTable ORDER BY 1;
.debug resultset off

.export csv mygeo.csv
SELECT * FROM geoTable ORDER BY 1;
.os cat mygeo.csv
DELETE FROM geoTable;
.import csv -h --nafilter mygeo.csv
.importtable -c geoTable
SELECT * FROM geoTable ORDER BY 1;
.os rm -f mygeo.csv

.export avro mygeo.avro
SELECT * FROM geoTable ORDER BY 1;
DELETE FROM geoTable;
.import avro mygeo.avro
.importtable -c geoTable
SELECT * FROM geoTable ORDER BY 1;
.os rm -f mygeo.avro

.export excel mygeo.xlsx
SELECT * FROM geoTable ORDER BY 1;
DELETE FROM geoTable;
.import excel -h mygeo.xlsx
.importtable -c geoTable
SELECT * FROM geoTable ORDER BY 1;
.os rm -f mygeo.xlsx

.export json mygeo.json
SELECT * FROM geoTable ORDER BY 1;
.os cat mygeo.json && echo
DELETE FROM geoTable;
.import json mygeo.json
INSERT INTO geoTable VALUES ({{a}}, {{b}});
SELECT * FROM geoTable ORDER BY 1;
.os rm -f mygeo.json

DELETE DATABASE vagrant;
DROP DATABASE vagrant;
