.wasb
-----

``.wasb`` sets up necessary configurations needed to access Windows Azure
Blob Storage (wasb).

This command requires ``jaqy-azure`` plugin.

Syntax
~~~~~~

.. code-block:: text

	usage: wasb [type] [setting]
	type:
	  account    set the optional account name
	  key        set the access key
	  endpoint   set the end point (for debugging purpose)

Example
~~~~~~~

.. code-block:: text

	.wasb account jaqy
	.wasb key SomeSecretKey
	.wasb endpoint http://127.0.0.1:10000/devstoreaccount1

	.import csv -h wasb://containerName@accountName/myfile
	SELECT * FROM MyTable ORDER BY a;

See Also
~~~~~~~~

* `Path Formats <../path.html>`__
