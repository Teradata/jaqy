.exit
-----

``.exit`` exits Jaqy.

By default, ``.quit`` is aliased to ``.exit``.  So both can work.

When there are no more inputs for Jaqy, an exit command is called implicitly,
so it is not necessary to explicitly call this command.

Syntax
~~~~~~

.. code-block:: text

	usage: .exit [exit code]

The exit code is optional.  If it is not specified, it is generated based on
the errors encountered.  See `Exit Code <../exitcode.html>`__ for more
information.

.. note::

	``.exit`` command does not actively attempt to close a session.  This
	behavior is	intentional.

	It is to ensure to the script can be exited immediately, since sometimes
	closing a database connection may take a while due to the server status.
	One can always call `.close <close.html>`__ command before exiting
	the script to gracefully closing a database connection.
