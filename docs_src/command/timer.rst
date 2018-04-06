.timer
------

``.timer`` sets a timer which is useful to do some simple performance tests.

Syntax
~~~~~~

.. code-block:: text

	usage: .timer [set]

To use the timer, ``.timer set`` must be called first to initialize the
timer.  Subsequent calls to ``.timer`` without arguments would report
the time elapsed since then.

.. note::

	This command creates a variable ``timer``.

Example
~~~~~~~

.. code-block:: sql

	-- starts the timer
	.timer set

	-- do some work
	.import csv -h on batch.csv
	INSERT INTO MyTable VALUES (?, ?);

	-- report the time elapsed since .timer set
	.timer

	-- do some work
	SELECT COUNT(*) FROM MyTable;

	-- report the time elapsed since .timer set
	.timer
