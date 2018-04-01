echo
----

``echo`` setting turns input echo on / off.

This is an interpreter level setting.

Syntax
~~~~~~

.. code-block:: text

	usage: .set echo [on | off | auto]

The default is ``auto`` which has the following behavior.  If the input is
interactive, then echo is off.  Otherwise, it is on.
