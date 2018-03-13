.import
-------

This command loads data into the database.

Syntax
~~~~~~

.. code-block:: text

	usage: .import [type] [type options] [path]

The followings are the currently supported import types.

* `avro <../import/avro.html>`__
* `csv <../import/csv.html>`__
* `json <../import/json.html>`__
* `pipe <../import/pipe.html>`__

It should be noted that csv, avro and pipe support field position and name
based loading, but json only supports name based loading.

See `Import Formats <../import.html>`__ for more information.

Loading
~~~~~~~

There are two possible ways of loading data.  One is position based, and the
other is name based.

Let us use the following CSV as an example.

.. code-block:: text

	Name,Value
	1,2

Position Based Loading
^^^^^^^^^^^^^^^^^^^^^^

With positioned loading, each ``?`` in SQL is used to count the input field
positions.

So for the following example, the first ``?`` corresponds to column ``Name``
in the CSV file, and the second ``?`` corresponds to column ``Value``.

.. code-block:: sql

	CREATE TABLE MyTable (a INTEGER, b INTEGER);
	.import csv -h on lib/import1.csv
	INSERT INTO MyTable VALUES (?, ?);

	-- Check the data
	SELECT * FROM MyTable ORDER BY a;
	-- success --
	A B
	- -
	1 2

Name Based Loading
^^^^^^^^^^^^^^^^^^

With name based loading, source field name is put inside ``{{`` and ``}}``
to specify the source field name.

.. code-block:: sql

	.import csv -h on lib/import1.csv
	INSERT INTO MyTable VALUES ({{Name}}, {{Value}});

	-- Check the data
	SELECT * FROM MyTable ORDER BY a;
	-- success --
	A B
	- -
	1 2

Batch Loading
^^^^^^^^^^^^^

Jaqy by default, uses batch loading which can improve the loading speed.
See `.batchsize <batchsize.html>`__ for more information.


Example
~~~~~~~

.. code-block:: sql

	-- Loading data using field position
	.import csv -h on lib/import1.csv
	INSERT INTO MyTable VALUES (?, ?);

	-- Loading data using names
	.import csv -h on lib/import1.csv
	INSERT INTO MyTable VALUES ({{a}}, {{b}});
