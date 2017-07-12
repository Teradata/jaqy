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
package com.teradata.jaqy.typeprinter;

import java.sql.Types;
import java.util.HashMap;

/**
 * @author	Heng Yuan
 */
public class TypePrinterRegistry
{
	private final static HashMap<Integer,TypePrinter> s_typePrinterMap = new HashMap<Integer,TypePrinter> ();

	static
	{
		s_typePrinterMap.put (Types.CHAR, StringTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.VARCHAR, StringTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.LONGNVARCHAR, StringTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.NCHAR, StringTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.NVARCHAR, StringTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.LONGNVARCHAR, StringTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.CLOB, ClobTypePrinter.getInstance ());

		s_typePrinterMap.put (Types.BINARY, BinaryTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.VARBINARY, BinaryTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.LONGVARBINARY, BinaryTypePrinter.getInstance ());
		s_typePrinterMap.put (Types.BLOB, BinaryTypePrinter.getInstance ());
	}

	public static TypePrinter getTypePrinter (int type)
	{
		TypePrinter printer = s_typePrinterMap.get (type);
		if (printer == null)
			return StringTypePrinter.getInstance ();
		return printer;
	}
}
