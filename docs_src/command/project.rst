.project
--------

``.project`` does client side ResultSet projection.

If `.limit <limit.html>`__ is used, then the projection is only applied to the
rows retrieved.

Since the columns are projected when they are retrieved, it does not cost extra
memory.

Syntax
~~~~~~

The syntax is basically similar to SELECT clause, but only simple column
names and ``AS`` clause is supported.

It should be noted that ``"`` is always used to quote column names.

Sometimes it is easier to refer a column by its index (starting from 1).
One can use ``@1`` instead.

Example
~~~~~~~

.. code-block:: text

	.project a AS "Column 1", b
	SELECT * FROM MyTable ORDER BY a;

	.project @1 AS "Column 1", @2
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `.limit <limit.html>`__
* `.project <project.html>`__
* `.sort <sort.html>`__
