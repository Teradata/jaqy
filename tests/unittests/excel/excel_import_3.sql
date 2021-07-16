--------------------------------------------------------------------------
-- .import excel test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.import excel data/empty.xlsx
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY 1;
DROP TABLE MyTable;

.import excel -h data/badheader.xlsx
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY 1;
DROP TABLE MyTable;

.import excel -h data/nofirstcol.xlsx
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY 1;
DROP TABLE MyTable;

.import excel data/nofirstcol.xlsx
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY 1;
DROP TABLE MyTable;

.import excel data/nofirstrow.xlsx
.importtable -c MyTable
SELECT * FROM MyTable ORDER BY 1;
DROP TABLE MyTable;

