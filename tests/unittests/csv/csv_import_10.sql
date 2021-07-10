--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/sqlserver_setup.sql

.import csv -h lib/import6.csv
.importtable myTable

.desc myTable

SELECT * FROM myTable ORDER BY 1;
DROP TABLE myTable;

.quit

