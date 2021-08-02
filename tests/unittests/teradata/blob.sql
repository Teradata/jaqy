--------------------------------------------------------------------------
-- PERIOD data type test
--------------------------------------------------------------------------
.run ../common/teradata_setup.sql
CREATE DATABASE vagrant AS PERM=1e8;
DATABASE vagrant;

CREATE TABLE blobTable
(
	a  INTEGER,
	b  VARBYTE(100),
	c  BLOB
);

INSERT INTO blobTable VALUES (1, 'dead'xb, 'deadbeef'xb);
INSERT INTO blobTable VALUES (2, 'facefeed'xb, NULL);
INSERT INTO blobTable VALUES (3, NULL, 'deadbeeffacefeed'xb);

.debug resultset on
SELECT * FROM blobTable ORDER BY 1;
.debug resultset off

.export csv blob.csv
SELECT * FROM blobTable ORDER BY 1;
.os cat blob.csv
DELETE FROM blobTable;
.import csv -h --nafilter blob.csv
.importtable -c blobTable
SELECT * FROM blobTable ORDER BY 1;
.os rm -f blob.csv

.export csv -f3 blob.csv
SELECT * FROM blobTable ORDER BY 1;
.os cat blob.csv
DELETE FROM blobTable;
.import csv -h --nafilter -k3 blob.csv
.importtable -c blobTable
SELECT * FROM blobTable ORDER BY 1;
.os rm -f blob.csv

.export json blob.json
SELECT * FROM blobTable ORDER BY 1;
.os cat blob.json && echo
DELETE FROM blobTable;
.import json blob.json
INSERT INTO blobTable VALUES ({{a}}, {{b}}, {{c}});
SELECT * FROM blobTable ORDER BY 1;
.os rm -f blob.json

.export avro blob.avro
SELECT * FROM blobTable ORDER BY 1;
DELETE FROM blobTable;
.import avro blob.avro
.importtable -c blobTable
SELECT * FROM blobTable ORDER BY 1;
.os rm -f blob.avro

.export excel blob.xlsx
SELECT * FROM blobTable ORDER BY 1;
DELETE FROM blobTable;
.import excel -h blob.xlsx
.importtable -c blobTable
SELECT * FROM blobTable ORDER BY 1;
.os rm -f blob.xlsx

DELETE DATABASE vagrant;
DROP DATABASE vagrant;
