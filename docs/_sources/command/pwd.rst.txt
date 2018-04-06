.pwd
----

``.pwd`` displays the current catalog and schema.

JDBC Call
~~~~~~~~~

Without database extension, the command calls to JDBC
`Connection <https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html>`__
class's
`getCatalog() <https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html#getCatalog-->`__
and
`getSchema() <https://docs.oracle.com/javase/8/docs/api/java/sql/Connection.html#getSchema-->`__
functions.

Database Extension
~~~~~~~~~~~~~~~~~~

Some databases may choose not to implement ``getCatalog()`` and ``getSchema()``
functions due to the difficulties of reliably tracking the current catalog /
schema.  That do not mean they do not have SQL for reporting the current
catalog or schema.  See `database configurations <../config/database.html>`__ to learn more.

Example
~~~~~~~

.. code-block:: sql

	.open -u root mysql://localhost/
	-- 0/9 - 0 - root@localhost @ localhost --
	.pwd
	database : null
	-- 0/10 - 0 - root@localhost @ localhost --
	USE vagrant;
	-- success. update count = 0
	-- 1/10 - 0 - root@localhost @ localhost --
	.pwd
	database : vagrant
	-- 1/11 - 0 - root@localhost @ localhost --
