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

.close
