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
package com.teradata.jaqy.exporter;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.parquet.io.PositionOutputStream;

/**
 * @author  Heng Yuan
 */
public class JaqyPositionOutputStream extends PositionOutputStream
{
    private long m_pos;
    private OutputStream m_os;

    public JaqyPositionOutputStream (OutputStream os)
    {
        m_os = os;
        m_pos = 0;
    }

    @Override
    public void write (byte[] b) throws IOException
    {
        m_os.write (b);
        m_pos += b.length;
    }

    @Override
    public void write (byte[] b, int off, int len) throws IOException
    {
        m_os.write (b, off, len);
        m_pos += len;
    }

    @Override
    public void write (int b) throws IOException
    {
        m_os.write (b);
        ++m_pos;
    }

    @Override
    public void flush () throws IOException
    {
        m_os.flush ();
    }

    @Override
    public void close () throws IOException
    {
        m_os.close ();
    }

    @Override
    public long getPos () throws IOException
    {
        return m_pos;
    }
}
