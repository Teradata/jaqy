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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.parquet.io.SeekableInputStream;

/**
 * @author  Heng Yuan
 */
public class JaqyParquetFileSeekableInputStream extends SeekableInputStream
{
    private FileInputStream m_is;

    public JaqyParquetFileSeekableInputStream (FileInputStream is)
    {
        m_is = is;
    }

    @Override
    public long getPos () throws IOException
    {
        return m_is.getChannel ().position ();
    }

    @Override
    public void seek (long newPos) throws IOException
    {
        m_is.getChannel ().position (newPos);

    }

    @Override
    public void readFully (byte[] bytes) throws IOException
    {
        readFully (bytes, 0, bytes.length);
    }

    @Override
    public void readFully (byte[] bytes, int start, int len) throws IOException
    {
        while (len > 0)
        {
            int bytesRead = m_is.read (bytes, start, len);
            if (bytesRead < 0)
            {
                throw new IOException ("Reached EOF");
            }
            start += bytesRead;
            len -= bytesRead;
        }
    }

    @Override
    public int read (ByteBuffer buf) throws IOException
    {
        int bytesRead = read (buf.array(), buf.arrayOffset() + buf.position(), buf.remaining ());
        if (bytesRead > 0)
        {
            buf.position (buf.position () + bytesRead);
        }
        return bytesRead;
    }

    @Override
    public void readFully (ByteBuffer buf) throws IOException
    {
        while (buf.remaining () > 0)
        {
            int bytesRead = read (buf.array (), buf.arrayOffset() + buf.position(), buf.remaining ());
            if (bytesRead < 0)
            {
                throw new IOException ("Reached EOF");
            }
            buf.position (buf.position () + bytesRead);
        }
    }

    @Override
    public int read () throws IOException
    {
        return m_is.read ();
    }
}
