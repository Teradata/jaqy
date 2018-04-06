.debug
------

``.debug`` turns on / off various debugging options.

Syntax
~~~~~~

.. code-block:: text

	usage:
		.debug log [info | warning | all | off]
		.debug preparedstatement [on | off]
		.debug resultset [on | off]

log
^^^

Turns Jaqy debug logging on / off.

preparedstatement
^^^^^^^^^^^^^^^^^

Turns on / off parameterized SQL parameter type information display.

resultset
^^^^^^^^^

Turns on / off SQL query result type information display.

Example
~~~~~~~

.. code-block:: sql

	.debug resultset on
	SELECT 1;

	.debug preparedstatement on
	.prepare
	INSERT INTO MyTable VALUES (?, ?);


See Also
~~~~~~~~

* `.import <import.html>`__
* `.prepare <prepare.html>`__
