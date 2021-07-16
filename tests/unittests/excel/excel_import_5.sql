--------------------------------------------------------------------------
-- .import excel test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.import excel -h data/formula.xlsx
.importtable -c MyTable

SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;

