.quiet
------

``.quiet`` turns ResultSet output on / off.

This command is useful to hide display of results that may not be important.

Syntax
~~~~~~

.. code-block:: text

	usage: .quiet [on | off]

Example
~~~~~~~

.. code-block:: sql

	.quiet on
	-- 2/27 - 0 - root@localhost @ localhost --
	SELECT 1234;
	-- success --
	-- activity count = 1
	-- 3/27 - 0 - root@localhost @ localhost --
	.quiet off
	-- 3/28 - 0 - root@localhost @ localhost --
	SELECT 1234;
	-- success --
	1234
	----
	1234
	-- activity count = 1
	-- 4/28 - 0 - root@localhost @ localhost --
