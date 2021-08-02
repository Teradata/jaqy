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
package com.teradata.jaqy.resultset;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.teradata.jaqy.path.FilePath;
import com.teradata.jaqy.utils.FileUtils;

/**
 * @author  Heng Yuan
 */
public class FileClobTest
{
    @Rule
    public TemporaryFolder m_folder = new TemporaryFolder ();

    @Test
    public void testClob () throws Exception
    {
        String s1 = "This is a test";
        StringReader sr1;
        Reader sr2;

        File file = new File (m_folder.getRoot (), "c1.txt");
        FilePath filePath = new FilePath (file);
        FileUtils.writeFile (filePath, s1);

        FileClob clob = new FileClob (filePath, Charset.forName ("utf-8"));
        Assert.assertEquals (s1.length (), clob.length ());

        Assert.assertEquals (s1, clob.getSubString (1, (int)clob.length ()));

        sr1 = new StringReader (s1);
        sr2 = clob.getCharacterStream ();
        Assert.assertEquals (0, FileUtils.compare (sr1, sr2));
        sr1.close ();
        sr2.close ();
    }

    @Test (expected = SQLException.class)
    public void testClob2 () throws Exception
    {
        File file = new File (m_folder.getRoot (), "c2.txt");
        FilePath filePath = new FilePath (file);
        FileClob clob = new FileClob (filePath, Charset.forName ("utf-8"));
        clob.length ();
    }

    @Test (expected = SQLException.class)
    public void testClob3 () throws Exception
    {
        File file = new File (m_folder.getRoot (), "c3.txt");
        FilePath filePath = new FilePath (file);
        FileClob clob = new FileClob (filePath, Charset.forName ("utf-8"));
        clob.getCharacterStream ();
    }

    @Test (expected = SQLException.class)
    public void testClob4 () throws Exception
    {
        File file = new File (m_folder.getRoot (), "c4.txt");
        FilePath filePath = new FilePath (file);
        FileClob clob = new FileClob (filePath, Charset.forName ("utf-8"));
        clob.getSubString (0, 1);
    }

    @Test (expected = SQLException.class)
    public void testClob5 () throws Exception
    {
        File file = new File (m_folder.getRoot (), "c5.txt");
        FilePath filePath = new FilePath (file);
        FileClob clob = new FileClob (filePath, Charset.forName ("utf-8"));
        clob.getSubString (1, -1);
    }

    @Test (expected = SQLException.class)
    public void testClob6 () throws Exception
    {
        File file = m_folder.newFile ("c6.txt");
        FilePath filePath = new FilePath (file);
        FileClob clob = new FileClob (filePath, Charset.forName ("utf-8"));
        clob.getSubString (1, 2);
    }
}
