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

import java.io.*;
import java.nio.charset.Charset;

import com.teradata.jaqy.interfaces.Path;
import com.teradata.jaqy.path.FilePath;

/**
 * @author  Heng Yuan
 */
public class FileUtils
{
    public static String TEMPFILE_PREFIX = "jaqy";
    public static String TEMPFILE_SUFFIX = ".tmp";

    public static Reader getReader (InputStream is, String encoding) throws IOException
    {
        if (encoding != null)
            return new InputStreamReader (is, encoding);

        PushbackInputStream pis = new PushbackInputStream (is, ByteOrderMarkUtils.BOM_LEN);
        encoding = ByteOrderMarkUtils.detectEncoding (pis);
        if (encoding == null)
        {
            return new InputStreamReader (pis);
        }
        else
        {
            return new InputStreamReader (pis, encoding);
        }
    }

    public static FilePath createTempFile () throws IOException
    {
        File file = File.createTempFile(TEMPFILE_PREFIX, TEMPFILE_SUFFIX);
//      file.deleteOnExit ();
        return new FilePath (file);
    }

    public static void writeFile (Path file, InputStream is, byte[] byteBuffer) throws IOException
    {
        OutputStream os = file.getOutputStream ();
        int len;
        while ((len = is.read (byteBuffer)) >= 0)
        {
            os.write (byteBuffer, 0, len);
        }
        os.close ();
        is.close ();
    }

    public static long writeFile (Path file, Reader reader, char[] charBuffer) throws IOException
    {
        return writeFile (file, Charset.forName ("UTF-8"), reader, charBuffer);
    }

    public static long writeFile (Path file, Charset charset, Reader reader, char[] charBuffer) throws IOException
    {
        Writer writer = new OutputStreamWriter (file.getOutputStream (), charset);
        int len;
        long length = 0;
        while ((len = reader.read (charBuffer)) >= 0)
        {
            writer.write (charBuffer, 0, len);
            length += len;
        }
        writer.close ();
        reader.close ();
        return length;
    }

    public static void writeFile (Path file, byte[] bytes) throws IOException
    {
        OutputStream os = file.getOutputStream ();
        os.write (bytes);
        os.close ();
    }

    public static void writeFile (Path file, String str) throws IOException
    {
        writeFile (file, Charset.forName ("UTF-8"), str);
    }

    public static void writeFile (Path file, Charset charset, String str) throws IOException
    {
        Writer writer = new OutputStreamWriter (file.getOutputStream (), charset);
        writer.write (str);
        writer.close ();
    }

    public static byte[] readFile (Path file, long start, int length) throws IOException
    {
        byte[] byteBuffer = new byte[length];
        InputStream is = file.getInputStream ();
        if (start > 0)
        {
            long offset = start;
            long s;
            while ((s = is.skip (offset)) != offset)
            {
                offset -= s;
            }
        }
        int offset = 0;
        int remain = length;
        int len;
        while (remain > 0 &&
               (len = is.read (byteBuffer, offset, remain)) >= 0)
        {
            offset += len;
            remain -= len;
        }
        is.close ();
        return byteBuffer;
    }

    public static long getLength (Path file, Charset charset) throws IOException
    {
        InputStreamReader reader = new InputStreamReader (file.getInputStream (), charset);
        long length = reader.skip (Long.MAX_VALUE);
        reader.close ();
        return length;
    }

    public static String readString (Path file, long start, int length) throws IOException
    {
        return readString (file, Charset.forName ("UTF-8"), start, length);
    }

    public static String readString (Path file, Charset charset, long start, int length) throws IOException
    {
        char[] charBuffer = new char[length];
        Reader reader = new InputStreamReader (file.getInputStream (), charset);
        if (start > 0)
        {
            long offset = start;
            long s;
            while ((s = reader.skip (offset)) != offset)
            {
                offset -= s;
            }
        }
        int offset = 0;
        int remain = length;
        int len;
        while (remain > 0 &&
               (len = reader.read (charBuffer, offset, remain)) >= 0)
        {
            offset += len;
            remain -= len;
        }
        reader.close ();
        return new String (charBuffer);
    }

    public static void copy (Writer out, Reader r, char[] charBuffer) throws IOException
    {
        int len;
        while ((len = r.read (charBuffer)) >= 0)
        {
            out.write (charBuffer, 0, len);
        }
        r.close ();
        out.close ();
    }

    public static void copy (OutputStream os, InputStream is, byte[] byteBuffer) throws IOException
    {
        int len;
        while ((len = is.read (byteBuffer)) >= 0)
        {
            os.write (byteBuffer, 0, len);
        }
        is.close ();
        os.close ();
    }

    public static int compare (Reader r1, Reader r2) throws IOException
    {
        try
        {
            if (r1 == r2)
                return 0;
            if (!(r1 instanceof StringReader) &&
                !(r1 instanceof BufferedReader))
                r1 = new BufferedReader (r1);
            if (!(r2 instanceof StringReader) &&
                !(r2 instanceof BufferedReader))
                r2 = new BufferedReader (r2);
    
            for (int ch1 = r1.read();
                 ch1 != -1;
                 ch1 = r1.read ())
            {
                int ch2 = r2.read ();
                if (ch2 == -1)
                    return 1;
                if (ch1 != ch2)
                {
                    return ch1 - ch2;
                }
            }
            int ch2 = r2.read();
            if (ch2 == -1)
                return 0;
            return -1;
        }
        finally
        {
            r1.close ();
            r2.close ();
        }
    }

    public static int compare (InputStream is1, InputStream is2) throws IOException
    {
        try
        {
            if (is1 == is2)
                return 0;
            if (!(is1 instanceof ByteArrayInputStream) &&
                !(is1 instanceof BufferedInputStream))
                is1 = new BufferedInputStream (is1);
            if (!(is2 instanceof ByteArrayInputStream) &&
                !(is2 instanceof BufferedInputStream))
                is2 = new BufferedInputStream (is2);
    
            for (int ch1 = is1.read();
                 ch1 != -1;
                 ch1 = is1.read ())
            {
                int ch2 = is2.read ();
                if (ch2 == -1)
                    return 1;
                if (ch1 != ch2)
                {
                    return ch1 - ch2;
                }
            }
            int ch2 = is2.read();
            if (ch2 == -1)
                return 0;
            return -1;
        }
        finally
        {
            is1.close ();
            is2.close ();
        }
    }
}
