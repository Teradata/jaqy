.protocol
---------

``.protocol`` sets the JDBC driver for a particular protocol.

Syntax
~~~~~~

.. code-block:: text

	usage: .protocol [protocol name] [JDBC driver name]

Without any arguments, it will just list the current protocol / driver
mappings.


Example
~~~~~~~

.. code-block:: sql

	-- Set postgresql Driver class name
	.protocol postgresql org.postgresql.Driver 

	-- List current protocol / driver mappings.
	.protocol
