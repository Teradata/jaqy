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

	usage: .importtable [tablename]

Example
~~~~~~~

.. code-block:: text

	-- set the current import
	.import csv -h on test.csv

	-- display the schema for the current import in column format
	.importschema

	-- create a table named MyTable and import data into it.
	.importtable MyTable

See Also
~~~~~~~~

* `.import <import.html>`__
* `.importtable <importtable.html>`__
