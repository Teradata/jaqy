WASB Path
^^^^^^^^^

This feature requires ``jaqy-azure`` plugin.

`.wasb <../command/wasb.html>`__ command can be used to configure the access
credential if needed.

For importing, any types of blobs can be read.  For exporting, block blobs
are used for output.  Blob encryption is not supported.

Path Syntax
***********

See this
`blog <https://blogs.msdn.microsoft.com/cindygross/2015/02/04/understanding-wasb-and-hadoop-storage-in-azure/>`__.
Basically, the path syntax is the following

	``wasb://containerName@accountName/filePath``

or

	``wasbs://containerName@accountName/filePath``

They are handled the same.

Also, ``.blob.core.windows.net`` is optional in Jaqy implementation.  Thus
``wasb://containerName@accountName/filePath`` is the same as
``wasb://containerName@accountName.blob.core.windows.net/filePath``.

Example
*******

.. code-block:: sql

	-- load jaqy-azure plugin first.
	.load /vagrant/jaqy-azure/target/jaqy-azure-1.1.0.jar

	-- configures a WASB access key
	.wasb key somesecretkey

	-- explicits specify the container and account names
	.import csv -h wasb://mycontainer@myaccount/myfolder/test.csv
	.importtable mytable

	-- uses the WASB configuration for the account name
	.wasb account myaccount
	.import csv -h wasb://mycontainer@/myfolder/test.csv
	.importtable mytable

	-- uses the WASB configuration for the container name as well
	.wasb container mycontainer
	.import csv -h wasb:///myfolder/test.csv
	.importtable mytable

See Also
********

* `.wasb <../command/wasb.html>`__ command
* `Introduction to Azure Blob storage <https://docs.microsoft.com/en-us/azure/storage/blobs/storage-blobs-introduction>`__
