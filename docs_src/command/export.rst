.export
-------

This command exports data from the database.

Syntax
~~~~~~

.. code-block:: text

	usage .export [type] [type options] [path]

The followings are the currently supported export types.

* `avro <../export/avro.html>`__
* `csv <../export/csv.html>`__
* `json <../export/json.html>`__
* `pipe <../export/pipe.html>`__

Example
~~~~~~~

.. code-block:: sql

	.export csv -c utf-8 file1.csv
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `.import <import.html>`__
