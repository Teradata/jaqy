--------------------------------------------------------------------------
-- .script command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

CREATE TABLE MyTable
(
	a INTEGER,
	b INTEGER
);

.script
function println (str)
{
	display.println (interpreter, str);
}

var schema = interpreter.getQueryString ("SELECT sql FROM SQLITE_MASTER WHERE NAME = 'MyTable' COLLATE NOCASE", 1);

println ("schema = " + schema);

var var1 = 'abc';
println ("var1 " + var1);

var var2 = 'xxx';
println ("var2 " + var2);

.end script

SELECT '${schema}' AS Test;
SELECT '${var1 + var2}' AS Test;

DROP TABLE MyTable;

.close
