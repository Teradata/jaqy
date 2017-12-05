--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

.import csv -h on lib/import5.csv
.importschema
.importtable MyTable

SELECT * FROM MyTable ORDER BY a;
DROP TABLE MyTable;
.close
