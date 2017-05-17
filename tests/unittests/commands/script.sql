--------------------------------------------------------------------------
-- .script command test
--------------------------------------------------------------------------
.help script

-- test running a script from a file
.script -f -t lib/print.js
-- test running a script from a file, with language specified.
.script -l t javascript -f lib/print.js

-- test running a script inline
.script
function println (str)
{
	display.println (interpreter, str);
}

println ("你好，世界");
.end script

-- since the previous script has created a println function, use it again.
.script
println ("你好，世界");
.end script
