package com.teradata.jaqy.schema;

import java.sql.Types;

import org.junit.Assert;
import org.junit.Test;

public class TypeMapTest
{
	@Test
	public void switchCharTypeTest ()
	{
		Assert.assertEquals (Types.CHAR, TypeMap.switchCharType (Types.NCHAR));
		Assert.assertEquals (Types.NCHAR, TypeMap.switchCharType (Types.CHAR));

		Assert.assertEquals (Types.VARCHAR, TypeMap.switchCharType (Types.NVARCHAR));
		Assert.assertEquals (Types.NVARCHAR, TypeMap.switchCharType (Types.VARCHAR));

		Assert.assertEquals (Types.LONGVARCHAR, TypeMap.switchCharType (Types.LONGNVARCHAR));
		Assert.assertEquals (Types.LONGNVARCHAR, TypeMap.switchCharType (Types.LONGVARCHAR));

		Assert.assertEquals (Types.CLOB, TypeMap.switchCharType (Types.NCLOB));
		Assert.assertEquals (Types.NCLOB, TypeMap.switchCharType (Types.CLOB));

		Assert.assertEquals (Types.BLOB, TypeMap.switchCharType (Types.BLOB));
	}

	@Test
	public void isSameTypeTest ()
	{
		Assert.assertTrue (TypeMap.isSameType ("CHAR", "char"));

		Assert.assertFalse (TypeMap.isSameType ("CHAR", "VARCHAR"));
		Assert.assertTrue (TypeMap.isSameType ("VARCHAR", "CHAR"));

		Assert.assertTrue (TypeMap.isSameType ("VARCHAR(100)", "VARCHAR"));
		Assert.assertTrue (TypeMap.isSameType ("VARCHAR(100) CHARACTER SET LATIN", "VARCHAR"));
		Assert.assertTrue (TypeMap.isSameType ("VARCHAR(100) CHARACTER SET LATIN", "VARCHAR CHARACTER SET LATIN"));
		Assert.assertTrue (TypeMap.isSameType ("CHAR(100) FOR BIT DATA", "CHAR() FOR BIT DATA"));
	}
}
