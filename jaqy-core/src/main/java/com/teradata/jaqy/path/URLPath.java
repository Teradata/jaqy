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
package com.teradata.jaqy.path;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.teradata.jaqy.interfaces.Path;

/**
 * An extremely simple Path for loading data from http URL links.
 * <p>
 * The implementation here is certainly not ideal, but it should be good
 * enough in many cases.
 *
 * @author  Heng Yuan
 */
public class URLPath implements Path
{
    private final static String HEAD = "HEAD";
    private final URL m_url;

    private long m_length;

    public URLPath (URL url)
    {
        m_url = url;
        m_length = -1;
    }

    public URL getURL ()
    {
        return m_url;
    }

    private HttpURLConnection getConnection (String method) throws IOException
    {
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) m_url.openConnection ();
        conn.setRequestMethod (method);
        conn.connect ();
        return conn;
    }

    private void getLength ()
    {
        HttpURLConnection conn = null;
        try
        {
            conn = getConnection (HEAD);
            if (conn.getResponseCode () != HttpURLConnection.HTTP_OK)
            {
                m_length = Long.MIN_VALUE;
                return;
            }
            conn.getInputStream ();
            m_length = conn.getContentLengthLong ();
        }
        catch (Exception ex)
        {
            m_length = Long.MIN_VALUE;
        }
        finally
        {
            if (conn != null)
                conn.disconnect ();
        }
    }

    private String getParentFile (String file)
    {
        int index = file.lastIndexOf ('/');
        if (index < 0)
            return "";
        else if (index < (file.length () - 1))
            return file.substring (0, index);
        else
        {
            index = file.lastIndexOf ('/', index);
            if (index < 0)
                return "";
            else
                return file.substring (0, index);
        }
    }

    @Override
    public Path getParent () throws IOException
    {
        String protocol = m_url.getProtocol ();
        String host = m_url.getHost ();
        int port = m_url.getPort ();
        String file = m_url.getFile ();

        file = getParentFile (file);

        return new URLPath (new URL (protocol, host, port, file));
    }

    @Override
    public Path getRelativePath (String name) throws IOException
    {
        String protocol = m_url.getProtocol ();
        String host = m_url.getHost ();
        int port = m_url.getPort ();
        String file = m_url.getFile ();

        if (name.startsWith ("/"))
            file = name;
        else
        {
            while (name.startsWith ("../"))
            {
                name = name.substring (3);
                file = getParentFile (file);
            }
    
            if (file.endsWith ("/"))
                file = file + name;
            else
                file = file + "/" + name;
        }

        return new URLPath (new URL (protocol, host, port, file));
    }

    @Override
    public String getPath ()
    {
        return m_url.toString ();
    }

    @Override
    public String getCanonicalPath ()
    {
        return m_url.toString ();
    }

    @Override
    public boolean isFile ()
    {
        return exists ();
    }

    @Override
    public long length ()
    {
        if (m_length >= 0)
            return m_length;
        if (m_length == Long.MIN_VALUE)
            return 0L;
        getLength ();
        if (m_length >= 0)
            return m_length;
        return 0L;
    }

    @Override
    public boolean exists ()
    {
        if (m_length >= 0)
            return true;
        if (m_length == Long.MIN_VALUE)
            return false;
        getLength ();
        return m_length >= 0;
    }

    @Override
    public InputStream getInputStream () throws IOException
    {
        return m_url.openStream ();
    }

    @Override
    public OutputStream getOutputStream () throws IOException
    {
        throw new IOException ("Cannot to create http output stream.");
    }
}
