.alias
------

``.alias`` creates a command alias.

To remove an alias, use `.unalias <unalias.html>`__ command.

Syntax
~~~~~~

.. code-block:: text

	usage:
		.alias name
		string
		.end alias

If name is not specified, the command will list existing aliases.

Note that any SQL must end with ``;`` or the parser would consider that more
text are coming.

The alias command supports command line arguments, which are separate via
spaces or tabs.  The first argument is ``$0`` (or ``${0}``), the second argument
is ``${1}``, and so on.  You can specify a range of arguments (e.g. ``${1-2}``)
or use open ended argument ranges (e.g. ``${1-}``).

All the commands and SQL are echoed when they are executed.  If you want to
hide the display of a command, use ``.@`` prefix.  For instance, instead of
using ``.open``, use ``.@open``.

Example
~~~~~~~

.. code-block:: sql

	-- List aliases
	.alias

	-- create a testing table
	CREATE TABLE MyTable (a INTEGER, b INTEGER);

	-- Create a short hand for insertion
	.alias ins
	INSERT INTO ${0} VALUES (${1-});
	.end alias

	-- Testing the insert alias
	.ins MyTable 1, 1

	SELECT * FROM MyTable;

And you should see that a row of data is inserted into ``MyTable``.  The
second argument is actually ``1,``, and the third argument is ``1``.

See Also
~~~~~~~~

* `.unalias <unalias.html>`__
