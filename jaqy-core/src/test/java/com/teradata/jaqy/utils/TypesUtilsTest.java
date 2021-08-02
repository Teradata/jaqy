/*
 * Copyright (c) 2021 Teradata
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.teradata.jaqy.utils;

import java.io.IOException;
import java.sql.Types;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author	Heng Yuan
 */
public class TypesUtilsTest
{
	@Test
	public void getTypeNameTest () throws IOException
	{
		Assert.assertEquals ("CHAR", TypesUtils.getTypeName (Types.CHAR));
		Assert.assertEquals ("REF", TypesUtils.getTypeName (Types.REF));

		Assert.assertEquals ("Type 65535", TypesUtils.getTypeName (65535));
	}

	@Test
	public void isStringTest () throws IOException
	{
		Assert.assertEquals (true, TypesUtils.isString (Types.CHAR));
		Assert.assertEquals (true, TypesUtils.isString (Types.VARCHAR));
		Assert.assertEquals (true, TypesUtils.isString (Types.LONGVARCHAR));
		Assert.assertEquals (true, TypesUtils.isString (Types.CLOB));
		Assert.assertEquals (true, TypesUtils.isString (Types.NCHAR));
		Assert.assertEquals (true, TypesUtils.isString (Types.NVARCHAR));
		Assert.assertEquals (true, TypesUtils.isString (Types.LONGNVARCHAR));
		Assert.assertEquals (true, TypesUtils.isString (Types.NCLOB));

		Assert.assertEquals (false, TypesUtils.isString (Types.INTEGER));
	}

	@Test
	public void isNumberTest () throws IOException
	{
		Assert.assertEquals (true, TypesUtils.isNumber (Types.TINYINT));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.SMALLINT));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.INTEGER));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.BIGINT));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.REAL));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.FLOAT));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.DOUBLE));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.DECIMAL));
		Assert.assertEquals (true, TypesUtils.isNumber (Types.NUMERIC));

		Assert.assertEquals (false, TypesUtils.isNumber (Types.BOOLEAN));
	}

	@Test
	public void isBinaryTest () throws IOException
	{
		Assert.assertEquals (true, TypesUtils.isBinary (Types.BINARY));
		Assert.assertEquals (true, TypesUtils.isBinary (Types.VARBINARY));
		Assert.assertEquals (true, TypesUtils.isBinary (Types.LONGVARBINARY));
		Assert.assertEquals (true, TypesUtils.isBinary (Types.BLOB));

		Assert.assertEquals (false, TypesUtils.isBinary (Types.INTEGER));
	}
}
