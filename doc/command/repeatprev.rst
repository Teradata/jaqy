.#
--

``.#`` repeats a previous SQL a number of times.

This command is primarily designed for developers to resend a previous SQL
to the database.

Syntax
~~~~~~

.. code-block:: text

	usage: .# [number]

You can use the shorthand syntax like the following.

.. code-block:: text

	.5

which is the same as

.. code-block:: text

	.# 5


Example
~~~~~~~

.. code-block:: sql

	INSERT INTO MyTable
	SELECT a + (SELECT MAX(a) FROM MyTable),
	       'POINT(' || (a + (SELECT MAX(a) FROM MyTable)) || ' ' || (a + (SELECT MAX(a) FROM MyTable)) || ')'
	FROM MyTable;

	-- Looks like that we need more data.
	.4

See Also
~~~~~~~~

* `.repeat <repeat.html>`__ - which repeats a following SQL a number of times.
