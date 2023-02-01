Overview
--------

The currently available command line options are the following.

.. code-block:: text

    usage: java -jar jaqy.jar [options] [commands]
    options:
         --color <on | off>         turns color support on / off
      -e,--echo <on | off | auto>   turns echo on / off / auto
      -h,--help                     displays this help information, then exit
         --norc                     does not read the initialization file
         --rcfile <file>            specifies the initialization file. Default is
                                    ~/.jqrc
      -v,--version                  displays version information, then exit
    commands:
      a series of commands separated by ;

Entering Commands
~~~~~~~~~~~~~~~~~

You do not need to enter each command as a whole string as Jaqy can concatenate
the parameters into a string.

.. code-block:: bash

	java -jar jaqy.jar .quiet on \; .run myscript.jq \; .quit

It is equivalent of running the following script.

.. code-block:: sql

	.quiet on
	.run myscript.jq
	.quit

Dealing with ``-`` in Commands
==============================

Some jaqy commands may have ``-`` options which cause problems for command line
processing.  See the following example.

.. code-block:: text

	$ java -jar jaqy.jar .open -u dbc -p dbc teradata://mytestvm
	Unrecognized option: -u

There are two solutions.

The first solution is to use ``--`` to stop command line option handling
and mark the beginning of the jaqy commands.

.. code-block:: bash

	java -jar jaqy.jar -- .open -u dbc -p dbc teradata://mytestvm

The second solution would simply enclose each jaqy command in quotes.

.. code-block:: bash

	java -jar jaqy.jar '.open -u dbc -p dbc teradata://mytestvm'

Entering Multiple Commands
==========================

Jaqy recognizes a standalone ``;`` parameter as a separator for multiple
jaqy commands / queries.

In Windows terminals, there is no need to escape ``;``.  However in bash,
``;`` needs to be escaped.

See the following example.

.. code-block:: bash

	# Log into my Teradata database on start up, run a query, and quit
	# Notice the second ; is in a long string to prevent it from being recognized a command separator
	java -jar jaqy.jar '.open -u dbc -p dbc teradata://mytestvm' \; 'SELECT 1234;' \; .quit


Complicated Example
===================

Here is a complicated example of doing everything from command line.

.. code-block:: bash

	# One line command to log into a database, do some queries and quit
	java -jar jaqy.jar --rcfile clouddbinit.jq -- '.open -u dbc -p dbc teradata://mytestvm' \; 'DATABASE test;' \; 'SELECT * FROM myTable;' \; .quit

It is equivalent of running the following SQL script, in addition to
having a custom startup initiation file.

.. code-block:: sql

	.open -u dbc -p dbc teradata://mytestvm
	DATABASE test;
	SELECT * FROM myTable;
	.quit
