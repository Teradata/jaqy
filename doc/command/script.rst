.script
-------

``.script`` runs a script in languages such as JavaScript.

Syntax
~~~~~~

.. code-block:: text

	usage: .script [options] [file]
	options:
	  -c,--charset <arg>   specifies the file character set
	  -l,--lang <arg>      specifies the language
	  -t,--temporary       specifies the script engine is temporary

``-l`` specified the scripting language.  If it is not specified, the default
is JavaScript, which is already bundled with JRE since Java 6.

``-t`` specifies that the script engine is temporary.  A temporary script
engine is destroyed immediately after the use.  A non-temporary script engine
retains its state so that it can be re-used.

If the script file is not specified, the end of script is ``.end script`` on
a line by itself.

Example
~~~~~~~

.. code-block:: text

	-- test running a script from a file using a temporary script engine.
	.script -t lib/print.js

	-- test running a script using a non-temporary script engine
	.script
	function println (str)
	{
		display.println (interpreter, str);
	}

	println ("你好，世界");
	.end script

	-- re-use the script engine above to call the same println function created.
	.script
	println ("你好，世界");
	.end script

Implementation
~~~~~~~~~~~~~~

Jaqy uses
`Java Scripting API <https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/prog_guide/api.html>`__
to create a
`ScriptEngine <https://docs.oracle.com/javase/8/docs/api/javax/script/ScriptEngine.html>`__
.
