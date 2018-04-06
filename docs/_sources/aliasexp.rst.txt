Aliases and Expression Expansion
================================

Aliases
-------

Aliases in Jaqy can be shortcuts to SQL statements, commands, or combinations
of both.  `.alias <command/alias.html>`__ is used to create aliases.

For example, the following is a very convenient alias for an ``INSERT``
statement without having to type too much.

.. code-block:: sql

	.alias ins
	INSERT INTO $0 VALUES ($(1-));
	.end alias

	.ins MyTable 1, 1

``$0``, ``$(1)``, ``$(1-)`` etc are the way to specify how argument
substitution is made.

You can also combine multiple SQL statements and commands together in one
alias, to create powerful macros.

For example, the following is a simple trick of dropping a table if it
exists, without generating error messages.  `.list <command/list.html>`__
command is a convenient way of listing tables in the current catalog / schema /
database.  If the table MyTable exists, then it would show up in the search.
By checking the number of rows returned, which is tracked by the variable
``activityCount``, we can tell if the table exists.  And we can use
`.if <command/if.html>`__ to conditionally execute our ``DROP TABLE``
statement.

.. code-block:: sql

	.list . . MyTable
	.if activityCount > 0
	DROP TABLE MyTable;
	.end if

Now, it is possible to create an alias for the entire thing and apply to
different tables.

.. code-block:: sql

	.alias dropifexists
	.list . . $(0)
	.if activityCount > 0
	DROP TABLE $(0);
	.end if
	.end alias

	-- test the alias
	.dropifexists MyTable

Notice the way to execute the alias is similar to a command.  Essentially,
having aliases is one way to create custom commands.

Expression Expansion
--------------------

Expression expansion is almost like a variable substitution, except that
functions can be called as well.  It is quite useful in generating SQL
which might be otherwise somewhat difficult.

For example, here is a simple script that generates some Well-known text (WKT)
data using expression expansion.

.. code-block:: sql

	CREATE TABLE MyTable (a INT, b VARCHAR(100));
	.repeat 10
	INSERT INTO MyTable VALUES (${iteration}, 'POINT(${Math.sin(iteration)} ${Math.sin(iteration)})');

`.repeat <command/repeat.html>`__ command is used to repeatedly submit a SQL.
Internally, it uses a variable ``iteration`` to track the iteration.
``${`` and ``}`` encloses any JavaScript expression that can be inline
evaluated.

The result is the following.

.. code-block:: sql

	SELECT * FROM MyTable ORDER BY a;

	 a b
	-- ------------------------------------------------
	 1 POINT(0.8414709848078965 0.8414709848078965)
	 2 POINT(0.9092974268256817 0.9092974268256817)
	 3 POINT(0.1411200080598672 0.1411200080598672)
	 4 POINT(-0.7568024953079282 -0.7568024953079282)
	 5 POINT(-0.9589242746631385 -0.9589242746631385)
	 6 POINT(-0.27941549819892586 -0.27941549819892586)
	 7 POINT(0.6569865987187891 0.6569865987187891)
	 8 POINT(0.9893582466233818 0.9893582466233818)
	 9 POINT(0.4121184852417566 0.4121184852417566)
	10 POINT(-0.5440211108893698 -0.5440211108893698)

Combined with `client-side ResultSet handling <clientrs.html>`__, expression
expansion can also be used to generate SQL DDLs that are normally fairly
difficult to do without using a full programming language.

For example, without database support, dropping all tables in a database
can be somewhat challenging.  With Jaqy, it is quite straightforward.  See this
`test script <https://github.com/Teradata/jaqy/blob/master/tests/unittests/commands/save_3.sql>`__
and its `output <https://github.com/Teradata/jaqy/blob/master/tests/unittests/commands/control/save_3.control>`__.
