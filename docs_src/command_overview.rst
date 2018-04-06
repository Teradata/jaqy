Overview
--------

For a list of currently installed commands, simply run the following command
to list them.

.. code-block::	text

	.help

To get more detailed information on a command, just do a help on the command.

.. code-block::	text

	.help command

Hiding a Command
~~~~~~~~~~~~~~~~

It is possible to hide the execution of a command in a script, by using ``.@``
prefix.  For example, the following command can be used to hide the actual
user, password, and the database URL in the output.

.. code-block::	text

	.@open -u root mysql://localhost/vagrant

It should be noted that by default, commands in ``~/.jqrc`` is not echoed.
So it is not necessary to use ``.@`` prefix there.

Character Set
~~~~~~~~~~~~~

A number of commands such as ``.run``, ``.exec`` etc have options to specify
the character set of the input file.

Supported character sets by Java can be seen at
https://docs.oracle.com/javase/8/docs/technotes/guides/intl/encoding.doc.html .

If the character set is not specified, an attempt to detect
`BOM <https://en.wikipedia.org/wiki/Byte_order_mark>`__ is made.  If BOM is not
present, then the default character set is used.


Relative Path
~~~~~~~~~~~~~

All files and classpaths are relative to the current `.run <run>`__ script
directory.  If there is no current ``.run`` script, then they are relative to
the current directory.
