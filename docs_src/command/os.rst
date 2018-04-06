.os
------

``.os`` runs shell commands.

On Windows, this command runs ``cmd.exe``, and on Linux, it runs ``/bin/bash``.

Each ``.os`` command runs its own shell, which terminates after the command is
completed.

Syntax
~~~~~~

.. code-block:: text

	usage: .os [shell commands]

Example
~~~~~~~

.. code-block:: sql

	.os echo asdf && echo ddd > dummy.txt
	asdf
	-- 0/4 - 0 ------------------------------------------
	.os cat dummy.txt
	ddd
	-- 0/5 - 0 ------------------------------------------
	.os rm -f dummy.txt
