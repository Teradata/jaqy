--------------------------------------------------------------------------
-- .import csv command test
--------------------------------------------------------------------------
.run ../common/postgresql_setup.sql

.import csv -h -f https://introcs.cs.princeton.edu/java/data/ip-by-country.csv
.importschema
.importtable mytable

SELECT COUNT(*) FROM mytable;
.limit 5
SELECT * FROM mytable ORDER BY 1;
.limit 0

DROP TABLE mytable;

.close
