--------------------------------------------------------------------------
-- .script command test
--------------------------------------------------------------------------
.run ../common/sqlite_setup.sql
.open sqlite::memory:

.script
var promptFGColor = "red";
var promptBGColor = "black";
var promptBold = false;
var esc = display.getEscape ();
function colorPrompt ()
{
    return esc.color (promptFGColor, promptBGColor, promptBold) + "---- fg: " + promptFGColor + ", bg: " + promptBGColor + ", bold: " + promptBold + esc.reset ();
}
.end script

.handler prompt colorPrompt ()

SELECT 1 AS Test;

.script
promptFGColor = "green";
promptBGColor = "yellow";
.end script

SELECT 1 AS Test;

.script
promptFGColor = "blue";
promptBGColor = "white";
promptBold = true;
.end script

SELECT 1 AS Test;

.script
promptFGColor = "purple";
promptBGColor = "cyan";
promptBold = false;
.end script

SELECT 1 AS Test;

.script
promptFGColor = "dummy";
promptBGColor = "dummy";
promptBold = false;
.end script

SELECT 1 AS Test;

.handler default

.close
