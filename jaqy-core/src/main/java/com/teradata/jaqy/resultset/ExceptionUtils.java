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
package com.teradata.jaqy.resultset;

import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * @author	Heng Yuan
 */
class ExceptionUtils
{
	static SQLException getInvalidColumnIndex (int column)
	{
		return new SQLException ("Invalid column index " + column + ".");
	}

	static SQLException getInvalidRow ()
	{
		return new SQLException ("Invalid row.");
	}

	static SQLException getNotImplemented ()
	{
		return new SQLFeatureNotSupportedException ("Not implemented.");
	}

	static SQLException getClosed ()
	{
		return new SQLException ("ResultSet was already closed.");
	}

	static SQLException getCannotCast ()
	{
		return new SQLException ("Cannot cast to the data type requested.");
	}

	static SQLException getIllegalArgument ()
	{
		return new SQLException ("Illegal argument.");
	}
}
