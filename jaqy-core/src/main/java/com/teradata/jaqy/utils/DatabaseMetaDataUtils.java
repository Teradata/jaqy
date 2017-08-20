/*
 * Copyright (c) 2017 Teradata
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
import java.sql.ResultSet;

/**
 * @author	Heng Yuan
 */
public class DatabaseMetaDataUtils
{
	public static String getIsolationLevel (int value)
	{
		switch (value)
		{
			case Connection.TRANSACTION_NONE:
				return "None";
			case Connection.TRANSACTION_READ_COMMITTED:
				return "Read committed";
			case Connection.TRANSACTION_READ_UNCOMMITTED:
				return "Read uncommitted";
			case Connection.TRANSACTION_REPEATABLE_READ:
				return "Repeatable read";
			case Connection.TRANSACTION_SERIALIZABLE:
				return "Serializable";
			default:
				return "Unknown";
		}
	}

	public static String getHoldability (int value)
	{
		switch (value)
		{
			case ResultSet.CLOSE_CURSORS_AT_COMMIT:
				return "Close cursors at commit";
			case ResultSet.HOLD_CURSORS_OVER_COMMIT:
				return "Hold cursors over commit";
			default:
				return "Unknown";
		}
	}
}
