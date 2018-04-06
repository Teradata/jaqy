.script
-------

``.script`` runs a script in languages such as JavaScript.

Syntax
~~~~~~

.. code-block:: text

	usage: .script [options] [path]
	options:
	  -c,--charset <arg>   specifies the file character set

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

See Also
~~~~~~~~

* `.eval <eval.html>`__ command
* `Scripting <../script.html>`__
