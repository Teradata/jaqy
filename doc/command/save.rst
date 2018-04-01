.save
-----

``.save`` is used to make an in-memory copy of the current ResultSet being
printed to a variable named 'save'.

Example
~~~~~~~

.. code-block:: text

	-- list tables in the current schema and save it
	.project TABLE_CAT, TABLE_NAME
	.save
	.list

	-- drop these tables
	.repeat ${save.size()}
	DROP TABLE `${save.get (iteration, 1)}`.`${save.get (iteration, 2)}`;
