autocommit
----------

``autocommit`` setting sets the autocommit property of the queries.

This is a session setting.

The default value is JDBC driver dependent, although it is usually on.

Syntax
~~~~~~

.. code-block:: text

	usage: .set autocommit [on | off]

Executing the setting without arguments will display the autocommit setting
for the current session.

JDBC Call
~~~~~~~~~

It corresponds to JDBC
`Connection <https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html>`__
class's
`getAutoCommit() <https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html#getAutoCommit-->`__
and
`setAutoCommit(boolean autoCommit) <https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html#setAutoCommit-boolean->`__
functions.
