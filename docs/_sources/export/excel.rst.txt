excel
^^^^^

Excel exporter has the following features

* Column names are exported as the first row / column.  Freeze pane
  is enabled.
* DECIMAL formats are kept.
* DATE / TIME / TIMESTAMP values are formatted using ANSI format.  Subseconds are not displayed.

Options
*******

.. code-block:: text

  -n,--name <arg>   specifies the worksheet name
  -s,--swap         dumps the data horizontally

.. note::

	* Most behaviors are similar to that of `csv <csv.html>`__ exporter.
	* Dumping the data horizontally has a limit of 65535 columns.

Example
*******

.. code-block:: sql

	.export excel -n MyTable mytable.xlsx
	SELECT * FROM MyTable ORDER BY 1;
