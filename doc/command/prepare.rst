.prepare
--------

This command is mostly for checking the parameter types required.  It works
in conjunction with ``.debug preparedstatement on``

Syntax
~~~~~~

.. code-block:: text

	usage: .prepare

Example
~~~~~~~

.. code-block:: sql

	.debug preparedstatement on
	.prepare
	INSERT INTO MyTable VALUES (?, ?);


See Also
~~~~~~~~

* `.debug <debug.html>`__
