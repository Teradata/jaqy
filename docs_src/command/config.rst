.config
-------

``.config`` configures database handlings using JSON.  For more information
on the format, see `Database Configurations <../config/database.html>`__.
The intent is that Jaqy can be adapted to a new database more easily without
writing a specific helper plugin for it.

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
			"schema" : false,
			"stream" : false,
			"setObjectType" : false
		},
		"typeMap": [
			{ "type" : "CHAR", "name" : "CHAR({0,number,#})", "maxPrecision" : 255 },
			{ "type" : "VARCHAR", "name" : "VARCHAR({0,number,#})", "maxPrecision" : 21845 }
		],
		"importTypeMap": [
			{ "type" : "CHAR", "name" : "TEXT", "maxPrecision" : 65535 },
			{ "type" : "VARCHAR", "name" : "TEXT", "maxPrecision" : 65535 },
			{ "type" : "LONGVARCHAR", "name" : "MEDIUMTEXT", "maxPrecision" : 16777215 },
			{ "type" : "CLOB", "name" : "LONGTEXT", "maxPrecision" : 2147483647 },
			{ "type" : "NCHAR", "name" : "TEXT", "maxPrecision" : 32767 },
			{ "type" : "NVARCHAR", "name" : "TEXT", "maxPrecision" : 32767 },
			{ "type" : "LONGNVARCHAR", "name" : "MEDIUMTEXT", "maxPrecision" : 8388607 },
			{ "type" : "NCLOB", "name" : "LONGTEXT", "maxPrecision" : 1073741823 },
			{ "type" : "BINARY", "name" : "BLOB", "maxPrecision" : 65535 },
			{ "type" : "VARBINARY", "name" : "BLOB", "maxPrecision" : 65535 },
			{ "type" : "LONGVARBINARY", "name" : "MEDIUMBLOB", "maxPrecision" : 16777215 },
			{ "type" : "BLOB", "name" : "LONGBLOB", "maxPrecision" : 2147483647 }
		],
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
		"protocol":	"postgresql",
		"features": {
			"streamLength" : false
		},
		"typeMap": [
			{ "type" : "SMALLINT", "name" : "int2" },
			{ "type" : "CHAR", "name" : "char({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "VARCHAR", "name" : "varchar({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "CLOB", "name" : "text", "maxPrecision" : 2147483648 },
			{ "type" : "NCHAR", "name" : "char({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "NVARCHAR", "name" : "varchar({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "NCLOB", "name" : "text" }
		],
		"importTypeMap": [
			{ "type" : "BOOLEAN", "name" : "bool" },
			{ "type" : "SMALLINT", "name" : "int2" },
			{ "type" : "INTEGER", "name" : "int4" },
			{ "type" : "CHAR", "name" : "text" },
			{ "type" : "VARCHAR", "name" : "text" },
			{ "type" : "CLOB", "name" : "text" },
			{ "type" : "NCHAR", "name" : "text" },
			{ "type" : "NVARCHAR", "name" : "text" },
			{ "type" : "NCLOB", "name" : "text" }
		]
	},
	{
		"protocol":	"sqlite",
		"features": {
			"stream" : false
		},
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
		"features": {
			"setObjectType" : false
		},
		"typeMap": [
			{ "type" : "BINARY", "name" : "CHAR ({0,number,#}) FOR BIT DATA", "maxPrecision" : 254 },
			{ "type" : "VARBINARY", "name" : "VARCHAR ({0,number,#}) FOR BIT DATA", "maxPrecision" : 32672 },
			{ "type" : "LONGVARBINARY", "name" : "LONG VARCHAR FOR BIT DATA", "maxPrecision" : 32700 }
		]
	},
	{
		"protocol":	"sqlserver",
		"catalogSQL": {
			"sql" : "SELECT DB_NAME()",
			"field" : 1
		},
		"typeMap": [
			{ "type" : "BOOLEAN", "name" : "bit" },
			{ "type" : "BINARY", "name" : "binary({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "VARBINARY", "name" : "varbinary({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "LONGVARBINARY", "name" : "varbinary({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "CHAR", "name" : "char({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "VARCHAR", "name" : "varchar({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "LONGVARCHAR", "name" : "varchar({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "CLOB", "name" : "varchar({0,number,#})", "maxPrecision" : 2147483648 },
			{ "type" : "NCHAR", "name" : "nchar({0,number,#})", "maxPrecision" : 1073741824 },
			{ "type" : "NVARCHAR", "name" : "nvarchar({0,number,#})", "maxPrecision" : 1073741824 },
			{ "type" : "LONGNVARCHAR", "name" : "nvarchar({0,number,#})", "maxPrecision" : 1073741824 },
			{ "type" : "NCLOB", "name" : "nvarchar({0,number,#})", "maxPrecision" : 1073741824 }
		]
	},
	{
		"protocol":	"teradata",
		"typeMap": [
			{ "type" : "CHAR", "name" : "CHAR({0,number,#}) CHARACTER SET LATIN", "maxPrecision" : 64000 },
			{ "type" : "VARCHAR", "name" : "VARCHAR({0,number,#}) CHARACTER SET LATIN", "maxPrecision" : 64000 },
			{ "type" : "LONGVARCHAR", "name" : "VARCHAR({0,number,#}) CHARACTER SET LATIN", "maxPrecision" : 64000 },
			{ "type" : "CLOB", "name" : "CLOB({0,number,#}) CHARACTER SET LATIN", "maxPrecision" : 2097088000 },
			{ "type" : "NCHAR", "name" : "CHAR({0,number,#}) CHARACTER SET UNICODE", "maxPrecision" : 32000 },
			{ "type" : "NVARCHAR", "name" : "VARCHAR({0,number,#}) CHARACTER SET UNICODE", "maxPrecision" : 32000 },
			{ "type" : "LONGNVARCHAR", "name" : "VARCHAR({0,number,#}) CHARACTER SET UNICODE", "maxPrecision" : 32000 },
			{ "type" : "NCLOB", "name" : "CLOB({0,number,#}) CHARACTER SET UNICODE", "maxPrecision" : 1048544000 }
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
