.typemap
--------

``.typemap`` displays the mapping of JDBC to SQL types.

Syntax
~~~~~~

.. code-block:: text

	usage: usage: .typemap [options]
	options:
	  -i,--import   display import type map

The type map displayed by default is the type map used to describe database
table column types, which is used by `.desc <desc.html>`__ when there are no
conveninet way of such info using SQL commands.

The import type map is the preferred types to be used when importing data
into the database using `.import <import.html>`__.

Example
~~~~~~~

.. code-block:: sql

	-- display the default type map 
	.typemap

	-- display import type map
	.typemap -i
