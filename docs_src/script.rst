Scripting
=========

Since JDK 6 and later comes with a JavaScript engine, Jaqy uses it to provide
scripting support.

Each
`JaqyInterpreter <https://github.com/Teradata/jaqy/blob/master/jaqy-core/src/main/java/com/teradata/jaqy/JaqyInterpreter.java>`__
has a JavaScript engine.  Since Jaqy currently only uses a JaqyInterpreter,
there is effectively just one script engine.

Commands
--------

* `.eval <command/eval.html>`__ is used to execute a one-liner JavaScript
* `.script <command/script.html>`__ is used to execute a block of JavaScript
  from a file.

Variables
---------

The following variables are set up by default.  These variables are all in the
engine scope.

+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| Variable      | Description                                                                                                                           |
+===============+=======================================================================================================================================+
| activityCount | The number of rows in the SQL query or SQL update.                                                                                    |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| display       | `Display <https://github.com/Teradata/jaqy/blob/master/jaqy-console/src/main/java/com/teradata/jaqy/ConsoleDisplay.java>`__           |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| esc           | `Escape <https://github.com/Teradata/jaqy/blob/master/jaqy-console/src/main/java/com/teradata/jaqy/utils/Escape.java>`__              |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| globals       | `Globals <https://github.com/Teradata/jaqy/blob/master/jaqy-core/src/main/java/com/teradata/jaqy/Globals.java>`__                     |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| iteration     | The current repeat iteration.                                                                                                         |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| interpreter   | `JaqyInterpreter <https://github.com/Teradata/jaqy/blob/master/jaqy-core/src/main/java/com/teradata/jaqy/JaqyInterpreter.java>`__     |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| parent        | Parent variables (i.e. variables in the global scope)                                                                                 |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| session       | Current `Session <https://github.com/Teradata/jaqy/blob/master/jaqy-core/src/main/java/com/teradata/jaqy/Session.java>`__             |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+

Expression Expansion
--------------------

By default, Jaqy does expression expansion for all SQL statements.
The general syntax is ``${`` `expression` ``}``.  It should be noted that
Jaqy does an extremely simple regular expression match by looking for the
pair of tokens, and evaluate the text in between using JavaScript.

The expression expansion can be disabled by using
`.expansion <command/expansion.html>`__ command.

.. note::

	The reason to use ``${`` and ``}`` pair instead of ``$(`` and ``)`` pair
	as in bash arithmetic expansion is simply because it is possible to allow
	functions to be called without complex parsing.

	Jaqy does use ``$(`` and ``)`` pair
	when dealing with `.alias <command/alias.html>`__ command arguments.
	So comparing to bash's parameter expansion, the usage of the tokens are
	somewhat switched.

Expression Expansion Example 1
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

.. code-block:: sql

	CREATE TABLE MyTable (a INTEGER, b INTEGER);
	CREATE TABLE ControlTable (a INTEGER, b INTEGER);

	INSERT INTO MyTable VALUES (1, 1);
	INSERT INTO MyTable VALUES (2, 2);

	SELECT * FROM MyTable;

	-- Variable Substitution
	INSERT INTO ControlTable VALUES (1, ${activityCount});

	SELECT * FROM ControlTable;

The result should be the following.

.. code-block:: text

	a b
	- -
	1 2

Expression Expansion Example 2
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The expression expansion evaluation ignores quotes etc.  Such behavior is
intentional, since it allows custom string values to be generated.

.. code-block:: sql

	CREATE TABLE geoTable (a INTEGER, geo VARCHAR(100));

	.script
	var rad = 1.1234;
	var x = Math.sin (rad);
	.end script

	INSERT INTO geoTable VALUES (1, 'POINT(${x} ${Math.cos(rad)})');

	SELECT * FROM geoTable;

The result should be the following.

.. code-block:: text

	a geo
	- --------------------------------------------
	1 POINT(0.901576557064293 0.43261959242745307)

JavaScript Tips
---------------

Calling Java Functions
~~~~~~~~~~~~~~~~~~~~~~

It is possible to call Java functions in JavaScript by using `Packages`
variable.

.. code-block:: javascript

	.script
	Packages.java.lang.System.out.println ( 'test' )
	.end script

Implementing Java Interface
~~~~~~~~~~~~~~~~~~~~~~~~~~~

It is entirely possible to write plugins for Jaqy purely using JavaScript.
Of course, its performance is not going to be that great.  However, if you
needed something done quickly, it could be an option.

.. code-block:: javascript

	.script
	{
		var myCommand = new Packages.com.teradata.jaqy.command.JaqyCommandAdapter (
		{
			getDescription : function ()
			{
				return "my dummy command";
			},
			execute : function (args, silent, globals, interpreter)
			{
				interpreter.println ("my dummy command called.");
			}
		});
		globals.getCommandManager ().addCommand ("mycmd", myCommand);
	}
	.end script
	-- now test the new .mycmd command
	.mycmd

