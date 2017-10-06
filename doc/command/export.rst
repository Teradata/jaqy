.export
-------

This command exports data from the database.

Syntax
~~~~~~

.. code-block:: text

	usage .export [type] [type options] [file]
	type:
	  avro
	  csv
	  json

	avro type options:
	  -c,--compression <arg>   sets the compression codec

	csv type options:
	  -c,--charset <arg>                                    sets the file
	                                                        character set
	  -d,--delimiter <arg>                                  specifies the
	                                                        delimiter
	  -t,--type <default | excel | rfc4180 | mysql | tdf>   sets the csv type.

	json type options:
	  -b,--binary <base64 | hex>   sets the binary format.
	  -c,--charset <arg>           sets the file character set
	  -f,--format <text | bson>    sets the JSON format.
	  -p,--pretty <on | off>       turns pretty print on / off.

Formats
~~~~~~~

csv
^^^

* Binary types are converted to hexadecimal format.
* NULL values are convert to empty strings.
* Array and Struct types are converted to string simply using toString()
  functions of the object.

	* For Teradata, PERIOD data types, which are transmitted as Struct types,
	  are detected and converted into formats that matches their BTEQ output
	  formats.

json
^^^^

* Binary types are converted to either base64 (default) or hexadecimal format.
* Array and Struct types are converted to JSON array type.

avro
^^^^

* Array and Struct types are converted to array of string if its element types
  cannot be guessed.

	* PostgreSQL Array element types can be easily guessed from the type name,
	  so if they are basic types (int, float, etc), then they are stored as
	  array of mapped AVRO types.
	* In other cases, they are array of string types.  The main difficulty
	  here is that JDBC ResultSetMetaData simply does not have a way to
	  provide detailed type information for Array and Struct types.

``-c`` option specifies the compression codec.  The following codecs are
possible.  See
`CodeFactory.fromString(String s) <http://avro.apache.org/docs/current/api/java/org/apache/avro/file/CodecFactory.html#fromString(java.lang.String)>`__
for more information.

	* null (no compression)
	* deflate
	* snappy
	* bzip2
	* xz

Example
~~~~~~~

.. code-block:: sql

	.export csv -c utf-8 file1.csv
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `.import <import.html>`__
