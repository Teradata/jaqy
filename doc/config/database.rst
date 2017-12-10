JSON Configuration
==================

`.config <command/config.html>`__ command is used to configure a database
using JSON configuration.  It should be noted that such configuration should
only be applied before connecting to the database.

Default Configuration
---------------------

The default configuration can be seen at
`initrc <https://github.com/Teradata/jaqy/blob/master/jaqy-console/src/main/resources/com/teradata/jaqy/initrc>`__
.

Field Description
-----------------

``protocol`` indicates which protocol is being configured.

``catalogSQL`` is used by `.pwd <command.html#pwd>`__ and
`.list <command.html#list>`__ commands to determine the current catalog.

``features`` list the functionalities that a given database does not support.

* ``"schema" : false`` if the database does not support schema.
* ``"catalog" : false`` if the database does not support catalog.

``schemaSQL`` is used by `.pwd <command/pwd.html>`__ and
`.list <command/list.html>`__ commands to determine the current schema.

``tableColumnSQL`` is used by `.desc <command.html#desc>`__ command to list
the columns in a table.  Jaqy uses 
`MessageFormat <https://docs.oracle.com/javase/8/docs/api/java/text/MessageFormat.html>`__
to construct the SQL query.

``tableSchemaSQL`` is used by `.desc <command.html#desc>`__ command to show
the table schema in SQL.  Jaqy uses 
`MessageFormat <https://docs.oracle.com/javase/8/docs/api/java/text/MessageFormat.html>`__
to construct the SQL query.

``typeMap`` sets up certain type information for properly inferring the type
information.  JDBC's
`DatabaseMetadata.getTypeInfo() <https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getTypeInfo-->`__.
has a number of limitations that makes it difficult to use.  Usually only types
such as ``VARCHAR(1000)`` with variable length information needs to be
specified.  Jaqy uses 
`MessageFormat <https://docs.oracle.com/javase/8/docs/api/java/text/MessageFormat.html>`__
to construct the type name, with precision and scale passed to it.

Occasionally, for databases with multiple types for the same type identifier,
the most common one to use may be specified here as well.

