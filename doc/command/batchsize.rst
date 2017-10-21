.batchsize
----------

``.batchsize`` sets the data batch loading size.

By default, the batchsize is 5000.

Too large of batch size consumes too much memory, while too small of batch size
can significantly reduce the loading performance.  A good value is the one
that gets the most out of the loading performance without consuming a lot of
memory.

Depending on the row size (i.e. how big a single row of data to be transmitted
in bytes), 5000 is a fairly good value.  On extremely large rows, you may want
to reduce this value.

Syntax
~~~~~~

.. code-block:: text

	usage: .batchsize [size]

Without arguments, the command displays the current session's batch size.

If the batch size is 1, batch mode is disabled.

Example
~~~~~~~

.. code-block:: sql

	-- Disable batch mode
	.batchsize 1
	.import csv -h on batch.csv
	INSERT INTO MyTable VALUES (?, ?);


	-- Large batch
	.batchsize 10000
	.import csv -h on batch.csv
	INSERT INTO MyTable VALUES (?, ?);
