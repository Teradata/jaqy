--color
-------

``--color`` turns `ansi color <https://en.wikipedia.org/wiki/ANSI_escape_code#Colors>`_ support on /off.

By default, the color support is on in interactive mode.  The color support
is off in script mode (i.e. I/O is redirected).

This option can be used to manually set the the color support.

Syntax
~~~~~~

.. code-block:: text

	usage: --color <on | off>

Example
~~~~~~~

.. code-block:: bash

	# disable ansi color
	java -jar jaqy.jar --color off
