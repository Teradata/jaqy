WASB Path
^^^^^^^^^

This feature requires ``jaqy-azure`` plugin.

`.wasb <command/wasb.html>`__ command can be used to configure the access
credential if needed.

For importing, wasb

Path Syntax
***********

See this
`blog <https://blogs.msdn.microsoft.com/cindygross/2015/02/04/understanding-wasb-and-hadoop-storage-in-azure/>`__.
Basically, the path syntax is the following

	``wasb://containerName@accountName/filePath``

or with encrypted connection

	``wasbs://containerName@accountName/filePath``

Example
*******

.. code-block:: sql

	-- load jaqy-azure plugin first.
	.load /vagrant/jaqy-azure/target/jaqy-azure-1.1.0.jar

	-- configures a new WASB connection property
	.wasb key somesecretkey
	.wasb account accountName

	-- uses the existing WASB configuration for the account name
	.import csv -h wasb://mycontainer@/csv/test.csv
	.importtable mytable

	-- 
	.import csv -h wasb://mycontainer@/csv/test.csv
	.importtable mytable

See Also
********

* `.wasb <command/wasb.html>`__ command
* `Introduction to Azure Blob storage <https://docs.microsoft.com/en-us/azure/storage/blobs/storage-blobs-introduction>`__
