.list
-----

``.list`` command list catalogs, schemas, and tables in the database.

Not all databases have catalogs or schema.  And if they do, they may be under
different terms.  Use the following command to find out the given database's
behavior.

.. code-block:: text

	.info behavior

Syntax
~~~~~~

.. code-block:: text

	usage: .list [catalog] [schema] [table]

Example
~~~~~~~

.. code-block:: text

	-- List tables in the current catalog
	.list
	-- List all the catalogs
	.list %
	-- List all the schemas in the current catalog
	.list . %
	-- same as .list
	.list . . %
	-- List all the schemas and tables (not all databases support it)
	.list . % %
