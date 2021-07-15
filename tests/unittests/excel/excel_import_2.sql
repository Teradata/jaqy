--------------------------------------------------------------------------
-- .import excel test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.import excel -h data/import2.xlsx
.importtable MyTable
SELECT * FROM MyTable ORDER BY c;
DROP TABLE MyTable;
