--------------------------------------------------------------------------
-- .expansion command test
--------------------------------------------------------------------------
.help expansion
.expansion
.expansion asdf

.run ../common/sqlite_setup.sql
.open sqlite::memory:

.script
var var1 = 'abc';
var var2 = 'def';
.end script

SELECT '${var1}' AS Test;
SELECT '${var2}' AS Test;
SELECT '${var1 + var2}' AS Test;

.expansion off

SELECT '${var1}' AS Test;
SELECT '${var2}' AS Test;
SELECT '${var1 + var2}' AS Test;

.expansion on

SELECT '${var1}' AS Test;
SELECT '${var2}' AS Test;
SELECT '${var1 + var2}' AS Test;

.close
