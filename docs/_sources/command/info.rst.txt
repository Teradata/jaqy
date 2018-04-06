.info
-----

``.info`` command displays various information for the current session.

Syntax
~~~~~~

.. code-block:: text

	usage: info [type]
	type:
	  behavior   database behavior
	  catalog    database catalogs
	  client     client info properties
	  feature    database features
	  function   database functions
	  keyword    SQL keywords
	  limit      database limits
	  schema     database schemas
	  server     database server
	  table      database table types
	  type       database type info
	  user       logon user

Example
~~~~~~~

.. code-block:: text

	.info server
	Name                     Value
	------------------------ ----------------------------------------------------------------------------------
	User                     root@localhost
	URL                      jdbc:mysql://localhost/?useUnicode=true&characterEncoding=utf-8
	Ready only               No
	Database product name    MySQL
	Database product version 5.6.33-0ubuntu0.14.04.1
	Database major version   5
	Database major version   6
	Driver name              MySQL Connector Java
	Driver version           mysql-connector-java-5.1.39 ( Revision: 3289a357af6d09ecc1a10fd3c26e95183e5790ad )
	JDBC major version       4
	JDBC minor version       0
