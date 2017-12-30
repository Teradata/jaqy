Comments
========

Jaqy recognizes two types of comments.

* Line comments on a line started with ``--``.  It should be noted that if
  ``--`` is not at the beginning of the line, then it would not be recognized.

* Block comments enclosed by `.rem <command/rem.html>`__ command and
  ``.end rem``.

Note that ``/* */`` style block comment is not recognized by Jaqy.

When Jaqy recognizes block of text as comment, it is ignored without
transmitting the text to the database server.  Otherwise, if the text
does not start with ``.``, then it is considered as part of SQL statement,
and would be transmitted to the database server.

Examples
--------

.. code-block:: sql

	-- a Jaqy line comment

	-- The following two lines are executed together.
	SELECT 1234; -------- Not recognized as Jaqy line comment
	SELECT 1234;

	.rem a Jaqy block comment;
	SELECT 1234;
	SELECT 1234;
	.end rem

	/* This is not recognized as a Jaqy block comment.  It is transmitted
	   with the following SQL statement to the database server. */
	SELECT 1234;

	/* You will see error messages since `;` at EOL indicates the end of a SQL
	   statement.

	SELECT * FROM MyTable;
	*/
