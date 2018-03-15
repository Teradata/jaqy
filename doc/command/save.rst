.save
-----

``.save`` is used to make an in-memory copy of the current resultset being
printed to variable named 'save'.

Example
~~~~~~~

.. code-block:: text

	-- save a resultset to variable save
	.save
	SELECT * FROM MyTable ORDER BY a;

	-- now print the resultset stored in variable save
	.eval interpreter.print (save)

	-- use with repeat command
	.repeat ${save.size()}
	SELECT '${save.get (iteration)[1]}' AS geo;
