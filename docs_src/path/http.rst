HTTP Path
^^^^^^^^^

This is a very simple implementation of getting data from ``http://`` or
``https://`` URLs.

Uploading data to a HTTP URL is not supported.

Example
*******

.. code-block:: sql

	-- loading CSV data from a https URL
	.import csv -h -f https://introcs.cs.princeton.edu/java/data/ip-by-country.csv
	.importschema
	.importtable mytable
