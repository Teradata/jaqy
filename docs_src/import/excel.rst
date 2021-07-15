excel
^^^^^

Excel importer supports schema discovery.

Note that while Excel exporter supports swap option, the importer does not
support it.

The expectation is that the table should be at the top left corner.  If
there is a header row, then that must be at row 1.

Options
*******

.. code-block:: text

	  -d,--date <arg>        imports the specified the column as date
	  -h,--header            indicates that there is a header row/column
	  -i,--index <arg>       specifies the worksheet to import from using index
	  -n,--name <arg>        specifies the worksheet to import from using name
	  -s,--timestamp <arg>   imports the specified the column as timestamp
	  -t,--time <arg>        imports the specified the column as time

Date, Time and Timestamp Handling
*********************************

Because Excel stores DATE, TIME, TIMESTAMP values as doubles, Excel importer
requires user to specify which columns are Date/Time/Timestamp values.

Example
*******

Import an Excel file with first row as the header.

.. code-block:: sql

	CREATE TABLE MyTable(a INTEGER, b INTEGER);
	.import excel -h data/file4.xlsx
	INSERT INTO MyTable VALUES (?, ?,);

Import an Excel file with 2nd column as DATE, 3rd column as TIME, and 4th
column as TIMESTAMP.

.. code-block:: sql

	.import excel -h -d1 -t2 -s3 data/file4.xlsx
	.importtable -c MyTable
