.importtable
------------

``.importtable`` command creates a staging table that matches the schema
of the current import.

Since the schema generated is mostly a guess, it may fail sometimes due to
not being able to generate the right type information.

The first time when it runs, it needs to scan the entire data to obtain the
schema.

Currently, it only supports AVRO and CSV formats.

Syntax
~~~~~~

.. code-block:: text

	usage: .importtable [options] [tablename]
	options:
	  -c,--check   check if the table already exists

Example
~~~~~~~

.. code-block:: text

	-- set the current import
	.import csv -h on test.csv

	-- display the schema for the current import in column format
	.importschema

	-- create a table named MyTable and import data into it.
	.importtable MyTable


	-- check if MyTable exists.  If so, skip the table creation and just
	-- import data into the existing table.
	.importtable -c MyTable

See Also
~~~~~~~~

* `.import <import.html>`__
* `.importtable <importtable.html>`__
