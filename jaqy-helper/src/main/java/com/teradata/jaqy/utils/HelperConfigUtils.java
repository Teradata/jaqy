package com.teradata.jaqy.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.yuanheng.cookjson.CookJsonParser;
import org.yuanheng.cookjson.TextJsonParser;
import org.yuanheng.cookjson.UTF8TextJsonParser;

import com.teradata.jaqy.HelperManager;
import com.teradata.jaqy.connection.JdbcFeatures;
import com.teradata.jaqy.helper.DefaultHelperFactory;

public class HelperConfigUtils
{
	private static void loadFeatures (DefaultHelperFactory factory, JsonObject v)
	{
		if (v == null)
			return;
		JdbcFeatures features = new JdbcFeatures ();
		if (!v.getBoolean ("schema", false))
		{
			features.noSchema = true;
		}
		if (!v.getBoolean ("catalog", false))
		{
			features.noCatalog = true;
		}
		factory.setFeatures (features);
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
		loadFeatures (factory, v.getJsonObject ("features"));
		HashMap<String, SimpleQuery> map = new HashMap<String, SimpleQuery> ();
		updateMap (map, "catalogSQL", v);
		updateMap (map, "schemaSQL", v);
		updateMap (map, "tableSchemaSQL", v);
		updateMap (map, "tableColumnSQL", v);
		factory.setSQLMap (map);
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

	public static void load (HelperManager manager, InputStream is) throws IOException
	{
		JsonValue v;
		CookJsonParser p = new UTF8TextJsonParser (is);
		p.next ();
		v = p.getValue ();
		p.close ();
		load (manager, (JsonObject)v);
	}
}
