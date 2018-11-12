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
package com.teradata.jaqy.importer;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.Date;
import java.sql.Types;
import java.util.HashMap;
import java.util.logging.Level;

import javax.json.JsonNumber;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonValue.ValueType;
import javax.json.spi.JsonProvider;
import javax.json.stream.JsonParser;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.yuanheng.cookjson.CookJsonParser;
import org.yuanheng.cookjson.CookJsonProvider;
import org.yuanheng.cookjson.value.CookJsonArray;
import org.yuanheng.cookjson.value.CookJsonBinary;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyPreparedStatement;
import com.teradata.jaqy.interfaces.JaqyImporter;
import com.teradata.jaqy.schema.ParameterInfo;
import com.teradata.jaqy.schema.SchemaInfo;
import com.teradata.jaqy.utils.JsonBinaryFormat;
import com.teradata.jaqy.utils.JsonFormat;
import com.teradata.jaqy.utils.JsonUtils;
import com.teradata.jaqy.utils.TypesUtils;

/**
 * @author	Heng Yuan
 */
class JsonImporter implements JaqyImporter
{
	private final Globals m_globals;
	private final CookJsonParser m_parser;
	private boolean m_end;
	private final JsonBinaryFormat m_binaryFormat;
	private JaqyConnection m_conn;

	private int m_depth;
	private String m_rowExp;
	private JsonValueVisitor[] m_vvs;
	private JsonEventVisitor m_v;

	private boolean m_rowEnd;
	private JsonRowEndListener m_rowEndListener = new JsonRowEndListener ()
	{
		@Override
		public void setRowEnd ()
		{
			m_rowEnd = true;
		}
	};

	public JsonImporter (Globals globals, JaqyConnection conn, InputStream is, Charset charset, JsonFormat format, JsonBinaryFormat binaryFormat, String rowExp, boolean rootAsArray) throws IOException
	{
		m_globals = globals;
		m_conn = conn;
		m_binaryFormat = binaryFormat;
		m_rowExp = rowExp;

		JsonProvider provider = new CookJsonProvider ();

		HashMap<String, Object> config = new HashMap<String, Object> ();
		switch (format)
		{
			case Text:
			{
				config.put (CookJsonProvider.FORMAT, CookJsonProvider.FORMAT_JSON);
				break;
			}
			case Bson:
				config.put (CookJsonProvider.FORMAT, CookJsonProvider.FORMAT_BSON);
				if (rootAsArray)
					config.put (CookJsonProvider.ROOT_AS_ARRAY, Boolean.TRUE);
				break;
		}

		CookJsonParser p = (CookJsonParser) provider.createParserFactory (config).createParser (is, charset);
		m_parser = p;
	}

	@Override
	public void close () throws IOException
	{
		m_parser.close ();
		m_conn = null;
	}

	@Override
	public String getName ()
	{
		return "json";
	}

	@Override
	public SchemaInfo getSchema ()
	{
		return null;
	}

	@Override
	public boolean next () throws IOException
	{
		if (m_end)
			return false;

		for (JsonValueVisitor v : m_vvs)
		{
			v.init ();
		}

		CookJsonParser p = m_parser;
		while (p.hasNext ())
		{
			JsonParser.Event e = p.next ();
			switch (e)
			{
				case START_OBJECT:
				case START_ARRAY:
					m_v.visit (e, p, m_depth);
					++m_depth;
					break;
				case END_OBJECT:
				case END_ARRAY:
					--m_depth;
					m_v.visit (e, p, m_depth);
					break;
				default:
					m_v.visit (e, p, m_depth);
					break;
			}

			if (m_rowEnd)
			{
				m_rowEnd = false;
				return true;
			}
		}

		m_end = true;
		m_parser.close ();
		return false;
	}

	@Override
	public void setParameters (String[] exps) throws Exception
	{
		if (exps == null)
		{
			throw new IOException ("json data has to be accessed via field.");
		}
		m_vvs = new JsonValueVisitor[exps.length];
		m_v = JsonExpFactory.createVisitor (m_rowExp, exps, m_vvs, m_rowEndListener, false);
		m_depth = 0;
	}

	@Override
	public Object getObject (int index, ParameterInfo paramInfo, JaqyInterpreter interpreter) throws IOException, DecoderException
	{
		JsonValue v = (JsonValue)m_vvs[index].getValue ();
		if (v == null || v.getValueType () == ValueType.NULL) 
			return null;
		switch (paramInfo.type)
		{
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			{
				if (v.getValueType () == ValueType.TRUE)
					return 1;
				else if (v.getValueType () == ValueType.FALSE)
					return 0;

				if (v instanceof JsonString)
				{
					return Integer.parseInt (((JsonString)v).getString ());
				}
				else if (v instanceof JsonNumber)
				{
					return ((JsonNumber)v).intValue ();
				}
				break;
			}
			case Types.BIGINT:
			{
				if (v.getValueType () == ValueType.TRUE)
					return BigInteger.ONE;
				else if (v.getValueType () == ValueType.FALSE)
					return BigInteger.ZERO;

				if (v instanceof JsonString)
				{
					return Integer.parseInt (((JsonString)v).getString ());
				}
				else if (v instanceof JsonNumber)
				{
					return ((JsonNumber)v).bigIntegerValue ();
				}
				break;
			}
			case Types.FLOAT:
			{
				if (v instanceof JsonString)
				{
					return Float.parseFloat (((JsonString)v).getString ());
				}
				else if (v instanceof JsonNumber)
				{
					return ((JsonNumber)v).doubleValue ();
				}
				break;
			}
			case Types.DOUBLE:
			{
				if (v instanceof JsonString)
				{
					return Double.parseDouble (((JsonString)v).getString ());
				}
				else if (v instanceof JsonNumber)
				{
					return ((JsonNumber)v).doubleValue ();
				}
				break;
			}
			case Types.DECIMAL:
			{
				if (v instanceof JsonString)
				{
					return new BigDecimal (((JsonString)v).getString ());
				}
				else if (v instanceof JsonNumber)
				{
					return ((JsonNumber)v).bigDecimalValue ();
				}
				break;
			}
			case Types.DATE:
			{
				if (v instanceof JsonString)
				{
					return Date.valueOf (((JsonString)v).getString ());
				}
				break;
			}
			case Types.CHAR:
			case Types.VARCHAR:
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.LONGVARCHAR:
			case Types.CLOB:
			case Types.NCLOB:
			{
				return JsonUtils.toString (v);
			}
			case Types.BINARY:
			case Types.VARBINARY:
			case Types.LONGVARBINARY:
			case Types.BLOB:
			{
				if (v instanceof CookJsonBinary)
				{
					return ((CookJsonBinary)v).getBytes ();
				}
				if (v instanceof JsonString)
				{
					String str = ((JsonString)v).getString ();
					switch (m_binaryFormat)
					{
						case Base64:
							return Base64.decodeBase64 (str);
						case Hex:
							return Hex.decodeHex (str.toCharArray ());
					}
				}
				break;
			}
			case Types.ARRAY:
			{
				if (v instanceof CookJsonArray)
				{
					try
					{
						int size = ((CookJsonArray)v).size ();
						Object[] elements = new Object[size];
						for (int i = 0; i < size; ++i)
						{
							JsonValue av = ((CookJsonArray)v).get (i);
							elements[i] = JsonUtils.toString (av);
						}
						return m_conn.getHelper ().createArrayOf (paramInfo, elements);
					}
					catch (Exception ex)
					{
						m_globals.log (Level.INFO, ex);
					}
				}
				// Okay, we give up, just return some kind of string
				return JsonUtils.toString (v);
			}
			case Types.STRUCT:
			{
				if (v instanceof CookJsonArray)
				{
					try
					{
						int size = ((CookJsonArray)v).size ();
						Object[] elements = new Object[size];
						for (int i = 0; i < size; ++i)
						{
							JsonValue av = ((CookJsonArray)v).get (i);
							elements[i] = JsonUtils.toString (av);
						}
						return m_conn.getHelper ().createStruct (paramInfo, elements);
					}
					catch (Exception ex)
					{
						m_globals.log (Level.INFO, ex);
					}
				}
				// Okay, we give up, just return some kind of string
				return JsonUtils.toString (v);
			}
			default:
			{
				if (v instanceof CookJsonBinary)
				{
					return ((CookJsonBinary)v).getBytes ();
				}
				if (v instanceof JsonNumber)
				{
					return ((JsonNumber)v).bigDecimalValue ();
				}
				return JsonUtils.toString (v);
			}
		}
		throw new IOException ("Unable to convert from " + v.getValueType () + " to " + TypesUtils.getTypeName (paramInfo.type));
	}

	@Override
	public void setNull (JaqyPreparedStatement stmt, int column, ParameterInfo paramInfo) throws Exception
	{
		stmt.setNull (column, paramInfo.type, paramInfo.typeName);
	}
}
