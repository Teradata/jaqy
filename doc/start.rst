Getting Started
===============

Jaqy is a Java based console application.  It runs a simple script
language that mixes SQL with commands.

A command is simply a name token with a dot prefix.  For instance, ``.open``,
``.close``, etc.

Jaqy also supports SQL line comments which starts with ``--``.  It should be
noted that Jaqy does not recognize comments ``/* */``.  Comments recognized
by Jaqy is not sent to the database, but sometimes such behavior may not be
wanted since it may be used to contain some metadata for logging purposes.
Thus, ``/* */`` is not recognized by Jaqy.

Launching Jaqy
--------------

Once `downloaded <download.html>`__ ``jaqy-1.0.jar``, it can be run using
the following command.

.. code-block:: bash

	java -jar jaqy-1.0.jar

If you need to load / export large amount of data though, it can be necessary
to allocate a bit more memory.

.. code-block:: bash

	java -Xmx256m -jar jaqy-1.0.jar

If you are going to have Unicode outputs, it may be necessary to specify the
encoding for the standard output.

.. code-block:: bash

	java -Dfile.encoding=UTF-8 -Xmx256m -jar jaqy-1.0.jar

It is recommended to have the above script in an alias

.. code-block:: bash

	alias jq='java -Dfile.encoding=UTF-8 -Xmx256m -jar jaqy-1.0.jar'

or a batch file.

Opening a Connection
--------------------

Here I use Teradata JDBC as an example.

.. code-block::	sql

	.protocol teradata com.teradata.jdbc.TeraDriver
	.classpath teradata lib/tdgssconfig.jar:lib/terajdbc4.jar
	.open -u dbc -p dbc teradata://127.0.0.1

The first command specifies the driver informationi for ``teradata`` protocol.
The second command specifies the class path to dynamically load the driver.
In this case, two jar files are necessary.  The last command actually opens
a JDBC connection to ``127.0.0.1`` with user ``dbc`` and password ``dbc``.

The first command is actually not necessary since Jaqy actual knows
the driver name of several databases.  You can look at the existing list of
driver names by simply run ``.protocol`` command by itself.

The second command if used frequently, can be put in ``~/.jqrc`` so that one
does not need to type it.  The driver is not loaded until the corresponding
JDBC protocol is used.

The ``.open`` command is used to initiate a database connection via JDBC
driver.  ``-u`` option is used to specify the user name ``dbc`` and ``-p``
option is used to specify the password ``dbc``.  The connection URL
``teradata://127.0.0.1`` is basically a JDBC connection URL without ``jdbc:``
prefix.

Exeuting a SQL Query
--------------------

Jaqy recognizes any text that are not started with ``.`` as SQL.  The SQL
block is ended with a semicolon ``;`` as the last character on a line.

.. code-block::	sql

	SELECT *
	FROM MyTable
	ORDER BY a;

It is possible to enter chain multiple queries in a single statement or enter
a slightly complex statement by avoiding having the first ``;`` as the last
character on a line.  Here are two examples.

.. code-block::	sql

	CREATE MACRO myquery AS
	(
		SELECT * FROM MyTable
		ORDER BY a
	;);

.. code-block::	sql

	SELECT * FROM MyTable ORDER BY a
	; SELECT * FROM MyTable ORDER BY a;

To enter even more complex SQL as a single statement, as in the case of
a stored procedure, one can use ``.exec`` to do so.

.. code-block::	sql

	.exec
	CREATE MACRO myquery AS
	(
		SELECT * FROM MyTable
		ORDER BY a
	;);
	.end exec

Or simply use ``.exec`` to execute an external SQL file.

.. code-block::	sql

	.exec mysp.sql

Closing a Connection
--------------------

The command is simply

``.close``

Exiting Jaqy
------------

You can either run ``.quit`` or ``.exit`` command to exit Jaqy.

Running a Jaqy Script
---------------------

You can enter commands or SQL interactively, or use put everything in a script
and pipe in the input.

.. code-block::	sql

	.open -u dbc -p dbc teradata://127.0.0.1

	/* switch to test database */
	DATABASE test;

	CREATE TABLE MyTable
	(
		a INTEGER,
		b INTEGER
	);

	-- Populate with data
	INSERT INTO MyTable VALUES (1, 1);
	INSERT INTO MyTable VALUES (2, 2);

	-- Testing macro creation with ; handling
	CREATE MACRO myquery AS
	(
		SELECT * FROM MyTable
		ORDER BY a
	;);

	EXEC myquery;

	-- Cleaning up
	DROP MACRO myquery;
	DROP TABLE MyTable;

	.quit

.. code-block::	bash

	jq < input.sql

The output of a script is generally to the standard output.  However, some
JDBC drivers may print debugging or error information to the standard error.
