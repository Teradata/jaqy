.config
-------

``.config`` configures database handlings using JSON.  For more information
on the format, see `Database Configurations <../config/database.html>`__.

.. note::

	Database configurations does not apply to the current active connection.

Example
~~~~~~~

.. code-block:: text

	.config
	[
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
	},
	{
		"protocol":	"sqlite",
		"tableSchemaSQL": {
			"sql" : "SELECT sql FROM SQLITE_MASTER WHERE NAME = ''{0}'' COLLATE NOCASE",
			"field" : 1
		},
		"tableColumnSQL": {
			"sql" : "PRAGMA table_info([{0}])",
			"field" : 1
		}
	},
	{
		"protocol":	"derby",
		"typeMap": [
			{ "name" : "CHAR ({0}) FOR BIT DATA", "type" : -2, "maxPrecision" : 254 },
			{ "name" : "VARCHAR ({0}) FOR BIT DATA", "type" : -3, "maxPrecision" : 32672 },
			{ "name" : "LONG VARCHAR FOR BIT DATA", "type" : 1, "maxPrecision" : 32700 }
		]
	},
	{
		"protocol":	"teradata",
		"typeMap": [
			{ "name" : "CHAR({0}) CHARACTER SET LATIN", "type" : 1, "maxPrecision" : 64000 },
			{ "name" : "VARCHAR({0}) CHARACTER SET LATIN", "type" : 12, "maxPrecision" : 64000 },
			{ "name" : "CLOB({0}) CHARACTER SET LATIN", "type" : 2005, "maxPrecision" : 2097088000 },
			{ "name" : "CHAR({0}) CHARACTER SET UNICODE", "type" : 1, "maxPrecision" : 32000 },
			{ "name" : "VARCHAR({0}) CHARACTER SET UNICODE", "type" : -9, "maxPrecision" : 32000 },
			{ "name" : "CLOB({0}) CHARACTER SET UNICODE", "type" : 2011, "maxPrecision" : 1048544000 }
		],
		"features": {
			"catalog" : false
		},
		"schemaSQL": {
			"sql" : "SELECT DATABASE",
			"field" : 1
		},
		"tableSchemaSQL": {
			"sql" : "SHOW TABLE {0}",
			"field" : 1
		},
		"tableColumnSQL": {
			"sql" : "HELP TABLE {0}",
			"field" : 1
		}
	}
	]
	.end config

See Also
~~~~~~~~

* `Database Configurations <../config/database.html>`__
