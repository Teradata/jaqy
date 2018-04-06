File Path
^^^^^^^^^

File path supports typical file paths started with ``/`` or ``\``.

To make the script to be more portable, both separators are recognized on
Windows and Linux.  Likewise for both ``:`` and ``;`` path separators.

On Windows, ``C:\\`` style path are also recognized.

File URIs such as ``file://`` are recognized as well.

Example
*******

.. code-block:: sql

	-- load a driver initiation and connection script
	.run ../common/mysql_setup.sql

	-- mysql_setup.sql
	-- load the driver relative to mysql_setup.sql
	.@classpath mysql ../drivers/mysql-connector-java-5.1.39-bin.jar
	.open -u root mysql://localhost/?useUnicode=true&characterEncoding=utf-8
