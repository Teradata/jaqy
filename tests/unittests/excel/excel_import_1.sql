--------------------------------------------------------------------------
-- .import excel test
--------------------------------------------------------------------------
.run ../common/sqlserver_setup.sql

.import excel
.import excel asdfasdf.xlsx
.import excel -n asdf -i 1234 data/import1.xlsx
.import excel -i -1234 data/import1.xlsx
.import excel -d -1 data/import1.xlsx

.import excel data/import1.xlsx
.import
.importschema
.importtable -c MyTable

SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;

.import excel -i 0 data/import1.xlsx
.import
.importtable -c MyTable

SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;

