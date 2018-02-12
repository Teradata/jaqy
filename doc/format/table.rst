table
^^^^^

Options
*******

.. code-block:: text

	  -a,--autosize <on | off>     turns auto column size determination on / off.
	  -b,--border <on | off>       turns border on / off.
	  -c,--columnthreshold <arg>   sets column size threshold.  If a column size
	                               is less than the threshold, then no auto size.
	  -m,--maxsize <arg>           sets the maximum size of a column.
	  -r,--rowthreshold <arg>      sets row threshold.  Scan up to this number of
	                               rows to determine the size of the column.

``-a`` option requires the ResultSet to be rewindable in order to scan all
the rows.  If it is
`TYPE_FORWARD_ONLY <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#TYPE_FORWARD_ONLY>`__
(e.g. SQLite ResultSet), then an in-memory copy of the ResultSet is made.
This action requires a lot of memory.  When this option is on,
``--columnthreshold`` and ``--maxsize`` options are ignored.  This option
is on by default.

``-c`` option default value is 1.

``-m`` option default value is 100.

``-r`` option default value is 1000.

.. note::

	* Binary types are converted to hexadecimal format.
	* NULL values are represented using ``?``, which matches Teradata BTEQ client
	  default handling.
	* `Array <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__ and
	  `Struct <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__
	  types are converted to string simply using toString()
	  functions of the object.

		* For Teradata, PERIOD data types, which are transmitted as Struct types,
		  are detected and converted into formats that matches their BTEQ output
		  formats.

.. warning::

	``table`` should not be used to display large amount of data.  See
	`Large Data Set Handling <../largedataset.html>`__ for more information.

Example
*******

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
	-- 5/12 - 0 ---------------------------------------------------------------
	.format table -b on
	-- 5/13 - 0 ---------------------------------------------------------------
	SELECT * FROM NumTable ORDER BY t1;
	-- success --
	+--------+------------+-------------+---------+
	|     T1 |         T2 |          T3 |      T4 |
	+--------+------------+-------------+---------+
	| -12345 | -123456789 | -1234567890 | -1234.5 |
	|  12345 |  123456789 |  1234567890 |  1234.5 |
	+--------+------------+-------------+---------+
	-- activity count = 2
