Load and Exporting Data
=======================

One of the main goals of Jaqy is to simplify importing and exporting various
data formats to and from the database.  In this tutorial, we are primarily
focused on CSV format.

Exporting CSV
-------------

The exporting of data from the database is illustrated in the following
example.

.. code-block::	sql

	.export csv myfile.csv
	SELECT *
	FROM MyTable
	ORDER BY a;

Here, `.export <command/export.html>`__ command is used to setup the
export.  `csv <export/csv.html>`__ option specifies that the output is
going to be in CSV format.  ``myfile.csv`` is the output file.  In the
interactive mode, the output file path is relative to the current directory.
In the script mode, the path is relative to the script that contains the
command.

Jaqy has an extensible framework for handling different types of paths.
With ``jaqy-s3`` plugin, it is also possible to export to S3 bucket.  The
current implementation of ``jaqy-s3`` writes to a local temporary file first
and then upload to the S3 bucket.  The following example is similar to the
above, just with the path changed to an S3 URI instead of a local file.

.. code-block::	sql

	.export csv s3://mybucket/myfile.csv
	SELECT *
	FROM MyTable
	ORDER BY a;

Export Binary and Character Data Files
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

CSV exporter allows the possibility of exporting certain columns of data
as separate files.  To facilitate the file name generation, a simple approach
of using
`printf format <https://en.wikipedia.org/wiki/Printf_format_string>`__
is used.

For example, the following exports column 2 and 3 into files named ``01.bin``,
``02.bin`` etc.  The encoding for character data is UTF-16LE.  The path
is relative to the csv file.

.. code-block::	sql

	.export csv -n %02d.bin -e UTF-16LE -f 2 -f 3 file1.csv
	SELECT * FROM MyTable ORDER BY a;

It is possible to have different naming patterns for each column.  The
counter in this case would not be shared.

For example, the following exports the 2nd column into files named ``0001.txt``,
``0002.txt`` etc, while the 3rd column gets exported into files named
``IMG0001.png``, ``IMG0002.png``, etc.

.. code-block::	sql

	.export csv -n %04d.txt -e UTF-8 -f2 -n IMG%04d.png -f3 file1.csv
	SELECT * FROM MyTable ORDER BY a;

Importing CSV
-------------

The following example shows a simple CSV data import.
`.import <command/import.html>`__ command is used to setup the data import.
For `csv <import/csv.html>`__ type, ``-h`` option specifies that the CSV
file has a header row.

.. code-block::	sql

	.import csv -h myfile.csv
	INSERT INTO MyTable VALUES (?, ?, ?);

The above syntax using ``?`` to do the import based on the position.  It is
possible to change the column order by using ``{{`` and ``}}`` to specify
the names.

.. code-block::	sql

	.import csv -h myfile.csv
	INSERT INTO MyTable VALUES ({{col1}}, {{col2}}, {{col3}});

Again, it is possible to access S3 bucket.  Unlike the exporting case, the
file can be directly imported.

.. code-block::	sql

	.import csv -h s3://mybucket/myfile.csv
	INSERT INTO MyTable VALUES ({{col1}}, {{col2}}, {{col3}});

You can also specify ``http`` or ``https`` URLs for data import.

.. code-block::	sql

	.import csv -h https://introcs.cs.princeton.edu/java/data/surnames.csv
	INSERT INTO MyTable VALUES ({{col1}}, {{col2}}, {{col3}});

Importing Binary and Character Data Files
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Much like the CSV export, it is possible to specify that values for certain
entries are the file names that contains the data for import.

In this example, the column 2 is treated as character data from a file, while
the column 3 is treated as binary data from a file.  The file names are
path relative to the csv file.

.. code-block::	sql

	.import csv -h -e UTF-16LE -j2 -k3 file1.csv
	INSERT INTO MyTable VALUES (?, ?, ?);


Automatic Schema Discovery
~~~~~~~~~~~~~~~~~~~~~~~~~~

Jaqy has a schema detection mechanism that allows SQL schema to be inferred
from the import.  You can use it to directly import the data without trying
to figure out of the schema yourself.  It is mostly for convenience, and not
guaranteed to work in all cases.

.. code-block::	sql

	.import csv -h -f https://introcs.cs.princeton.edu/java/data/surnames.csv
	.importtable surnames


Direct Inter-Database Data Transfer
-----------------------------------

Jaqy has a fairly unique feature of allowing direct inter-database data
transfer.  You can see from the example below.  Two sessions
(see `.session <command/session.html>`__) are used.  Then one session export
to `pipe <export/pipe.html>`__, while the other session import from
`pipe <import/pipe.html>`__.

.. code-block:: sql

	.session 0

	-- PostgreSQL large data set export settings

	.set autocommit off
	.set fetchsize 50
	.export pipe
	SELECT * FROM TextTable ORDER BY a;

	.session 1

	.import pipe
	.set batchsize 50
	INSERT INTO TextTable VALUES (?, ?, ?, ?);

The example here intentionally made
batch size and fetch size small to illustrate the functionality.  Usually,
one needs to consider the amount of memory for holding the ResultSet and
the amount of the memory needed to submit the data.
See `large data set handling <largedataset.html>`__.
