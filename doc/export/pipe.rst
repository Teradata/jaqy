pipe
^^^^

Pipe export / import is used to directly transfer data between databases.

For exporting a large data set, necessary precautions in
`Large Data Set Handling <../largedataset.html>`__ apply.

For importing data with LOB values, it may be necessary to change the
`batch size <command/batchsize.html>`__ to reduce the memory consumption.
However, very large LOB values can still cause memory exhaustion issues
for some JDBC drivers.

One advantage of using pipe export / import over other formats is the ability
to handle some database specific data types, if both the source and the target
databases are the same type.

For cross database type transfers, commonly used data types (INTEGER, VARCHAR,
VARBYTE, etc) should not be an issue.  More database specific types such as
Array, Struct are going to be problematic.

Example
*******

.. code-block:: sql

	.session 0

	-- PostgreSQL large data set export settings

	.autocommit off
	.fetchsize 50
	.export pipe
	SELECT * FROM TextTable ORDER BY a;

	.session 1

	.import pipe
	.batchsize 50
	INSERT INTO TextTable VALUES (?, ?, ?, ?);

See Also
********

* `pipe <../import/pipe.html>`__ import format.
