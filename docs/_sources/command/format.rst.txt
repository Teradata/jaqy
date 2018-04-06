.format
-------

This command sets the display format for query results.

By default, the Jaqy uses table format with ``-autosize on`` and no border.

Syntax
~~~~~~

.. code-block:: text

	usage: .format [type] [type options]
	type:
	  csv
	  json
	  table

	csv type options:
	  -d,--delimiter <arg>                                  specifies the
	                                                        delimiter
	  -t,--type <default | excel | rfc4180 | mysql | tdf>   sets the csv type.

	json type options:
	  -b,--binary <base64 | hex>   sets the binary format.
	  -p,--pretty <on | off>       turns pretty print on / off.

	table type options:
	  -a,--autosize <on | off>     turns auto column size determination on / off.
	  -b,--border <on | off>       turns border on / off.
	  -c,--columnthreshold <arg>   sets column size threshold.  If a column size
	                               is less than the threshold, then no auto size.
	  -m,--maxsize <arg>           sets the maximum size of a column.
	  -r,--rowthreshold <arg>      sets row threshold.  Scan up to this number of
	                               rows to determine the size of the column.

For more detailed information on each format type, please see
`Display Format <../format.html>`__.

Example
~~~~~~~

.. code-block:: sql

	.format table -a off
	-- 3/11 - 0 ---------------------------------------------------------------
	SELECT * FROM NumTable ORDER BY t1;
	-- success --
	    T1          T2                   T3                       T4
	------ ----------- -------------------- ------------------------
	-12345  -123456789          -1234567890                  -1234.5
	 12345   123456789           1234567890                   1234.5
	-- activity count = 2
	-- 4/11 - 0 ---------------------------------------------------------------
	.format table -a on
	-- 4/12 - 0 ---------------------------------------------------------------
	SELECT * FROM NumTable ORDER BY t1;
	-- success --
	    T1         T2          T3      T4
	------ ---------- ----------- -------
	-12345 -123456789 -1234567890 -1234.5
	 12345  123456789  1234567890  1234.5
	-- activity count = 2
