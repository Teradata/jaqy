pq
^^

This feature requires ``jaqy-avro`` plugin.  It exports
`Apache Parquet <https://parquet.apache.org/documentation/latest/>`__
files.

Unfortunately, due to the fact that parquet has hard code dependency
on hadoop libraries, the size of this plugin is ~20MB.

Options
*******

.. code-block:: text

	  -b,--blocksize <arg>     sets the row group / block size
	  -c,--compression <arg>   sets the compression codec
	  -d,--padding <arg>       sets the maximum padding size
	  -p,--pagesize <arg>      sets the page size
	  -r,--rowcount <arg>      sets the row count limit

Supported Compression Codecs
****************************

+---------------+-----------------+
| Compression   | extension       |
+===============+=================+
| brotli        | .br             |
+---------------+-----------------+
| gzip          | .gz             |
+---------------+-----------------+
| lz4           | .lz4            |
+---------------+-----------------+
| lzo           | .lzo            |
+---------------+-----------------+
| snappy        | .snappy         |
+---------------+-----------------+
| zstd          | .zstd           |
+---------------+-----------------+

* LZ4 compression requires the native hadoop installation.  This is one of
  the things hard coded by the Apache Parquet library.

* LZO compression requires a separate library due to its
  `GPL license <https://www.gnu.org/licenses/gpl-3.0.en.html>`__.
  Please see https://github.com/twitter/hadoop-lzo for the build
  instruction.

* It is possible to specify the compression codec implicitly by using the
  corresponding file extension.

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

	* For the explanation of page size, row group / block size, etc, see
	  `Apache Parquet <https://parquet.apache.org/documentation/latest/>`__.


Example
*******

.. code-block:: sql

	.export pq -myfile.parquet.snappy
	SELECT * FROM MyTable ORDER BY a;

See Also
********

* `Apache Parquet <https://parquet.apache.org/documentation/latest/>`__
