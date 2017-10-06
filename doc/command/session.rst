.session
---------

``.session`` creates a new session or switches to a session.

In Jaqy, each session can only have one active database connection.  Thus,
it is necessary to have multiple sessions to open multiple database
connections.

Syntax
~~~~~~

.. code-block:: text

	usage: .session [new | session id]

Without arguments, the command will list the current sessions.

Example
~~~~~~~

.. code-block:: sql

	-- creates a new session
	.session new

	-- list the sessions
	.session

	-- switch to the new session
	.session 1
