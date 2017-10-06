.autocommit
-----------

``.autocommit`` sets the autocommit property of the queries.

Syntax
~~~~~~

.. code-block:: text

	usage: .autocommit [on | off]

Executing the command without arguments will display the autocommit setting
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
