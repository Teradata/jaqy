/*
 * Copyright (c) 2017-2018 Teradata
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
package com.teradata.jaqy.printer;

import java.io.PrintWriter;
import java.util.HashMap;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;

import org.yuanheng.cookjson.CookJsonGenerator;
import org.yuanheng.cookjson.CookJsonProvider;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyPrinter;
import com.teradata.jaqy.utils.JsonBinaryFormat;
import com.teradata.jaqy.utils.JsonUtils;

/**
 * @author	Heng Yuan
 */
class JsonPrinter implements JaqyPrinter
{
	private final boolean m_pretty;
	private final JsonBinaryFormat m_binaryFormat;

	public JsonPrinter (boolean pretty, JsonBinaryFormat binaryFormat)
	{
		m_pretty = pretty;
		m_binaryFormat = binaryFormat;
	}

	@Override
	public String getName ()
	{
		return "json";
	}

	@Override
	public long print (JaqyResultSet rs, PrintWriter pw, long limit, JaqyInterpreter interpreter) throws Exception
	{
		JsonProvider provider = new CookJsonProvider ();

		HashMap<String, Object> config = new HashMap<String, Object> ();
		config.put (CookJsonProvider.FORMAT, CookJsonProvider.FORMAT_JSON);
		if (m_pretty)
			config.put (JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
		switch (m_binaryFormat)
		{
			case Base64:
				config.put (CookJsonProvider.BINARY_FORMAT, CookJsonProvider.BINARY_FORMAT_BASE64);
				break;
			case Hex:
				config.put (CookJsonProvider.BINARY_FORMAT, CookJsonProvider.BINARY_FORMAT_HEX);
				break;
		}

		CookJsonGenerator g = (CookJsonGenerator)provider.createGeneratorFactory (config).createGenerator (pw);

		long count = JsonUtils.print (interpreter.getGlobals (), g, rs, limit);
		pw.println ();
		return count;
	}

	@Override
	public boolean isForwardOnly ()
	{
		return true;
	}
}
