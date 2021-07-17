--------------------------------------------------------------------------
-- .script command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.script
var promptBGColor = "black";
var esc = display.getEscape ();
function colorPrompt ()
{
    return esc.bgColor (promptBGColor) + "---- bg: " + promptBGColor + esc.reset ();
}
.end script

.handler prompt colorPrompt ()

SELECT 1 AS Test;

.script
promptBGColor = "yellow";
.end script

SELECT 1 AS Test;

.script
promptBGColor = "white";
promptBold = true;
.end script

SELECT 1 AS Test;

.script
promptBGColor = "cyan";
promptBold = false;
.end script

SELECT 1 AS Test;

.script
promptBGColor = null;
promptBold = false;
.end script

SELECT 1 AS Test;

.script
promptBGColor = "dummy";
promptBold = false;
.end script

SELECT 1 AS Test;

.handler default

.close
