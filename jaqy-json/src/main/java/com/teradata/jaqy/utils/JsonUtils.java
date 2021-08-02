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
package com.teradata.jaqy.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.logging.Level;

import javax.json.JsonString;
import javax.json.JsonStructure;
import javax.json.JsonValue;

import org.yuanheng.cookjson.CookJsonGenerator;
import org.yuanheng.cookjson.CookJsonParser;
import org.yuanheng.cookjson.TextJsonGenerator;
import org.yuanheng.cookjson.TextJsonParser;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.connection.JaqyResultSetMetaData;
import com.teradata.jaqy.interfaces.JaqyResultSet;

/**
 * @author  Heng Yuan
 */
public class JsonUtils
{
    public static Charset DEFAULT_CHARSET = Charset.forName ("utf-8");
    public static JsonBinaryFormat DEFAULT_BINARY_FORMAT = JsonBinaryFormat.Base64;
    public static JsonFormat DEFAULT_FORMAT = JsonFormat.Text;

    public static String toString (JsonValue v)
    {
        if (v instanceof JsonString)
        {
            return ((JsonString)v).getString ();
        }
        else if (v instanceof JsonStructure)
        {
            StringWriter sw = new StringWriter ();
            TextJsonGenerator g = new TextJsonGenerator (sw);
            g.write (v);
            g.close ();
            return sw.toString ();
        }
        return v.toString ();
    }

    private static void print (CookJsonGenerator g, Object obj) throws SQLException
    {
        if (obj == null)
        {
            g.writeNull ();
        }
        else
        {
            if (obj instanceof Boolean)
                g.write ((Boolean)obj);
            else if (obj instanceof CharSequence)
                g.write (obj.toString ());
            else if (obj instanceof Number)
            {
                if (obj instanceof BigInteger)
                    g.write ((BigInteger)obj);
                else if (obj instanceof BigDecimal)
                    g.write ((BigDecimal)obj);
                else if (obj instanceof Double ||
                         obj instanceof Float)
                    g.write (((Number)obj).doubleValue ());
                else
                    g.write (((Number)obj).intValue ());
            }
            else if (obj instanceof byte[])
                g.write ((byte[])obj);
            else
            {
                g.write (obj.toString ());
            }
        }
    }

    private static void print (CookJsonGenerator g, String name, Object obj) throws SQLException
    {
        if (obj == null)
        {
            g.writeNull (name);
        }
        else
        {
            if (obj instanceof Boolean)
                g.write (name, (Boolean)obj);
            else if (obj instanceof CharSequence)
                g.write (name, obj.toString ());
            else if (obj instanceof Number)
            {
                if (obj instanceof BigInteger)
                    g.write (name, (BigInteger)obj);
                else if (obj instanceof BigDecimal)
                    g.write (name, (BigDecimal)obj);
                else if (obj instanceof Double ||
                         obj instanceof Float)
                    g.write (name, ((Number)obj).doubleValue ());
                else
                    g.write (name, ((Number)obj).intValue ());
            }
            else if (obj instanceof byte[])
                g.write (name, (byte[])obj);
            else if (obj instanceof Array)
                print (g, name, (Array)obj);
            else if (obj instanceof Clob)
            {
                Clob clob = (Clob)obj;
                String str = clob.getSubString (1, (int)clob.length ());
                clob.free ();
                g.write (name, str);
            }
            else if (obj instanceof Blob)
            {
                Blob blob = (Blob)obj;
                byte[] bytes = blob.getBytes (1, (int)blob.length ());
                blob.free ();
                g.write (name, bytes);
            }
            else if (obj instanceof SQLXML)
            {
                SQLXML xml = (SQLXML)obj;
                g.write (name, xml.getString ());
            }
            else if (obj instanceof JsonValue)
            {
                g.write (name, (JsonValue)obj);
            }
            else
            {
                // java.sql.Struct should go here.  Other than Oracle and
                // Teradata, no other databases really implements it even
                // if the database itself support composite types (PosgreSQL).
                //
                // The issue is that the JDBC's Struct type is highly
                // problematic.  Even if it is possible to export it, but it
                // won't be possible to import it using generic tools.
                // It is just simpler to deal with it using String.
                g.write (name, obj.toString ());
            }
        }
    }

    private static void print (CookJsonGenerator g, String name, Array a) throws SQLException
    {
        if (name == null)
            g.writeStartArray ();
        else
            g.writeStartArray (name);
        ResultSet rs = a.getResultSet ();
        while (rs.next ())
        {
            Object obj = rs.getObject (2);
            print (g, obj);
        }
        g.writeEnd ();
        a.free ();
    }

    public static long print (Globals globals, CookJsonGenerator g, JaqyResultSet rs, long limit) throws SQLException
    {
        JaqyResultSetMetaData metaData = rs.getMetaData ();
        int columns = metaData.getColumnCount ();
        String[] headers = new String[columns];
        boolean[] jsonCheck = new boolean[columns];

        for (int i = 0; i < columns; ++i)
        {
            headers[i] = metaData.getColumnLabel (i + 1);
            jsonCheck[i] = metaData.isJson (i + 1);
        }

        g.writeStartArray ();
        long count = 0;
        if (limit == 0)
            limit = Long.MAX_VALUE;
        while (rs.next () && count < limit)
        {
            ++count;
            g.writeStartObject ();
            for (int i = 0; i < columns; ++i)
            {
                Object obj = rs.getObject (i + 1);
                if (obj != null && jsonCheck[i])
                {
                    globals.log (Level.INFO, "Column " + (i + 1) + " is a JSON column: " + obj.getClass ());
                    String str = null;
                    // We only deal with textual form of JSON here.
                    if (obj instanceof Clob)
                    {
                        Clob clob = (Clob)obj;
                        str = clob.getSubString (1, (int)clob.length ());
                        clob.free ();
                    }
                    else
                    {
                        str = obj.toString ();
                    }

                    if (str != null)
                    {
                        // Now convert str to JsonValue
                        try
                        {
                            CookJsonParser p = new TextJsonParser (new StringReader (str));
                            p.next ();
                            obj = p.getValue ();
                            p.close ();
                        }
                        catch (Exception ex)
                        {
                            globals.log (Level.INFO, ex);
                        }
                    }
                }
                print (g, headers[i], obj);
            }
            g.writeEnd ();
        }
        g.writeEnd ();
        g.flush ();
        return count;
    }
}
