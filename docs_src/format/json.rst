json
^^^^

Options
*******

.. code-block:: text

	  -b,--binary <base64 | hex>   sets the binary format.
	  -p,--pretty <on | off>       turns pretty print on / off.

.. note::

	* Binary types are converted to either base64 (default) or hexadecimal format.
	* `Array <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__ and
	  `Struct <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__
	  types are converted to JSON array type.

Example
*******

.. code-block:: sql

	.format json -b hex
	-- 4/6 - 0 ----------------------------------------------------------------
	SELECT * FROM BinTable ORDER BY a;
	-- success --
	[
		{
			"a" : 1,
			"b" : "deadbeef"
		},
		{
			"a" : 2,
			"b" : "facedead"
		}
	]
	-- activity count = 2
