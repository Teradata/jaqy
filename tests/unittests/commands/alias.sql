--------------------------------------------------------------------------
-- .alias command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.help alias
.alias

.format csv
.open sqlite::memory:

CREATE TABLE MyTable (a INTEGER, b TEXT);

-- creates an alias that insert two rows
-- because comma is part of the arguments, we need to be careful
.alias ins
.version
INSERT INTO $0 VALUES (${1-});
INSERT INTO $0 VALUES (1 + ${1} ${2});
.end alias

.ins MyTable 0, NULL
.ins MyTable 2, 'POINT(EMPTY)'
.ins MyTable 4, 'POINT(1 1)'
.ins MyTable 6, 'LINESTRING(1 1, 3 3)'
.ins MyTable 8, 'This is a test.'

SELECT * FROM MyTable ORDER BY a;

.quit
