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

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLXML;

import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.interfaces.JaqyResultSet;
import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.typehandler.TypeHandler;

/**
 * @author  Heng Yuan
 */
public class FileHandler implements TypeHandler
{
    private final Path m_csvFile;
    private final CSVExportInfo m_fileInfo;

    public FileHandler (Path csvFile, CSVExportInfo fileInfo)
    {
        m_csvFile = csvFile;
        m_fileInfo = fileInfo;
    }

    @Override
    public String getString (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws Exception
    {
        Object o = rs.getObject (column);
        if (o == null)
            return null;
        String fileName = m_fileInfo.nameGen.newName ();
        Path file = m_csvFile.getRelativePath (fileName);
        if (o instanceof byte[])
        {
            FileUtils.writeFile (file, (byte[])o);
        }
        else if (o instanceof Blob)
        {
            Blob blob = (Blob)o;
            InputStream is = ((Blob)o).getBinaryStream ();
            FileUtils.writeFile (file, is, interpreter.getByteBuffer ());
            is.close ();
            blob.free ();
        }
        else if (o instanceof Clob)
        {
            Clob clob = (Clob)o;
            Reader reader = clob.getCharacterStream ();
            FileUtils.writeFile (file, reader, interpreter.getCharBuffer ());
            reader.close ();
            clob.free ();
        }
        else if (o instanceof SQLXML)
        {
            SQLXML xml = (SQLXML)o;
            Reader reader = xml.getCharacterStream ();
            FileUtils.writeFile (file, m_fileInfo.charset, reader, interpreter.getCharBuffer ());
            reader.close ();
            xml.free ();
        }
        else
        {
            FileUtils.writeFile (file, m_fileInfo.charset, o.toString ());
        }
        return fileName;
    }

    @Override
    public int getLength (JaqyResultSet rs, int column, JaqyInterpreter interpreter) throws Exception
    {
        return 0;
    }
}
