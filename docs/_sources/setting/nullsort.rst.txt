nullsort
--------

``nullsort`` setting sets whether ``NULL`` should be sorted as low or high.

This is an interpreter level setting.

By default, it is low.

Syntax
~~~~~~

.. code-block:: text

	usage: .set nullsort [low | high]

Executing the setting without arguments will display the nullsort setting
for the current interpreter.

Example
~~~~~~~

.. code-block:: sql

	.set nullsort low
	.sort a DESC
	SELECT * FROM MyTable;

	.set nullsort high
	.sort a DESC
	SELECT * FROM MyTable;

See Also
~~~~~~~~

* `Scripting <../script.html>`__
* `.sort <../command/sort.html>`__
