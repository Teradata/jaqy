--------------------------------------------------------------------------
-- .import excel test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.import excel -h https://github.com/coconut2015/jaqy/raw/master/tests/unittests/excel/data/file4.xlsx
.importtable -c MyTable

SELECT * FROM MyTable ORDER BY 1;

DROP TABLE MyTable;

