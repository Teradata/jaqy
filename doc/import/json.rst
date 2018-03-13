json
^^^^

JSON importer only supports name based loading.

It does *not* support schema discovery.


Options
*******

.. code-block:: text

	  -a,--array                   treats BSON root document as array.
	  -b,--binary <base64 | hex>   sets the binary format.
	  -c,--charset <arg>           sets the file character set
	  -f,--format <text | bson>    sets the JSON format.

Example
*******

.. code-block:: sql

	.import json -b hex myfile.json
	INSERT INTO MyTable VALUES ({{a}}, {{b}});
