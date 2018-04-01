expansion
---------

``expansion`` setting turns on / off expression expansion.

This is an interpreter level setting.

By default, it is on.

Syntax
~~~~~~

.. code-block:: text

	usage: .set expansion [on | off]

Executing the setting without arguments will display the expansion setting
for the current interpreter.

Example
~~~~~~~

.. code-block:: sql

	.script
	var var1 = 'abc';
	var var2 = 'def';
	.end script

	.set expansion off

	SELECT '${var1}' AS Test;
	SELECT '${var2}' AS Test;
	SELECT '${var1 + var2}' AS Test;

	.set expansion on

	SELECT '${var1}' AS Test;
	SELECT '${var2}' AS Test;
	SELECT '${var1 + var2}' AS Test;

See Also
~~~~~~~~

* `Scripting <../script.html>`__
