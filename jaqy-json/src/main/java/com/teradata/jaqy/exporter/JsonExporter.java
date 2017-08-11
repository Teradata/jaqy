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
package com.teradata.jaqy.exporter;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.json.spi.JsonProvider;
import javax.json.stream.JsonGenerator;

import org.yuanheng.cookjson.CookJsonGenerator;
import org.yuanheng.cookjson.CookJsonProvider;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyExporter;
import com.teradata.jaqy.utils.JsonBinaryFormat;
import com.teradata.jaqy.utils.JsonFormat;
import com.teradata.jaqy.utils.JsonUtils;

/**
 * @author	Heng Yuan
 */
class JsonExporter implements JaqyExporter
{
	private final Writer m_out;
	private final OutputStream m_os;
	private final boolean m_pretty;
	private final JsonFormat m_format;
	private final JsonBinaryFormat m_binaryFormat;

	public JsonExporter (OutputStream os, Charset charset, JsonFormat format, boolean pretty, JsonBinaryFormat binaryFormat)
	{
		switch (format)
		{
			case Text:
				m_out = new OutputStreamWriter (os, charset);
				m_os = null;
				break;
			case Bson:
				m_out = null;
				m_os = os;
				break;
			default:
				throw new IllegalArgumentException ("Unknown format.");
		}
		m_pretty = pretty;
		m_format = format;
		m_binaryFormat = binaryFormat;
	}

	@Override
	public String getName ()
	{
		return "json";
	}

	@Override
	public long export (JaqyResultSet rs, Globals globals) throws Exception
	{
		JsonProvider provider = new CookJsonProvider ();
		CookJsonGenerator g = null;

		HashMap<String, Object> config = new HashMap<String, Object> ();
		switch (m_format)
		{
			case Text:
			{
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
				g = (CookJsonGenerator)provider.createGeneratorFactory (config).createGenerator (m_out);
				break;
			}
			case Bson:
				config.put (CookJsonProvider.FORMAT, CookJsonProvider.FORMAT_BSON);
				g = (CookJsonGenerator)provider.createGeneratorFactory (config).createGenerator (m_os);
				break;
		}

		long count = JsonUtils.print (g, rs);
		g.close ();

		return count;
	}
}
