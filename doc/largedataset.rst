Large Data Set Handling
=======================

When selecting large amount of data out, or getting results with large LOB
values, it is very possible to run out of memory.  This section details the
approaches to handle such large query results.

General Guidelines
------------------

Some drivers such SQLite JDBC driver requires the data to be in the memory,
so there is not much can be done.  For PostgreSQL, it does allow incremental
retrieval of a large query result, if you follow the guidelines at
`Issuing a Query and Processing the Result <https://jdbc.postgresql.org/documentation/head/query.html>`__
.

In general, the following commands may be necessary.

* `.autocommit <command/autocommit.html>`__ off
* `.fetchsize <command/fetchsize.html>`__ [small size]

Displaying Large Data Set
-------------------------

When displaying a large query result, avoid using `table <format/table.html>`__
format.  This is because table format retrieves data by using
`TYPE_SCROLL_INSENSITIVE <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#TYPE_SCROLL_INSENSITIVE>`__
ResultSet.  For some JDBC drivers, this type of ResultSet is handled by
loading all the data into the memory, which obviously makes handling large
data set problematic.  Furthermore, table format may make a copy of rows
scanned if the ResultSet is
`TYPE_FORWARD_ONLY <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#TYPE_FORWARD_ONLY>`__
.  Thus, table format is not
suitable for displaying large data sets.

For `csv <format/csv.html>`__ and `json <format/json.html>`__ formats, they
have no such problems since they use
`TYPE_FORWARD_ONLY <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#TYPE_FORWARD_ONLY>`__
ResultSet.

Exporting Large Data Set
------------------------

Jaqy uses
`TYPE_FORWARD_ONLY <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#TYPE_FORWARD_ONLY>`__
ResultSet to retrieve the data, thus it is not a factor.

However, depending on the export type and the presence of CLOB / BLOB / XML
types, it may still be an issue.  Exporting in csv format with external file
for these large data type columns is the best option in these cases.

Example
-------

.. code-block:: sql

	-- Exporting large amount of data in PostgreSQL
	.autocommit off
	.fetchsize 10
	.export csv largedataset.csv
	SELECT * FROM MyBigTable;
