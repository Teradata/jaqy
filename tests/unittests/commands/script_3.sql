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

INSERT INTO MyTable VALUES (1, 2);
INSERT INTO MyTable VALUES (3, 4);

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

.script
var val3 = interpreter.getResultSet ("SELECT * FROM MyTable").get (2, 2);
.end script

SELECT '${val3}' AS Test;

DROP TABLE MyTable;

.close
