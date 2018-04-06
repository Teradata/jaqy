.desc
-----

``.desc`` displays the schema for a table.

Syntax
~~~~~~

.. code-block:: text

	usage: usage: .desc [options] [table name]
	options:
	  -s,--sql   display schema in SQL

Example
~~~~~~~

The following example was tested done on PostgreSQL.

.. code-block:: sql

	create table mytable ( a int, b int );
	-- success. update count = 0
	-- 1/2 - 0 - postgres @ localhost - public --
	.desc mytable
	Column Type Nullable
	------ ---- --------
	a      int4 Yes
	b      int4 Yes
	-- 1/3 - 0 - postgres @ localhost - public --
	.desc -s mytable
	CREATE TABLE mytable (
	    a int4,
	    b int4
	)
	-- 1/4 - 0 - postgres @ localhost - public --

Generic Handling
~~~~~~~~~~~~~~~~

The generic approach used by Jaqy basically uses the following SQL to generate
the column list.  And from the column list, infer the table schema.

.. code-block::	sql

	SELECT * FROM TABLENAME WHERE 1=0

Obviously, it will not contain information such as indexes, triggers,
constraints, etc.  Sometimes, the table column type may not be exact, due to
the limitations in JDBC capabilities providing the type information.

**Known issues** XML type in Apache Derby cannot be directly selected out using
the approach above.  The command will fail as the result.  TBH, it is Apache
Derby's design issue.

Database Extension
~~~~~~~~~~~~~~~~~~

This command can be configured using
`database configurations <../config/database.html>`__ to
use database specific SQL rather than the generic approach used.  For instance,
Teradata uses ``HELP TABLE <tableName>`` and ``SHOW TABLE <tableName>`` to get
the column information, and the table schema, respectively.

By default, MySQL, Teradata, SQLite have been configured to use the native
approach to display the table columns and schema.
