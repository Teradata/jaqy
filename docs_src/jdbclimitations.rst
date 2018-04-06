JDBC Limitations
================

Here are some limitations of JDBC encountered during the implementations
of Jaqy.

32-bit Row Position and Activity Count
--------------------------------------

There are a number of places where the use of 32-bit int that are very limited
in today's Big Data world.

* In `ResultSet <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html>`__

	* `boolean absolute(int row) <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#absolute-int->`__
	* `int getRow() <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#getRow-->`__
	* There are also no mechanisms for obtaining the ResultSet row count
	  without having to either scan the rows or to use the positioning to find
	  out the row count.  In
	  `ResultSet.TYPE_FORWARD_ONLY <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSet.html#TYPE_FORWARD_ONLY>`__,
	  the only way to get the row count is by scanning rows.

* In `Statement <https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html>`__,

	* `int getUpdateCount() <https://docs.oracle.com/javase/8/docs/api/java/sql/Statement.html#getUpdateCount-->`__

Lacking Good Type Information
-----------------------------

The only thing in JDBC that provides type information is
`DatabaseMetadata.getTypeInfo() <https://docs.oracle.com/javase/8/docs/api/java/sql/DatabaseMetaData.html#getTypeInfo-->`__.
Yet it is not all that useful.

* There can be multiple types mapped to a single JDBC type without a way of
  indicating the default one to use.  For instance, in PostgreSQL, both
  ``int4`` and ``serial`` maps to ``Types.INTEGER``.
* For types such as VARCHAR(100) CHARACTER SET LATIN, there are basically no
  way to infer the type name based on the type information.

Poor Array and Struct Type Support
----------------------------------

JDBC has very poor
`Array <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__ and
`Struct <https://docs.oracle.com/javase/8/docs/api/java/sql/Array.html>`__
support.

`ResultSetMetaData <https://docs.oracle.com/javase/8/docs/api/java/sql/ResultSetMetaData.html>`__
and
`ParameterMetaData <https://docs.oracle.com/javase/8/docs/api/java/sql/ParameterMetaData.html>`__
have only the basic type information, without any mechanism obtaining the
element or attribute types.  Needless to say, there is no way to obtain nested
Array or Struct information.

There is also no way to obtain the Struct attributes.
