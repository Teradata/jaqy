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
package com.teradata.jaqy;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.*;

import com.teradata.jaqy.interfaces.*;
import com.teradata.jaqy.path.HttpPathHandler;
import com.teradata.jaqy.utils.FixedVariable;
import com.teradata.jaqy.utils.PathUtils;
import com.teradata.jaqy.utils.URLUtils;

/**
 * This class holds all the globals and configuration informations.
 *
 * @author  Heng Yuan
 */
public class Globals
{
    /** The program name */
    private final String m_name;
    /** The program version */
    private final String m_version;
    /** The greeting message */
    private String m_greeting;

    private final JaqyDriverManager m_driverManager = new JaqyDriverManager ();
    private final OptionManager m_optionManager = new OptionManager ();
    private final ScriptManager m_scriptManager = new ScriptManager ();
    private final DebugManager m_debugManager = new DebugManager ();
    private final Os m_os = new Os ();
    private final ObjectManager<JaqyCommand> m_commandManager = new ObjectManager<JaqyCommand> (this);
    private final ObjectManager<JaqySetting> m_settingManager = new ObjectManager<JaqySetting> (this);
    private final AliasManager m_aliasManager = new AliasManager ();
    private final HelperManager m_helperManager = new HelperManager ();
    private final PathHandlerManager m_pathHandlerManager = new PathHandlerManager ();

    private Object m_sessionLock = new Object ();
    private final ArrayList<Session> m_sessions = new ArrayList<Session> ();

    private final VariableManager m_varManager = new VariableManager (null, "Global Variables");
    private final Variable m_globalsVar = new FixedVariable ("globals", this);

    private final JaqyHandlerFactoryManager<JaqyPrinter> m_printerManager;
    private final JaqyHandlerFactoryManager<JaqyExporter> m_exporterManager;
    private final JaqyHandlerFactoryManager<JaqyImporter> m_importerManager;

    private final String m_rc = "com.teradata.jaqy.interfaces.JaqyPlugin";
    private final File m_dir = new File (".");

    private final Logger m_logger = Logger.getAnonymousLogger ();
    private final Formatter m_formatter = new Formatter ()
    {
        @Override
        public String format (LogRecord record)
        {
            StringBuilder builder = new StringBuilder ();
            final String prefix = builder.append ('[').append (record.getLevel ()).append ("]: ").toString ();
            String msg = record.getMessage ();
            if (msg != null)
            {
                builder.append (msg).append ('\n');
            }
            Throwable t = record.getThrown ();
            if (t != null)
            {
                builder.append (t).append ('\n');
                for (Object o : t.getStackTrace ())
                {
                    builder.append (prefix).append ("\tat ").append (o).append ('\n');
                }
            }
            return builder.toString ();
        }
    };

    public Globals (String name, String version)
    {
        m_name = name;
        m_version = version;
        m_varManager.registerVariable (m_globalsVar);

        m_logger.setUseParentHandlers (false);
        Handler handler = new StreamHandler (System.out, m_formatter);
        handler.setLevel (Level.OFF);
        m_logger.addHandler (handler);
        m_logger.setLevel (Level.OFF);

        m_pathHandlerManager.addPathHandler (new HttpPathHandler ());
        m_printerManager = new JaqyHandlerFactoryManager<JaqyPrinter> (this, "com.teradata.jaqy.interfaces.JaqyPrinter");
        m_exporterManager = new JaqyHandlerFactoryManager<JaqyExporter> (this, "com.teradata.jaqy.interfaces.JaqyExporter");
        m_importerManager = new JaqyHandlerFactoryManager<JaqyImporter> (this, "com.teradata.jaqy.interfaces.JaqyImporter");
    }

    public void printVersion (PrintWriter pw)
    {
        String name = getName ();
        String version = getVersion ();
        pw.println (name + " " + version);
    }

    public void addOption (JaqyOption option)
    {
        m_optionManager.addOption (option);
    }

    public OptionManager getOptionManager ()
    {
        return m_optionManager;
    }

    public JaqyDriverManager getDriverManager ()
    {
        return m_driverManager;
    }

    public String getName ()
    {
        return m_name;
    }

    public String getVersion ()
    {
        return m_version;
    }

    /**
     * Get the greeting message.
     *
     * @return  the greeting message
     */
    public String getGreeting ()
    {
        return m_greeting;
    }

    /**
     * Set the greeting message.
     *
     * @param   greetMessage
     *          the greeting message to be set
     */
    public void setGreeting (String greetMessage)
    {
        m_greeting = greetMessage;
    }

    /**
     * Get the script manager.
     *
     * @return  the script manager
     */
    public ScriptManager getScriptManager ()
    {
        return m_scriptManager;
    }

    public Session createSession (Display display)
    {
        synchronized (m_sessionLock)
        {
            int sessionId = m_sessions.size ();
            Session session = new Session (this, sessionId, display);
            m_sessions.add (session);
            return session;
        }
    }

    /**
     * Get current number of sessions.
     * @return  the current number of sessions.
     */
    public int getNumSessions ()
    {
        synchronized (m_sessionLock)
        {
            return m_sessions.size ();
        }
    }

    /**
     * Get a session based on the session id.
     * @param   id
     *          the session id
     * @return  the session corresponding to the id.
     */
    public Session getSession (int id)
    {
        synchronized (m_sessionLock)
        {
            if (id < 0 || id >= m_sessions.size ())
                return null;
            return m_sessions.get (id);
        }
    }

    /**
     * Get current sessions.
     * <p>
     * This function should only be called when sessionLock is locked.
     * @return  the current sessions.
     */
    public Collection<Session> getSessions ()
    {
        ArrayList<Session> sessions = new ArrayList<Session> ();
        synchronized (m_sessionLock)
        {
            sessions.addAll (m_sessions);
        }
        return sessions;
    }

    /**
     * Retrieves the debug manager.
     * @return  the debug manager
     */
    public DebugManager getDebugManager ()
    {
        return m_debugManager;
    }

    /**
     * Retrieves the OS object.
     *
     * @return  the os object
     */
    public Os getOs ()
    {
        return m_os;
    }

    /**
     * Gets the command manager.
     * @return  the command manager
     */
    public ObjectManager<JaqyCommand> getCommandManager ()
    {
        return m_commandManager;
    }

    /**
     * Gets the command manager.
     * @return  the command manager
     */
    public ObjectManager<JaqySetting> getSettingManager ()
    {
        return m_settingManager;
    }

    /**
     * Gets the alias manager.
     * @return  the alias manager
     */
    public AliasManager getAliasManager ()
    {
        return m_aliasManager;
    }

    /**
     * Gets the variable manager.
     * @return  the variable manager
     */
    public VariableManager getVarManager ()
    {
        return m_varManager;
    }

    /**
     * @return  the printerManager
     */
    public JaqyHandlerFactoryManager<JaqyPrinter> getPrinterManager ()
    {
        return m_printerManager;
    }

    /**
     * @return  the exporterManager
     */
    public JaqyHandlerFactoryManager<JaqyExporter> getExporterManager ()
    {
        return m_exporterManager;
    }

    /**
     * @return  the importerManager
     */
    public JaqyHandlerFactoryManager<JaqyImporter> getImporterManager ()
    {
        return m_importerManager;
    }

    public void loadRC (ClassLoader cl, JaqyInterpreter interpreter) throws IOException
    {
        String name = "META-INF/services/" + m_rc;
        log (Level.INFO, "loading service: " + name);
        Enumeration<URL> e;
        if (cl instanceof URLClassLoader)
            e = ((URLClassLoader) cl).findResources (name);
        else
            e = cl.getResources (name);
        for (; e.hasMoreElements ();)
        {
            URL url = e.nextElement ();
            log (Level.INFO, "load service url: " + url);
            loadURL (cl, url, interpreter);
        }
    }

    private void loadURL (ClassLoader cl, URL url, JaqyInterpreter interpreter) throws IOException
    {
        BufferedReader reader = new BufferedReader (new InputStreamReader (url.openStream (), "utf-8"));
        String line;
        while ((line = reader.readLine ()) != null)
        {
            line = line.trim ();
            if (line.length () == 0)
                continue;
            log (Level.INFO, "load service class: " + line);
            loadClass (cl, line, interpreter);
        }
        reader.close ();
    }

    private void loadClass (ClassLoader cl, String className, JaqyInterpreter interpreter)
    {
        try
        {
            Class<?> c = cl.loadClass (className);
            JaqyPlugin plugin = (JaqyPlugin) c.newInstance ();
            plugin.init (this);
        }
        catch (Throwable t)
        {
            log (Level.WARNING, t);
            interpreter.getDisplay ().error (interpreter, t);
        }
    }

    public void loadPlugin (String path, JaqyInterpreter interpreter)
    {
        try
        {
            String[] paths = PathUtils.split (path);
            URL[] urls = new URL[paths.length];
            for (int i = 0; i < paths.length; ++i)
            {
                urls[i] = URLUtils.getFileURL (paths[i]);
            }
            ClassLoader cl = new URLClassLoader (urls);

            loadRC (cl, interpreter);

            // apparently we do not want to load the resources, since
            // that would result in re-initiating the same resource multiple
            // times.
            m_printerManager.loadRC (cl);
            m_exporterManager.loadRC (cl);
            m_importerManager.loadRC (cl);
        }
        catch (Throwable t)
        {
            log (Level.WARNING, t);
            interpreter.error ("invalid classpath: " + path);
        }
    }

    public File getDirectory ()
    {
        return m_dir;
    }

    public HelperManager getHelperManager ()
    {
        return m_helperManager;
    }

    public PathHandlerManager getPathHandlerManager ()
    {
        return m_pathHandlerManager;
    }

    public PathHandler getPathHandler (String path)
    {
        return m_pathHandlerManager.getHandler (path);
    }

    public void setLevel (String levelStr)
    {
        Level level;
        if ("info".equals (levelStr))
            level = Level.INFO;
        else if ("warning".equals (levelStr))
            level = Level.WARNING;
        else if ("all".equals (levelStr))
            level = Level.ALL;
        else if ("off".equals (levelStr))
            level = Level.OFF;
        else
            throw new IllegalArgumentException ("Unknown logging level: " + levelStr);
        setLevel (level);
    }

    public String getLevel ()
    {
        Level level = m_logger.getLevel ();
        if (level == Level.ALL)
            return "all";
        if (level == Level.INFO)
            return "info";
        if (level == Level.WARNING)
            return "warning";
        return "off";
    }

    private void setLevel (Level level)
    {
        m_logger.setLevel (level);
        m_logger.getHandlers ()[0].setLevel (level);
    }

    public boolean log (Level level, String msg)
    {
        m_logger.log (level, msg);
        m_logger.getHandlers ()[0].flush ();
        return true;
    }

    public boolean log (Level level, Throwable t)
    {
        m_logger.log (level, null, t);
        m_logger.getHandlers ()[0].flush ();
        return true;
    }
}
