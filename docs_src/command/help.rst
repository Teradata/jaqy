.help
-----

``.help`` turns on / off various debugging options.

Syntax
~~~~~~

.. code-block:: text

	usage:
		usage: .help [optional command]

Without the optional command, ``.help`` just lists the available commands
and their brief descriptions.

If the command is specified, then it displays detailed information for that
command.

Example
~~~~~~~

.. code-block:: sql

	-- List the available commands
	.help

	-- The dot before the command is optional
	.help .export
	.help export
