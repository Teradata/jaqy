--echo
------

``--echo`` turns input echo on / off / auto.

By default, echo is ``auto``.  That is, the input echo is off in interactive
mode.  The echo support on off in script mode (i.e. I/O is redirected).

This option can be used to manually set the the echo.

Short Option
~~~~~~~~~~~~

``-e``

Syntax
~~~~~~

.. code-block:: text

	--echo <on | off | auto>

Example
~~~~~~~

.. code-block:: bash

	# turn on input echo
	java -jar jaqy.jar --echo on
