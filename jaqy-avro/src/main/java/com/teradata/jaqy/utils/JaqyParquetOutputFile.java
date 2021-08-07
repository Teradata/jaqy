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

import java.io.IOException;

import org.apache.parquet.io.OutputFile;
import org.apache.parquet.io.PositionOutputStream;

import com.teradata.jaqy.interfaces.Path;

/**
 * @author  Heng Yuan
 */
public class JaqyParquetOutputFile implements OutputFile
{
    private final Path m_path;

    public JaqyParquetOutputFile (Path path)
    {
        m_path = path;
    }

    @Override
    public PositionOutputStream create (long blockSizeHint) throws IOException
    {
        return new JaqyParquetPositionOutputStream (m_path.getOutputStream ());
    }

    @Override
    public PositionOutputStream createOrOverwrite (long blockSizeHint) throws IOException
    {
        return new JaqyParquetPositionOutputStream (m_path.getOutputStream ());
    }

    @Override
    public boolean supportsBlockSize ()
    {
        return false;
    }

    @Override
    public long defaultBlockSize ()
    {
        return 0;
    }
}
