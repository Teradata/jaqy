.classpath
----------

``.classpath`` specifies the JDBC driver location for a protocol

The drivers are not loaded until they are actually being used.

Syntax
~~~~~~

.. code-block:: text

	usage: .classpath [protocol name] [classpath]

Without any arguments, it will just list the current driver locations.

Example
~~~~~~~

.. code-block:: text

	-- Sets the Teradata driver jar location on Windows
	.classpath teradata lib\tdgssconfig.jar;lib\terajdbc4.jar

	-- Sets the Teradata driver jar location on Linux
	.classpath teradata lib/tdgssconfig.jar:lib/terajdbc4.jar

	-- List classpaths for various protocols
	.classpath
