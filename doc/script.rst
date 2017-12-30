Scripting
=========

Jaqy can be extended in various ways.  And one way to add new functionalities
is by using its scripting support.  Jaqy uses
`Java Scripting API <https://docs.oracle.com/javase/6/docs/technotes/guides/scripting/programmer_guide/>`__
to bind scripting engines.  Since a JavaScript engine is shipped with JDK 6 and
later, Jaqy uses it as the default script engine.

Variables
---------

The variables can be listed using `.var <command/var.html>`__ command.  The
following variables are set up by default.

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
| interpreter   | `JaqyInterpreter <https://github.com/Teradata/jaqy/blob/master/jaqy-core/src/main/java/com/teradata/jaqy/JaqyInterpreter.java>`__     |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| parent        | Parent variables                                                                                                                      |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+
| session       | Current `Session <https://github.com/Teradata/jaqy/blob/master/jaqy-core/src/main/java/com/teradata/jaqy/Session.java>`__             |
+---------------+---------------------------------------------------------------------------------------------------------------------------------------+

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

