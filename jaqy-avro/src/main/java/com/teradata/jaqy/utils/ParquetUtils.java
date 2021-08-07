/*
 * Copyright (c) 2021 Teradata
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

import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import com.teradata.jaqy.JaqyException;

/**
 * @author Heng Yuan
 */
public class ParquetUtils
{
    public static long getByteSize (String strSize)
    {
        strSize = strSize.trim ().toLowerCase ();

        long size = 1;
        if (strSize.endsWith ("mb"))
        {
            size = 1024 * 1024;
            strSize = strSize.substring (0, strSize.length () - 2).trim ();
        }
        else if (strSize.endsWith ("gb"))
        {
            size = 1024 * 1024 * 1024;
            strSize = strSize.substring (0, strSize.length () - 2).trim ();
        }
        try
        {
            long s = Long.parseLong (strSize);
            size *= s;
        }
        catch (Exception ex)
        {
            return -1;
        }
        return size;
    }

    public static CompressionCodecName getCompressionCodec (String name)
    {
        if ("gz".equalsIgnoreCase (name))
        {
            return CompressionCodecName.GZIP;
        }
        try
        {
            return CompressionCodecName.fromConf (name);
        }
        catch (Exception ex)
        {
            throw new JaqyException ("Unknown compression codec: " + name);
        }
    }

    public static CompressionCodecName getCompressionCodecFromExtension (String name)
    {
        for (CompressionCodecName c : CompressionCodecName.values ())
        {
            if (c == CompressionCodecName.UNCOMPRESSED)
                continue;

            if (name.endsWith (c.getExtension ()))
            {
                return c;
            }
        }
        return CompressionCodecName.UNCOMPRESSED;
    }
}
