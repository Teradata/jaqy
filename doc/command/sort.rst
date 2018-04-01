.sort
-----

``.sort`` does client side ResultSet sorting.

If `.limit <limit.html>`__ is used, then sorting is only applied to the rows
retrieved.

The sorting configuration is removed after the SQL query.

Syntax
~~~~~~

The syntax is basically similar to ORDER BY clause.  You can specify the
column either by its column position, or by its name.

Example
~~~~~~~

.. code-block:: text

	-- do descending sort on the 2nd column, then ascending sort on the 1st column 
	.sort 2 DESC, 1 ASC
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `.filter <filter.html>`__
* `.limit <limit.html>`__
* `.project <project.html>`__
