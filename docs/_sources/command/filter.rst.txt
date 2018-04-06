.filter
-------

``.filter`` does client side ResultSet filtering using predicates.

If `.limit <limit.html>`__ is used, then the filtering is only applied to the
rows retrieved.

Since the rows are filtered when they are retrieved, it does not cost extra
memory.

The filtering predicates are removed after being used.

Syntax
~~~~~~

The syntax is basically similar to WHERE clause.

Besides the usual ``AND``, ``OR``, ``<``, ``<=``, ``=``, ``>=``, ``>``
operators.  The following operators are also supported.

* ``BETWEEN <num> AND <num>``
* ``< ANY ( ... )``, ``< SOME ( ... )``, ``< ALL ( ... )`` etc
* ``IN ( ... )``
* ``LIKE 'regex'``.  The regex here is in JavaScript syntax.

JavaScript function calls are supported as well.

Example
~~~~~~~

.. code-block:: text

	.filter a > 10 AND b < 100
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `.limit <limit.html>`__
* `.project <project.html>`__
* `.sort <sort.html>`__
