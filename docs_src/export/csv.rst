csv
^^^

Options
*******

.. code-block:: text

	  -c,--charset <arg>                                    sets the file
	                                                        character set
	  -d,--delimiter <arg>                                  specifies the
	                                                        delimiter
	  -e,--encoding <arg>                                   specifies the external
	                                                        file character set
	  -f,--file <arg>                                       specifies the external
	                                                        file column
	  -n,--name <arg>                                       specifies the external
	                                                        file name pattern
	  -t,--type <default | excel | rfc4180 | mysql | tdf>   sets the csv type.

.. note::

	* Binary types are converted to hexadecimal format.
	* NULL values are convert to empty strings.
	* `Array <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__ and
	  `Struct <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__
	  types are converted to string simply using toString()
	  functions of the object.

		* For Teradata,
		  `PERIOD <https://info.teradata.com/HTMLPubs/DB_TTU_16_00/index.html#page/SQL_Reference%2FB035-1143-160K%2Fphj1472241382702.html%23>`__
		  data types, which are transmitted as Struct types,
		  are converted into formats that matches their BTEQ output formats.

	* In most cases, CSV exporter relies on the toString() function of the
	  object retrieved by the JDBC driver to obtain the output.  There is
	  no guarantee such String representations can be used for import.

External File Name Pattern
**************************

The file name pattern basically uses
`printf <https://en.wikipedia.org/wiki/Printf_format_string>` format.
Thus a name pattern of ``img%02d.png`` would generate ``img01.png``,
``img02.png``, etc.  The numbering starts from 1.

Multiple columns can share the same naming pattern.  For instance, the
following example have two columns share the same naming pattern and counter.

.. code-block:: sql

	.export csv -n %02d.bin -f 2 -f 3 file1.csv
	SELECT * FROM MyTable ORDER BY a;

To specify different naming patterns for different columns, just specify
a new naming pattern before specifying the ``-f`` option.

.. code-block:: sql

	.export csv -n %02d.bin -f2 -n img%04d.png -f3 file1.csv
	SELECT * FROM MyTable ORDER BY a;

External File Character Set
***************************

Much like external file name pattern, ``-e`` option needs to be specified
before ``-f`` option to be effective.  In the following example, column 2
is exported in UTF-8 character set, while column 3 is exported in UTF-16LE
character set.

.. code-block:: sql

	.export csv -e UTF-8 -f2 -e UTF-16LE -f3 file1.csv
	SELECT * FROM MyTable ORDER BY a;

Example
*******

.. code-block:: sql

	.format csv
	-- 8/8 - 0 ----------------------------------------------------------------
	SELECT * FROM MyTable ORDER BY a;
	-- success --
	a,b,c
	1,abc,def
	2,john,doe
	3,"a""b","c""d"
	4,"a,b","c,d"
	5,a'b,c'd
	6,"a'"",b","c'"",d"
	7,a	b,"c,d"
	-- activity count = 7
	-- 9/8 - 0 ----------------------------------------------------------------
	.format csv -type default -d |
	-- 9/9 - 0 ----------------------------------------------------------------
	SELECT * FROM MyTable ORDER BY a;
	-- success --
	a|b|c
	1|abc|def
	2|john|doe
	3|"a""b"|"c""d"
	4|a,b|c,d
	5|a'b|c'd
	6|"a'"",b"|"c'"",d"
	7|a	b|c,d
	-- activity count = 7
