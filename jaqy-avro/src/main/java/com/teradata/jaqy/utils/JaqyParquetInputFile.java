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

import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

import com.teradata.jaqy.path.FilePath;

/**
 * @author  Heng Yuan
 */
public class JaqyParquetInputFile implements InputFile
{
    private FilePath m_path;

    public JaqyParquetInputFile (FilePath path)
    {
        m_path = path;
    }

    @Override
    public long getLength () throws IOException
    {
        return m_path.length ();
    }

    @Override
    public SeekableInputStream newStream () throws IOException
    {
        return new JaqyParquetFileSeekableInputStream ((FileInputStream)m_path.getInputStream ());
    }
}
