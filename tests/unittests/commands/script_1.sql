--------------------------------------------------------------------------
-- .script command test
--------------------------------------------------------------------------
.help script

.script lib/print.js
.script -c utf-8 lib/print.js

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
