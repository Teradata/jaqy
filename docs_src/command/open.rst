.open
-----

``.open`` opens a JDBC connection in the current session.  If the current
session already has an open database connection, the command would fail.

In order for ``.open`` command to work, `.protocol <protocol>`__ and `.classpath <classpath>`__
settings must be configured before the protocol is used to open a connection.

Syntax
~~~~~~

.. code-block:: bash

	usage: .open [options] [url]
	options:
	  -D <name=value>       set a connection property
	  -f,--prompt           force password prompt
	  -p,--password <arg>   specify the password
	  -u,--user <arg>       specify the user

URL
^^^

URL is basically a JDBC connection URL without ``jdbc:`` prefix.


Example
~~~~~~~

.. code-block:: sql

	-- Open a Teradata session with client character set being UTF8.
	.open -u dbc -p dbc teradata://127.0.0.1/CHARSET=UTF8
	.close

	-- Open a PostgreSQL connection with user postgres and empty password
	.open -u postgres postgresql://127.0.0.1/
	.close

	-- Open a MySQL connection with root user, and default database vagrant
	.open -u root mysql://localhost/vagrant
	.close

	-- Open SQLite in-memory database
	.open sqlite::memory:
	.close
