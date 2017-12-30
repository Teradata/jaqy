avro
^^^^

AVRO importer supports both positional and name based loading.

It also supports schema discovery.  The current simple implementation requires
the entire file to be scanned.

.. note::

	* The compression codec is automatically detected.

Example
*******

.. code-block:: sql

	.import avro myfile.avro
	.importtable mytable

See Also
********

* `AVRO Specification <http://avro.apache.org/docs/current/spec.html>`__
