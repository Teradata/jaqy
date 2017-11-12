package com.teradata.jaqy.utils;

import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.yuanheng.cookjson.CookJsonParser;
import org.yuanheng.cookjson.TextJsonParser;

import com.teradata.jaqy.HelperManager;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.helper.DefaultHelperFactory;
import com.teradata.jaqy.schema.TypeInfo;

public class HelperConfigUtils
{
	private static void loadType (Map<Integer, TypeInfo> map, JsonObject v)
	{
		TypeInfo typeInfo = new TypeInfo ();
		typeInfo.type = v.getInt ("type");
		typeInfo.typeName = v.getString ("name");
		if (typeInfo.typeName.indexOf ('{') >= 0)
		{
			typeInfo.maxPrecision = v.getInt ("maxPrecision");
			typeInfo.typeFormat = new MessageFormat (typeInfo.typeName);
		}
		map.put (typeInfo.type, typeInfo);
	}

	private static HashMap<Integer, TypeInfo> getTypeMap (JsonArray v)
	{
		if (v == null)
			return null;
		int size = v.size ();
		HashMap<Integer, TypeInfo> map = new HashMap<Integer, TypeInfo> ();
		for (int i = 0; i < size; ++i)
		{
			loadType (map, (JsonObject)v.getJsonObject (i));
		}
		return map;
	}

	private static JdbcFeatures getFeatures (JsonObject v)
	{
		if (v == null)
			return null;
		JdbcFeatures features = new JdbcFeatures ();
		if (!v.getBoolean ("schema", false))
		{
			features.noSchema = true;
		}
		if (!v.getBoolean ("catalog", false))
		{
			features.noCatalog = true;
		}
		return features;
	}

	private static SimpleQuery getSimpleQuery (JsonObject v, String key)
	{
		v = v.getJsonObject (key);
		if (v == null)
			return null;
		String sql = v.getString ("sql");
		int field = v.getInt ("field");
		return new SimpleQuery (sql, field);
	}

	private static void updateMap (HashMap<String, SimpleQuery> map, String key, JsonObject v)
	{
		SimpleQuery q = getSimpleQuery (v, key);
		if (q != null)
			map.put (key, q);
	}

	public static void load (HelperManager manager, JsonObject v) throws IOException
	{
		String protocol = v.getString ("protocol");
		if (protocol == null || protocol.length () == 0)
			throw new IOException ("Invalid protocol name.");
		DefaultHelperFactory factory = (DefaultHelperFactory) manager.getHelperFactory (protocol);
		JdbcFeatures features = getFeatures (v.getJsonObject ("features"));
		HashMap<Integer, TypeInfo> typeMap = getTypeMap (v.getJsonArray ("typeMap"));
		HashMap<String, SimpleQuery> sqlMap = new HashMap<String, SimpleQuery> ();
		updateMap (sqlMap, "catalogSQL", v);
		updateMap (sqlMap, "schemaSQL", v);
		updateMap (sqlMap, "tableSchemaSQL", v);
		updateMap (sqlMap, "tableColumnSQL", v);
		factory.setSQLMap (sqlMap);
		if (features != null)
			factory.setFeatures (features);
		if (typeMap != null)
			factory.setCustomTypeMap (typeMap);
	}

	public static void load (HelperManager manager, JsonArray v) throws IOException
	{
		int size = v.size ();
		for (int i = 0; i < size; ++i)
		{
			load (manager, v.getJsonObject (i));
		}
	}

	public static void load (HelperManager manager, String json) throws IOException
	{
		JsonValue v;
		CookJsonParser p = new TextJsonParser (new StringReader (json), json.length ());
		p.next ();
		v = p.getValue ();
		p.close ();
		load (manager, (JsonArray)v);
	}
}
