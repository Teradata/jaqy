package com.teradata.jaqy.schema;

import org.junit.Assert;
import org.junit.Test;

public class SchemaUtilsTest
{
	@Test
	public void getQuotedIdentifierTest ()
	{
		Assert.assertEquals ("abc", SchemaUtils.getQuotedIdentifier ("abc", " "));
		Assert.assertEquals ("abc", SchemaUtils.getQuotedIdentifier ("abc", null));
		Assert.assertEquals ("abc", SchemaUtils.getQuotedIdentifier ("abc", ""));

		Assert.assertEquals ("\"abc\"", SchemaUtils.getQuotedIdentifier ("abc", "\""));

		Assert.assertEquals ("\"a\"\"b\"", SchemaUtils.getQuotedIdentifier ("a\"b", "\""));
		Assert.assertEquals ("\"\"\"ab\"\"\"", SchemaUtils.getQuotedIdentifier ("\"ab\"", "\""));
		Assert.assertEquals ("\"\"\"\"", SchemaUtils.getQuotedIdentifier ("\"", "\""));
	}
}
