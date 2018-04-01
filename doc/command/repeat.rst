.repeat
-------

``.repeat`` repeats a following SQL a number of times.

This command is very useful in populating a database with a lot of data.
With `scalar subquery <https://en.wikipedia.org/wiki/Correlated_subquery>`__,
you can avoid having duplicate rows.

Syntax
~~~~~~

.. code-block:: text

	usage: .repeat [number]

Example
~~~~~~~

.. code-block:: sql

	-- Generate a bunch of spatial points on a line

	CREATE TABLE MyTable (a INTEGER, b VARCHAR(10000));

	INSERT INTO MyTable VALUES (1, 'POINT(1 1)');

	.repeat 5
	INSERT INTO MyTable
	SELECT a + (SELECT MAX(a) FROM MyTable),
	       'POINT(' || (a + (SELECT MAX(a) FROM MyTable)) || ' ' || (a + (SELECT MAX(a) FROM MyTable)) || ')'
	FROM MyTable;

And the result is the following.

.. code-block:: sql

	SELECT * FROM MyTable ORDER BY a;
	-- success --
	 a b
	-- ------------
	 1 POINT(1 1)
	 2 POINT(2 2)
	 3 POINT(3 3)
	 4 POINT(4 4)
	 5 POINT(5 5)
	 6 POINT(6 6)
	 7 POINT(7 7)
	 8 POINT(8 8)
	 9 POINT(9 9)
	10 POINT(10 10)
	11 POINT(11 11)
	12 POINT(12 12)
	13 POINT(13 13)
	14 POINT(14 14)
	15 POINT(15 15)
	16 POINT(16 16)
	17 POINT(17 17)
	18 POINT(18 18)
	19 POINT(19 19)
	20 POINT(20 20)
	21 POINT(21 21)
	22 POINT(22 22)
	23 POINT(23 23)
	24 POINT(24 24)
	25 POINT(25 25)
	26 POINT(26 26)
	27 POINT(27 27)
	28 POINT(28 28)
	29 POINT(29 29)
	30 POINT(30 30)
	31 POINT(31 31)
	32 POINT(32 32)
	-- activity count = 32


See Also
~~~~~~~~

* `.# <repeatprev.html>`__ - which repeats a previous SQL a number of times.
