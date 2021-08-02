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

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.schema.BasicColumnInfo;
import com.teradata.jaqy.schema.FullColumnInfo;
import com.teradata.jaqy.schema.SchemaInfo;

/**
 * @author Heng Yuan
 */
public class AvroUtils
{
    public static class ScanColumnInfo
    {
        String  name;
        int     type;
        int     maxLength = -1;

        boolean nullable;
        boolean varlength;
        boolean ascii = true;
    }

    static Schema.Type getAvroType (BasicColumnInfo columnInfo)
    {
        switch (columnInfo.type)
        {
            case Types.BIT:
                return Schema.Type.BOOLEAN;
            case Types.BOOLEAN:
                return Schema.Type.BOOLEAN;
            case Types.TINYINT:
            case Types.SMALLINT:
            case Types.INTEGER:
                return Schema.Type.INT;
            case Types.BIGINT:
                return Schema.Type.LONG;
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.FLOAT:
            case Types.DOUBLE:
            {
                if (columnInfo.className != null)
                {
                    if ("java.lang.Float".equals (columnInfo.className))
                    {
                        return Schema.Type.FLOAT;
                    }
                    else if ("java.lang.Double".equals (columnInfo.className))

                    {
                        return Schema.Type.DOUBLE;
                    }
                }
                switch (columnInfo.type)
                {
                    case Types.REAL:
                    case Types.FLOAT:
                    case Types.DOUBLE:
                        return Schema.Type.DOUBLE;
                }
                return Schema.Type.STRING;
            }
            case Types.BINARY:
            case Types.BLOB:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                return Schema.Type.BYTES;
            case Types.NULL:
                return Schema.Type.NULL;
            case Types.ARRAY:
            case Types.STRUCT:
                return Schema.Type.ARRAY;
            default:
                return Schema.Type.STRING;
        }
    }

    private static Schema getArraySchema (FullColumnInfo columnInfo)
    {
        FullColumnInfo[] childrenInfos = columnInfo.children;
        if (childrenInfos == null ||
            childrenInfos.length != 1)
        {
            // We cannot handle this case.
            throw new RuntimeException ("Cannot handle column " + columnInfo.label + " type.");
        }
        Schema.Type childAvroType = getAvroType (childrenInfos[0]);
        if (childAvroType == Schema.Type.ARRAY)
        {
            // We cannot handle this case.
            throw new RuntimeException ("Cannot handle column " + columnInfo.label + " type.");
        }
        Schema childSchema = Schema.create (childAvroType);
        if (childrenInfos[0].nullable != ResultSetMetaData.columnNoNulls)
        {
            // In order to include NULLs in the record, we have to include
            // NULL as part of field schema for the column.
            ArrayList<Schema> list = new ArrayList<Schema> ();
            list.add (Schema.create (Schema.Type.NULL));
            list.add (childSchema);
            childSchema = Schema.createUnion (list);
        }
        return Schema.createArray (childSchema);
    }

    public static Schema getSchema (SchemaInfo schemaInfo, JaqyHelper helper) throws SQLException
    {
        FullColumnInfo[] columnInfos = schemaInfo.columns;
        int columns = columnInfos.length;
        ArrayList<Schema.Field> fields = new ArrayList<Schema.Field> (columns);

        for (int i = 0; i < columns; ++i)
        {
            String header = columnInfos[i].label;
            Schema.Type avroType = getAvroType (columnInfos[i]);
            Schema fieldSchema;
            if (avroType == Schema.Type.ARRAY)
            {
                fieldSchema = getArraySchema (columnInfos[i]);
            }
            else
            {
                fieldSchema = Schema.create (getAvroType (columnInfos[i]));
            }

            if (columnInfos[i].nullable != ResultSetMetaData.columnNoNulls)
            {
                // In order to include NULLs in the record, we have to include
                // NULL as part of field schema for the column.
                ArrayList<Schema> list = new ArrayList<Schema> ();
                list.add (Schema.create (Schema.Type.NULL));
                list.add (fieldSchema);
                fieldSchema = Schema.createUnion (list);
            }

            Schema.Field field = new Schema.Field (header, fieldSchema, null, (Object) null);
            fields.add (field);
        }

        // create a dummy record schema name.
        String schemaName = "rsmd" + columnInfos.hashCode ();
        Schema schema = Schema.createRecord (schemaName, null, null, false);
        schema.setFields (fields);
        return schema;
    }

    public static Object getAvroObject (Object obj, Schema.Type avroType, FullColumnInfo columnInfo, Schema childSchema) throws SQLException
    {
        switch (avroType)
        {
            case STRING:
            {
                String value;
                if (obj instanceof Clob)
                {
                    Clob clob = (Clob)obj;
                    value = clob.getSubString (1, (int)clob.length ());
                    clob.free ();
                }
                else if (obj instanceof SQLXML)
                {
                    SQLXML xml = ((SQLXML)obj);
                    value = xml.getString ();
                    xml.free ();
                }
                else
                {
                    value = obj.toString ();
                }
                return value;
            }
            case BYTES:
            {
                byte[] bytes;
                if (obj instanceof byte[])
                {
                    bytes = (byte[])obj;
                }
                else if (obj instanceof Blob)
                {
                    Blob blob = ((Blob)obj);
                    bytes = blob.getBytes (1, (int)blob.length ());
                    blob.free ();
                }
                else
                {
                    break;
                }
                return ByteBuffer.wrap (bytes);
            }
            case ARRAY:
            {
                if (!(obj instanceof Array))
                    break;
                ResultSet rs = ((Array)obj).getResultSet ();
                GenericArray<Object> arr = new GenericData.Array<Object> (1, childSchema);
                Schema.Type childType = getAvroType (columnInfo.children[0]);
                while (rs.next ())
                {
                    Object o = rs.getObject (2);
                    arr.add (getAvroObject (o, childType, columnInfo.children[0], null));
                }
                rs.close ();
                return arr;
            }
            default:
            {
                return obj;
            }
        }
        throw new IllegalArgumentException ("Cannot convert " + obj.getClass () + " to " + avroType);
    }

    public static long print (DataFileWriter<GenericRecord> writer, Schema schema, JaqyResultSet rs, SchemaInfo schemaInfo) throws Exception
    {
        FullColumnInfo[] columnInfos = schemaInfo.columns;
        int columns = columnInfos.length;
        Schema.Type[] avroTypes = new Schema.Type[columns];
        Schema[] avroSchemas = new Schema[columns];

        for (int i = 0; i < columns; ++i)
        {
            avroTypes[i] = getAvroType (columnInfos[i]);
            if (avroTypes[i] == Schema.Type.ARRAY)
            {
                avroSchemas[i] = getArraySchema (columnInfos[i]);
            }
        }

        long count = 0;
        while (rs.next ())
        {
            ++count;
            GenericRecord r = new GenericData.Record (schema);
            for (int i = 0; i < columns; ++i)
            {
                Object obj = rs.getObject (i + 1);
                if (obj == null)
                {
                    r.put (i, null);
                }
                else
                {
                    r.put (i, getAvroObject (obj, avroTypes[i], columnInfos[i], avroSchemas[i]));
                }
            }
            writer.append (r);
        }
        return count;
    }

    /**
     * Convert AVRO schema to database type.
     *
     * @param   fieldSchema
     *          the AVRO column schema.
     * @param   typeInfo (OUT)
     *          the type info to be updated.
     * @return  true if we need to scan rows for variable length data.
     */
    private static boolean updateType (Schema fieldSchema, ScanColumnInfo typeInfo)
    {
        switch (fieldSchema.getType ())
        {
            case STRING:
            {
                if (typeInfo.type != 0 &&
                    typeInfo.type != Types.CHAR)
                    throw new RuntimeException ("Cannot handle the AVRO schema.");
                if (typeInfo.type == 0)
                {
                    typeInfo.type = Types.CHAR;
                }
                return true;
            }
            case BYTES:
            {
                if (typeInfo.type != 0 &&
                    typeInfo.type != Types.BINARY)
                    throw new RuntimeException ("Cannot handle the AVRO schema.");
                typeInfo.type = Types.BINARY;
                return true;
            }
            case INT:
            {
                if (typeInfo.type != 0 &&
                    typeInfo.type != Types.INTEGER)
                    throw new RuntimeException ("Cannot handle the AVRO schema.");
                typeInfo.type = Types.INTEGER;
                break;
            }
            case LONG:
            {
                if (typeInfo.type != 0 &&
                    typeInfo.type != Types.BIGINT)
                    throw new RuntimeException ("Cannot handle the AVRO schema.");
                typeInfo.type = Types.BIGINT;
                break;
            }
            case FLOAT:
            {
                if (typeInfo.type != 0 &&
                    typeInfo.type != Types.FLOAT)
                    throw new RuntimeException ("Cannot handle the AVRO schema.");
                typeInfo.type = Types.FLOAT;
                break;
            }
            case DOUBLE:
            {
                if (typeInfo.type != 0 &&
                    typeInfo.type != Types.DOUBLE)
                    throw new RuntimeException ("Cannot handle the AVRO schema.");
                typeInfo.type = Types.DOUBLE;
                break;
            }
            case BOOLEAN:
            {
                if (typeInfo.type != 0 &&
                    typeInfo.type != Types.BOOLEAN)
                    throw new RuntimeException ("Cannot handle the AVRO schema.");
                typeInfo.type = Types.BOOLEAN;
                break;
            }
            case NULL:
            {
                typeInfo.nullable = true;
                break;
            }
            case UNION:
            {
                boolean b = false;
                for (Schema s : fieldSchema.getTypes ())
                {
                    b |= updateType (s, typeInfo);
                }
                return b;
            }
            default:
            {
                throw new RuntimeException ("Cannot generate the schema.");
            }
        }
        return false;
    }

    public static SchemaInfo getSchemaInfo (ScanColumnInfo[] scanColumnInfos, boolean doScan)
    {
        int numColumns = scanColumnInfos.length;
        FullColumnInfo[] columnInfos = new FullColumnInfo[numColumns];
        for (int i = 0; i < numColumns; ++i)
        {
            FullColumnInfo columnInfo = new FullColumnInfo ();
            ScanColumnInfo scanColumnInfo = scanColumnInfos[i];
            columnInfos[i] = columnInfo;
            columnInfo.name = scanColumnInfo.name;
            columnInfo.label = scanColumnInfo.name;

            if (!doScan)
            {
                scanColumnInfo.nullable = true;
            }

            columnInfo.nullable = scanColumnInfo.nullable ? ResultSetMetaData.columnNullable : ResultSetMetaData.columnNoNulls;
            columnInfo.type = scanColumnInfo.type;

            if (scanColumnInfo.type == Types.CHAR)
            {
                if (scanColumnInfo.maxLength < 1)
                {
                    scanColumnInfo.maxLength = 1;
                    scanColumnInfo.varlength = true;
                }

                if (scanColumnInfo.ascii)
                {
                    columnInfo.type = scanColumnInfo.varlength ? Types.VARCHAR : Types.CHAR;
                }
                else
                {
                    columnInfo.type = scanColumnInfo.varlength ? Types.NVARCHAR : Types.NCHAR;
                }
                columnInfo.precision = scanColumnInfo.maxLength;
            }
            else if (scanColumnInfo.type == Types.BINARY)
            {
                if (scanColumnInfo.maxLength < 1)
                {
                    scanColumnInfo.maxLength = 1;
                    scanColumnInfo.varlength = true;
                }

                columnInfo.type = scanColumnInfo.varlength ? Types.VARBINARY : Types.BINARY;
                columnInfo.precision = scanColumnInfo.maxLength;
            }
        }
        return new SchemaInfo (columnInfos);
    }

    /**
     * Get the database schema from AVRO schema
     * @param   avroSchema
     *          the AVRO schema
     * @return  database schema
     */
    public static SchemaInfo getSchema (Schema avroSchema, Iterator<GenericRecord> iter)
    {
        List<Schema.Field> fields = avroSchema.getFields ();
        int numCols = fields.size ();
        ScanColumnInfo[] scanColumnInfos = new ScanColumnInfo[numCols];
        int i = 0;
        boolean doScan = false;
        for (Schema.Field field  : fields)
        {
            ScanColumnInfo scanColumnInfo = new ScanColumnInfo ();
            scanColumnInfos[i++] = scanColumnInfo;

            scanColumnInfo.name = field.name ();
            doScan |= updateType (field.schema (), scanColumnInfo);
        }
        int numFields = i;

        if (doScan)
        {
            // We have variable length data to check the maximum length.
            while (iter.hasNext ())
            {
                GenericRecord record = iter.next ();
                for (i = 0; i < numFields; ++i)
                {
                    ScanColumnInfo typeInfo = scanColumnInfos[i];
                    if (typeInfo.type == Types.CHAR)
                    {
                        CharSequence o = (CharSequence)record.get (i);
                        if (o == null)
                        {
                            typeInfo.nullable = true;
                            continue;
                        }
                        if (typeInfo.ascii)
                        {
                            typeInfo.ascii = StringUtils.isAscii (o.toString ());
                        }
                        int len = o.length ();
                        if (typeInfo.maxLength != len)
                        {
                            if (typeInfo.maxLength < 0)
                            {
                                typeInfo.maxLength = len;
                            }
                            else
                            {
                                typeInfo.varlength = true;
                                if (typeInfo.maxLength < len)
                                {
                                    typeInfo.maxLength = len;
                                }
                            }
                        }
                    }
                    else if (typeInfo.type == Types.BINARY)
                    {
                        ByteBuffer bb = (ByteBuffer)record.get (i);
                        if (bb == null)
                        {
                            typeInfo.nullable = true;
                            continue;
                        }
                        int len = bb.remaining ();
                        if (typeInfo.maxLength != len)
                        {
                            if (typeInfo.maxLength < 0)
                            {
                                typeInfo.maxLength = len;
                            }
                            else
                            {
                                typeInfo.varlength = true;
                                if (typeInfo.maxLength < len)
                                {
                                    typeInfo.maxLength = len;
                                }
                            }
                        }
                    }
                    else
                    {
                        if (record.get (i) == null)
                        {
                            typeInfo.nullable = true;
                        }
                    }
                }
            }
        }

        return getSchemaInfo (scanColumnInfos, doScan);
    }
}
