.sort
-----

``.sort`` does client side ResultSet sorting.

If `.limit <limit.html>`__ is used, then sorting is only applied to the rows
retrieved, not all of the ResultSet.

The sorting configuration is removed after the SQL query.

Syntax
~~~~~~

.. code-block:: text

	usage: .sort [options]
	options:
	  -a,--ascending <arg>    ascending sort
	  -d,--descending <arg>   descending sort
	  -h,--high               null sorts high
	  -l,--low                null sorts low


Example
~~~~~~~

.. code-block:: text

	-- do descending sort on the 2nd column, then ascending sort on the 1st column 
	.sort -d 2 -a 1
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `.limit <limit.html>`__
