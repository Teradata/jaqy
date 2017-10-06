.run
----

``.run`` executes a script.  It also sets up the current ``.run`` script
directory such that subsequent commands such as ``.exec``, ``.run``, etc 
are all relative to this directory.

Syntax
~~~~~~

.. code-block:: text

	usage: .run [options] [file]
	options:
	  -c,--charset <arg>   sets the file character set

Example
~~~~~~~

.. code-block:: sql

	-- Run lib/test.sql and sets the current script directory to lib/
	.run lib/test.sql
