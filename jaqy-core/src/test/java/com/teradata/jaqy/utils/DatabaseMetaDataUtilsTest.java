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

import java.sql.Connection;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author	Heng Yuan
 */
public class DatabaseMetaDataUtilsTest
{
	@Test
	public void testGetIsolationLevel ()
	{
		Assert.assertEquals ("None", DatabaseMetaDataUtils.getIsolationLevel (Connection.TRANSACTION_NONE));
		Assert.assertEquals ("Read committed", DatabaseMetaDataUtils.getIsolationLevel (Connection.TRANSACTION_READ_COMMITTED));
		Assert.assertEquals ("Read uncommitted", DatabaseMetaDataUtils.getIsolationLevel (Connection.TRANSACTION_READ_UNCOMMITTED));
		Assert.assertEquals ("Repeatable read", DatabaseMetaDataUtils.getIsolationLevel (Connection.TRANSACTION_REPEATABLE_READ));
		Assert.assertEquals ("Serializable", DatabaseMetaDataUtils.getIsolationLevel (Connection.TRANSACTION_SERIALIZABLE));
		Assert.assertEquals ("Unknown", DatabaseMetaDataUtils.getIsolationLevel (-1));
	}
}
