Client Side ResultSet Handling
==============================

There are times when certain server DDL produces a result that does not
offer the ability to sort, filer, or selective projection.  In these cases,
client side ResultSet handling can come in handy.

Client-Side Filtering
---------------------

Jaqy offers client-side ResultSet filtering using
`.filter <command/filter.html>`__ command.  Its syntax is very similar to that
of ``WHERE`` clause in SQL.

.. code-block:: text

	.filter a > 10 AND b < 100
	SELECT * FROM MyTable ORDER BY a;

It is possible to use JavaScript or Java functions as well.

.. code-block:: text

	.eval var myfunc = function(a,b) { return a + b; }
	.filter a > myfunc(3,4) and a < 10
	SELECT * FROM MyTable ORDER BY a;

Client-Side Sorting
-------------------

Client-side ResultSet sorting can be done using `.sort <command/sort.html>`__
command.  The syntax is very similar the ``ORDER BY`` syntax in SQL.

.. code-block:: text

	-- do descending sort on the 2nd column, then ascending sort on the 1st column 
	.sort 2 DESC, 1 ASC
	SELECT * FROM MyTable;

	-- sort by column b
	.sort b DESC
	SELECT * FROM MyTable;

Client-Side Projection
----------------------

Client-side projection is used to pick or rename a few columns from the
ResultSet.

.. code-block:: sql

	.project a AS "Column 1", b
	SELECT * FROM MyTable ORDER BY a;

It is possible to combine all three forms of client-side ResultSet processing
together to do some silly things.
