avro
^^^^

This feature requires ``jaqy-avro`` plugin.

Options
*******

.. code-block:: text

	  -c,--compression <arg>   sets the compression codec

.. note::

	* Supported compression can be seen at
	  `CodecFactory <https://avro.apache.org/docs/1.8.2/api/java/org/apache/avro/file/CodecFactory.html#fromString(java.lang.String)>`__

	  * null
	  * deflate
	  * snappy
	  * bzip2
	  * xz

Database Type to AVRO Type Mapping
**********************************

+---------------+-----------------+
| Database Type | AVRO Type       |
+===============+=================+
| BOOLEAN       | BOOLEAN         |
+---------------+-----------------+
| TINYINT       | INTEGER         |
| SMALLINT      |                 |
| INTEGER       |                 |
+---------------+-----------------+
| BIGINT        | LONG            |
+---------------+-----------------+
| FLOAT         | FLOAT           |
+---------------+-----------------+
| DOUBLE        | DOUBLE          |
+---------------+-----------------+
| ARRAY         | ARRAY           |
+---------------+-----------------+
| BINARY        | BYTES           |
| VARBINARY     |                 |
| LONGVARBINARY |                 |
| BLOB          |                 |
+---------------+-----------------+
| DECIMAL       | STRING          |
| NUMERIC       |                 |
| REAL          |                 |
| CHAR          |                 |
| VARCHAR       |                 |
| CLOB          |                 |
+---------------+-----------------+

.. note::

	* DECIMAL is converted to string to preserve the precision.
	* `Array <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__
	  is in general treated as array of string types.  The primary reason is
	  that there is no way to get the array element type in JDBC.

		* For PostgreSQL, because such information can be easily guessed, it
		  is supported for some well known types.

	* `Struct <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__
	  is exported as array of string types.

		* For Teradata,
		  `PERIOD <https://info.teradata.com/HTMLPubs/DB_TTU_16_00/index.html#page/SQL_Reference%2FB035-1143-160K%2Fphj1472241382702.html%23>`__
		  data types, which are transmitted as Struct types,
		  are converted into formats that matches their BTEQ output formats.

		* For PostgreSQL, the driver reports Struct type even though the data
		  is actually string.  Jaqy had a specific workaround for this
		  inconsistency.

	* For types not listed in the above table, they are stored as STRING.  AVRO
	  exporter relies on the toString() function of the object retrieved by the
	  JDBC driver to obtain the output.  There is no guarantee such String
	  representations can be used for import.

Example
*******

.. code-block:: sql

	.export avro myfile.avro
	SELECT * FROM MyTable ORDER BY a;

See Also
********

* `AVRO Specification <http://avro.apache.org/docs/current/spec.html>`__
