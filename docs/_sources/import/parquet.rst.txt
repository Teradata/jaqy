pq
^^

This feature requires ``jaqy-avro`` plugin.  It imports
`Apache Parquet <https://parquet.apache.org/documentation/latest/>`__
files.

This importer supports both positional and name based loading.

It also supports schema discovery when there is at least a single record.
The current simple implementation requires the entire file to be scanned.

.. note::

	* The compression codec is automatically detected.  LZ4 and LZO
	  may not be supported.  See `Parquet exporter <../export/parquet.html>`__
	  for more information.
	* The input path must be a file path.

Example
*******

.. code-block:: sql

	.import pq myfile.parquet.snappy
	.importtable MyTable

See Also
********

* `Apache Parquet <https://parquet.apache.org/documentation/latest/>`__
