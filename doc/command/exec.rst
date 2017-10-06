.exec
-----

``.exec`` runs a block of text in a single execution.

Sometimes, it is necessary to execute a large SQL with lots of ``;``
characters at end of the line.  For instance, stored procedures do so
frequently.  It is thus necessary to introduce a way to do.

Syntax
~~~~~~

.. code-block:: bash

	usage: .exec [options] [file]
	options:
	  -c,--charset <arg>   sets the file character set


Example
~~~~~~~

.. code-block:: sql

	-- Execute an external file as a SQL
	.exec myproc.spl

	-- Execute an inline block of SQL
	.exec
	CREATE PROCEDURE simpleproc (OUT c INT)
	BEGIN
			SELECT COUNT(*) INTO c FROM MyTable;
	END;
	.end exec
