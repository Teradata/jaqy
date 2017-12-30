.import
-------

This command loads data into the database.

Syntax
~~~~~~

.. code-block:: text

	usage: .import [type] [type options] [file]
	type:
	  avro
	  csv
	  json

	avro type options:

	csv type options:
	  -c,--charset <arg>                                    sets the file
	                                                        character set
	  -d,--delimiter <arg>                                  specifies the
	                                                        delimiter
	  -f,--nafilter                                         enables N/A value
	                                                        filtering
	  -h,--header <on | off>                                indicates the file has
	                                                        a header or not
	  -t,--type <default | excel | rfc4180 | mysql | tdf>   sets the csv type.
	  -v,--navalues <arg>                                   specifies a comma
	                                                        delimited list of N/A
	                                                        values.  If it is not
	                                                        specified and
	                                                        --nafilter is enabled,
	                                                        then the default list
	                                                        is used.

	json type options:
	  -a,--array                   treats BSON root document as array.
	  -b,--binary <base64 | hex>   sets the binary format.
	  -c,--charset <arg>           sets the file character set
	  -f,--format <text | bson>    sets the JSON format.
	  -p,--pretty <on | off>       turns pretty print on / off.


It should be noted that both CSV and AVRO supports field position and name
based loading, JSON only supports name based loading.

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
