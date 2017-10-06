JSON Configuration
==================

Field Description
-----------------

It is possible to extends Jaqy using JSON.

``protocol`` indicates which protocol is being configured.

``features`` list the functionalities that a given database does not support.

* ``"schema" : false`` if the database does not support schema.
* ``"catalog" : false`` if the database does not support catalog.

``catalogSQL`` is used by `.pwd <command.html#pwd>`__ and
`.list <command.html#list>`__ commands to determine the current catalog.

``schemaSQL`` is used by `.pwd <command.html#pwd>`__ and
`.list <command.html#list>`__ commands to determine the current schema.

``tableColumnSQL`` is used by `.desc <command.html#desc>`__ command to list
the columns in a table.  Jaqy uses 
`MessageFormat <https://docs.oracle.com/javase/8/docs/api/java/text/MessageFormat.html>`__
to construct the SQL query.

``tableSchemaSQL`` is used by `.desc <command.html#desc>`__ command to show
the table schema in SQL.  Jaqy uses 
`MessageFormat <https://docs.oracle.com/javase/8/docs/api/java/text/MessageFormat.html>`__
to construct the SQL query.

Example
-------

MySQL configuration

.. code-block::	json

	{
		"protocol":	"mysql",
		"features": {
			"schema" : false
		},
		"catalogSQL": {
			"sql" : "SELECT DATABASE()",
			"field" : 1
		},
		"tableSchemaSQL": {
			"sql" : "SHOW CREATE TABLE {0}",
			"field" : 2
		},
		"tableColumnSQL": {
			"sql" : "DESCRIBE {0}",
			"field" : 1
		}
	}

SQLite configuration

.. code-block::	json

	{
		"protocol":	"sqlite",
		"tableSchemaSQL": {
			"sql" : "SELECT sql FROM SQLITE_MASTER WHERE NAME = ''{0}''  COLLATE NOCASE",
			"field" : 1
		},
		"tableColumnSQL": {
			"sql" : "PRAGMA table_info([{0}])",
			"field" : 1
		}
	}
