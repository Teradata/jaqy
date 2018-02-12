.fetchsize
----------

``.fetchsize`` sets the number of rows to be retrieved by the JDBC driver at
once.  This setting is useful for handling large data sets.

By default, the fetch size is 0, which uses the JDBC driver's default settings.

It should be noted that this setting may be ignored by the JDBC driver, or
having to be used in conjunction with other settings to be effective.

This setting is only active for the current connection.  The setting is
lost when the connection is closed.

Syntax
~~~~~~

.. code-block:: text

	usage: .fetchsize [size]

Without arguments, the command displays the current session's fetch size.

JDBC Call
~~~~~~~~~

It corresponds to JDBC
`Statement <https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html>`__
class's
`setFetchSize(int rows) <https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#setFetchSize-int->`__
function.

Example
~~~~~~~

.. code-block:: sql

	-- Exporting large amount of data in PostgreSQL
	.autocommit off
	.fetchsize 10
	.export csv largedataset.csv
	SELECT * FROM MyBigTable;

	-- restore settings
	.autocommit on
	.fetchsize 0
