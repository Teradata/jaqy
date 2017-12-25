--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

.import csv -h lib/sin.csv
.importschema
.importtable SinTable
.desc -s SinTable

SELECT COUNT(*), min(x), max(x) FROM SinTable;
DROP TABLE SinTable;

.import csv -h -r 0 lib/sin.csv
.importschema
.importtable SinTable
.desc -s SinTable

SELECT COUNT(*), min(x), max(x) FROM SinTable;
DROP TABLE SinTable;

.import csv -h -r 12 lib/sin.csv
.importschema
.importtable SinTable
.desc -s SinTable

SELECT COUNT(*), min(x), max(x) FROM SinTable;
DROP TABLE SinTable;

.import csv lib/sin2.csv
.importschema
.importtable SinTable
.desc -s SinTable

SELECT COUNT(*), min(col1), max(col1) FROM SinTable;
DROP TABLE SinTable;

.import csv lib/empty.csv
.importschema

.import csv -h lib/numstr.csv
.importschema

.close
