--------------------------------------------------------------------------
-- .import excel test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.import excel -h -n "sheet 1234" data/file4.xlsx
.import excel -h -i 1234 data/file4.xlsx

.import excel -h -n "sheet 1" data/file4.xlsx
.importtable -c MyTable

SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;

.import excel -h -i 0 data/file4.xlsx
.importtable -c MyTable

SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;
