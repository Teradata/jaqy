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
	  account    sets the default account name
	  container  sets the default container name
	  key        sets the access key
	  endpoint   sets the end point (for testing)
	  create     creates a container if it does not exist.
	  delete     deletes a container if it exists.
	  list       lists blobs in a container
	  remove     removes a blob

Example
~~~~~~~

.. code-block:: text

	.wasb account myaccount
	.wasb container mycontainer
	.wasb key SomeSecretKey
	.wasb endpoint http://127.0.0.1:10000/devstoreaccount1

	-- create a new WASB container
	.wasb create mycontainer

	-- export CSV to myfile in mycontainer
	.export csv wasb://mycontainer@myaccount/myfile
	SELECT * FROM MyTable ORDER BY a;

	-- list blobs in mycontainer
	.wasb list mycontainer

	-- import CSV from default container in default account
	.import csv -h wasb:///myfile.csv
	INSERT INTO MyTable VALUES ({{a}}, {{b}}, {{c}});

	-- remove a WASB blob
	.wasb remove wasb://mycontainer@myaccount/myfile

	-- delete a WASB container
	.wasb delete mycontainer

See Also
~~~~~~~~

* `Path Formats <../path.html>`__
