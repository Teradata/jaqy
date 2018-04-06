csv
^^^

CSV importer supports both positional and name based loading.  The loading
is stream based and thus can be used to load very large files that do not
fit into the memory.

CSV importer also supports schema discovery.

Options
*******

.. code-block:: text

	  -c,--charset <arg>                                    sets the file
	                                                        character set
	  -d,--delimiter <arg>                                  specifies the
	                                                        delimiter
	  -e,--encoding <arg>                                   specifies the external
	                                                        file character set
	  -f,--nafilter                                         enables N/A value
	                                                        filtering
	  -h,--header                                           indicates the file has
	                                                        a header
	  -j,--clob <arg>                                       specifies the external
	                                                        text file column
	  -k,--blob <arg>                                       specifies the external
	                                                        binary file column
	  -p,--precise                                          Obtain precise decimal
	                                                        points if possible.
	                                                        This option is only
	                                                        meaningful in
	                                                        generating a table
	                                                        schema.  By default,
	                                                        floating values are
	                                                        treated as DOUBLE
	                                                        PRECISION.
	  -r,--rowthreshold <arg>                               sets row threshold in
	                                                        schema determination.
	  -t,--type <default | excel | rfc4180 | mysql | tdf>   sets the csv type.
	  -v,--navalues <arg>                                   specifies a comma
	                                                        delimited list of N/A
	                                                        values.  If it is not
	                                                        specified and
	                                                        --nafilter is enabled,
	                                                        then the default list
	                                                        is used.

.. note::

	* Binary types are assumed to be in hexadecimal format.
	* N/A values are converted to NULL.

NULL and N/A Handling
*********************

There are many possible ways to store NULL and N/A values in CSV.  CSV
format provides a ``--nafilter`` and ``--navalues`` options to specify
the list.  If ``--nafilter`` is specified, the default N/A value list
are the following, which
`pandas.read_csv <https://pandas.pydata.org/pandas-docs/stable/generated/pandas.read_csv.html>`__
uses.

		"-1.#IND", "1.#QNAN", "1.#IND", "-1.#QNAN", "#N/A N/A", "#N/A", "N/A", "NA", "#NA", "NULL", "NaN", "-NaN", "nan", "-nan", ""

Schema Detection
****************

CSV importer supports schema discovery which allows
`.importschema <../command/importschema.html>`__ and
`.importtable <../command/importtable.html>`__ to generate database compatible
table information.

The default algorithm for schema detection is the following

* For each column, 1000 non-N/A values are analyzed.  If all the values are
  either fixed length string, or numeric values, the scan stops.
* If any of the analyzed column contains variable length string, the entire
  data file needs to be scanned to determine the maximum string length.

CSV importer scans most CSV files pretty fast even with full file scan.  100MB
can be scanned and imported in a few seconds.  Of course, your mileage may
vary depending on the disk and network speed.

For files over 100GB, you do want to be slightly careful since it can take
hours to import the data.

* Make sure the server has enough storage.
* The default schema detection algorithm works fine for the data.
* Or just manually specify the import INSERT statement.

.. warning::

	For `S3 Path <../path/s3.html>`__, the early scan termination can generate
	warnings.  See
	`abort() <https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/model/S3ObjectInputStream.html#abort-->`__
	of S3ObjectInputStream.

Example
*******

.. code-block:: sql

	CREATE TABLE MyTable(a INTEGER, b INTEGER);
	-- success. update count = 0
	-- 9/11 - 0 ---------------------------------------------------------------
	-- test csv with header
	.import csv -h lib/import1.csv
	-- 9/12 - 0 ---------------------------------------------------------------

	INSERT INTO MyTable VALUES (?, ?);
	Parameter Count                              2
	  Index                                        1
		Type                                         INTEGER
		SQL Type                                     INTEGER
		Java Class                                   java.lang.Integer
		Precision                                    10
		Scale                                        0
		Nullable                                     1
		Signed                                       Y
		Mode                                         IN
	  Index                                        2
		Type                                         INTEGER
		SQL Type                                     INTEGER
		Java Class                                   java.lang.Integer
		Precision                                    10
		Scale                                        0
		Nullable                                     1
		Signed                                       Y
		Mode                                         IN
	-- success. update count = 1
	-- 10/12 - 0 --------------------------------------------------------------

	SELECT * FROM MyTable ORDER BY a;
	-- success --
	A B
	- -
	1 1
	2 2
	3 3
	4 4
	-- activity count = 4
