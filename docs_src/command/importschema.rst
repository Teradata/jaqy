.importschema
-------------

``.importschema`` command attempts to display the schema of the current
import.

It should be noted that the schema generated is mostly a guess.

The first time when it runs, it needs to scan the entire data to obtain the
schema.

Syntax
~~~~~~

.. code-block:: text

	usage: .importschema [options]
	options:
	  -s,--sql   display schema in SQL

Example
~~~~~~~

.. code-block:: text

	-- set the current import
	.import csv -h on test.csv

	-- display the schema for the current import in column format
	.importschema

	-- display the schema for the current import in SQL format
	.importschema -s

See Also
~~~~~~~~

* `.import <import.html>`__
* `.importtable <importtable.html>`__
