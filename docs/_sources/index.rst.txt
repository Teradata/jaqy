Jaqy Documentation
===========================

Jaqy is a universal database client for connecting any databases with
`JDBC <https://en.wikipedia.org/wiki/Java_Database_Connectivity>`__ drivers.
It is designed with the following features in mind.

* Zero installation and requiring no administrative privileges.
  In contrast, `ODBC <https://en.wikipedia.org/wiki/Open_Database_Connectivity>`__ drivers
  require administrative privileges to install and setup.
* Late JDBC binding.  JDBC drivers are loaded dynamically only when needed.
  As the result, Jaqy starts up quickly without needing to load unnecessary
  drivers.
* Interactive and scripting support.
* Various data display format.  Currently, the following display formats are
  supported.

  * table
  * csv
  * json

* Various data format supports for loading into and exporting from databases.
  Currently, the following formats are supported.

  * `csv <https://en.wikipedia.org/wiki/Comma-separated_values>`__
  * `json <http://www.json.org/>`__ / `bson <http://bsonspec.org/>`__
  * `avro <https://avro.apache.org/docs/current/>`__

* `JavaScript <http://docs.oracle.com/javase/7/docs/technotes/guides/scripting/programmer_guide/>`__
  integration to allow conditional statements and expression expansion.
* Extensible.  Plugins can be loaded at runtime to add new commands, new display,
  import and export formats, etc.

Jaqy is mainly tested against `SQLite <https://github.com/xerial/sqlite-jdbc>`__,
`Apache Derby <https://db.apache.org/derby/>`__, `MySQL <https://www.mysql.com/>`__
and `PostgreSQL <https://www.postgresql.org/>`__,
since they can be easily made available as part of
`CI <https://en.wikipedia.org/wiki/Continuous_integration>`__ process.
Brief testings on Teradata, HIVE, Presto, Teradata Aster, Microsoft SQL Server
etc were done, and it worked pretty well.

**This software is NOT SUPPORTED by Teradata.** This is an approved personal
project of `@coconut2015 <https://github.com/coconut2015>`__.

.. include::	toc.txt
