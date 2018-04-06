lobcachesize
------------

``lobcachesize`` setting sets the client-side ResultSet LOB cache size.  It
affects CLOB / BLOB / XML types.  The cache is mainly used for sorting
purposes.

This is an interpreter level setting.

By default, it is 4096.

Syntax
~~~~~~

.. code-block:: text

	usage: .set lobcachesize [size]

Without the size argument, it displays the interpreter's current LOB cache
size.

Example
~~~~~~~

.. code-block:: sql

	.set lobcachesize 64000
	.sort 2, 3
	SELECT * FROM XmlTable ORDER BY a;

See Also
~~~~~~~~

* `.sort <../command/sort.html>`__
