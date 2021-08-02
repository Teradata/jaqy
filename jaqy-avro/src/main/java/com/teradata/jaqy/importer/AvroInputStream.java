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
package com.teradata.jaqy.importer;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.avro.file.SeekableInput;

/**
 * @author  Heng Yuan
 */
class AvroInputStream implements SeekableInput
{
    private final FileInputStream m_is;

    public AvroInputStream (FileInputStream is)
    {
        m_is = is;
    }

    @Override
    public void close () throws IOException
    {
        m_is.close ();
    }

    @Override
    public void seek (long p) throws IOException
    {
        m_is.getChannel ().position (p);
    }

    @Override
    public long tell () throws IOException
    {
        return m_is.getChannel ().position ();
    }

    @Override
    public long length () throws IOException
    {
        return m_is.getChannel ().size ();
    }

    @Override
    public int read (byte[] b, int off, int len) throws IOException
    {
        return m_is.read (b, off, len);
    }
}
