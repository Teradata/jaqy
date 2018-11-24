--------------------------------------------------------------------------
-- expansion setting test
--------------------------------------------------------------------------
.set expansion
.set expansion asdf

.run ../common/sqlite_setup.sql
.open sqlite::memory:

.script
var var1 = 'abc';
var var2 = 'def';
.end script

SELECT '${var1}' AS Test;
SELECT '${var2}' AS Test;
SELECT '${var1 + var2}' AS Test;

.set expansion off
.set expansion

SELECT '${var1}' AS Test;
SELECT '${var2}' AS Test;
SELECT '${var1 + var2}' AS Test;

.set expansion on
.set expansion

SELECT '${var1}' AS Test;
SELECT '${var2}' AS Test;
SELECT '${var1 + var2}' AS Test;

.close
