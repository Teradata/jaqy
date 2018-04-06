.limit
------

``.limit`` limits the output to a number of rows.

The rows in the ResultSet after the limit is not read.  Thus, the activity
count reported with ``.limit`` on may not be accurate.

Syntax
~~~~~~

.. code-block:: text

	usage: .limit [number]


Example
~~~~~~~

.. code-block:: text

	-- Limit the output to 1 row
	.limit 1
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `.sort <sort.html>`__
