/*
 * Copyright (c) 2017-2021 Teradata
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
package com.teradata.jaqy.exporter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;

import org.yuanheng.cookjson.CookJsonGenerator;
import org.yuanheng.cookjson.CookJsonProvider;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.utils.JsonUtils;

/**
 * @author	Heng Yuan
 */
class JsonExporter implements JaqyExporter
{
	private final JsonExporterOptions m_options;
	private Writer m_out;
	private OutputStream m_os;

	public JsonExporter (OutputStream os, JsonExporterOptions options)
	{
		m_options = options;
		switch (m_options.format)
		{
			case Text:
				m_out = new OutputStreamWriter (os, m_options.charset);
				m_os = null;
				break;
			case Bson:
				m_out = null;
				m_os = os;
				break;
		}
	}

	@Override
	public String getName ()
	{
		return "json";
	}

	@Override
	public long export (JaqyResultSet rs, JaqyInterpreter interpreter) throws Exception
	{
		JsonProvider provider = new CookJsonProvider ();
		CookJsonGenerator g = null;

		HashMap<String, Object> config = new HashMap<String, Object> ();
		switch (m_options.format)
		{
			case Text:
			{
				config.put (CookJsonProvider.FORMAT, CookJsonProvider.FORMAT_JSON);
				if (m_options.pretty)
					config.put (JsonGenerator.PRETTY_PRINTING, Boolean.TRUE);
				switch (m_options.binaryFormat)
				{
					case Base64:
						config.put (CookJsonProvider.BINARY_FORMAT, CookJsonProvider.BINARY_FORMAT_BASE64);
						break;
					case Hex:
						config.put (CookJsonProvider.BINARY_FORMAT, CookJsonProvider.BINARY_FORMAT_HEX);
						break;
				}
				g = (CookJsonGenerator)provider.createGeneratorFactory (config).createGenerator (m_out);
				break;
			}
			case Bson:
				config.put (CookJsonProvider.FORMAT, CookJsonProvider.FORMAT_BSON);
				g = (CookJsonGenerator)provider.createGeneratorFactory (config).createGenerator (m_os);
				break;
		}

		long count = JsonUtils.print (interpreter.getGlobals (), g, rs, 0);
		g.close ();

		return count;
	}

	@Override
	public void close ()
	{
		if (m_out != null)
		{
			try
			{
				m_out.close ();
				m_out = null;
			}
			catch (Exception ex)
			{
			}
		}
		if (m_os != null)
		{
			try
			{
				m_os.close ();
				m_os = null;
			}
			catch (Exception ex)
			{
			}
		}
	}
}
