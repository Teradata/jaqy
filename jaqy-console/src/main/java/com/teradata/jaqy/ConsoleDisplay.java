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

import java.io.PrintWriter;
import java.util.logging.Level;

import com.teradata.jaqy.interfaces.Display;
import com.teradata.jaqy.interfaces.StateHandler;
import com.teradata.jaqy.utils.DefaultStateHandlers;
import com.teradata.jaqy.utils.Escape;
import com.teradata.jaqy.utils.FixedVariable;
import com.teradata.jaqy.utils.TitleUtils;

/**
 * @author  Heng Yuan
 */
public class ConsoleDisplay implements Display
{
    private final Globals m_globals;

    private final FixedVariable m_displayVar = new FixedVariable ("display", this);
    private final Escape m_escape = new Escape (this);
    private final FixedVariable m_escapeVar = new FixedVariable ("esc", m_escape);

    private PrintWriter m_pw;
    private Echo m_echo = Echo.auto;
    private boolean m_colorEnabled;

    private java.io.Console m_console;

    private boolean m_initiated;

    private StateHandler m_promptHandler = DefaultStateHandlers.promptHandler;
    private StateHandler m_titleHandler = DefaultStateHandlers.titleHandler;
    private StateHandler m_successHandler = DefaultStateHandlers.successHandler;
    private StateHandler m_updateHandler = DefaultStateHandlers.updateHandler;
    private StateHandler m_activityCountHandler = DefaultStateHandlers.activityCountHandler;
    private StateHandler m_errorHandler = DefaultStateHandlers.errorHandler;
    private StateHandler m_iterationHandler = DefaultStateHandlers.iterationHandler;

    public ConsoleDisplay (Globals globals)
    {
        m_globals = globals;
        m_pw = new PrintWriter (System.out, true);
        m_console = System.console ();
        boolean interactive = m_console != null;
        if (!interactive)
        {
            m_echo = Echo.on;
            m_colorEnabled = false;
        }
        else
        {
            m_echo = Echo.auto;
            m_colorEnabled = true;
        }
        VariableManager varManager = globals.getVarManager ();
        varManager.registerVariable (m_displayVar);
        varManager.registerVariable (m_escapeVar);
    }

    @Override
    public Globals getGlobals ()
    {
        return m_globals;
    }

    @Override
    public String getPassword (JaqyInterpreter interpreter, String prompt)
    {
        if (m_console == null)
        {
            throw new JaqyException ("Cannot get password in non-interactive mode.");
        }
        m_pw.print (prompt);
        m_pw.flush ();
        char[] chars = m_console.readPassword ("");
        return new String (chars);
    }

    @Override
    public void print (JaqyInterpreter interpreter, String s)
    {
        m_pw.print (s);
    }

    @Override
    public void println (JaqyInterpreter interpreter, String s)
    {
        m_pw.println (s);
    }

    @Override
    public void error (JaqyInterpreter interpreter, Throwable t)
    {
        m_globals.log (Level.WARNING, t);
        interpreter.setException (t);
        String msg = m_errorHandler.getString (interpreter);
        if (msg != null)
        {
            m_pw.println (msg);
        }
    }

    @Override
    public void echo (JaqyInterpreter interpreter, String msg, boolean interactive)
    {
        if ((m_echo == Echo.on ||
             (m_echo == Echo.auto && !interactive)) &&
            m_initiated)
            m_pw.println (msg);
    }

    @Override
    public PrintWriter getPrintWriter ()
    {
        return m_pw;
    }

    @Override
    public void showPrompt (JaqyInterpreter interpreter)
    {
        if (!m_initiated)
            return;
        String prompt = m_promptHandler.getString (interpreter);
        if (prompt != null)
        {
            m_pw.print (prompt);
        }
        m_pw.flush ();
    }

    @Override
    public void showTitle (JaqyInterpreter interpreter)
    {
        // we do not have a console
        if (m_console == null)
            return;
        // we do not update title until we are initiated.
        if (!m_initiated)
            return;

        String title = m_titleHandler.getString (interpreter);
        if (title != null)
        {
            TitleUtils.showTitle (title, this, m_globals.getOs ());
        }
    }

    @Override
    public Echo getEcho ()
    {
        return m_echo;
    }

    @Override
    public void setEcho (Echo echo)
    {
        m_echo = echo;
    }

    /**
     * Checking if we are running in interactive mode or
     * reading input from a file.
     *
     * @return  true if we are running in interactive mode.
     *          false if we are reading input from a file.
     */
    @Override
    public boolean isInteractive ()
    {
        return m_console != null;
    }

    void setInitiated ()
    {
        m_initiated = true;
    }

    /**
     * Check if we are currently supporting ANSI color output.
     *
     * @return  whether or not ANSI color output is enabled.
     */
    public boolean isColorEnabled ()
    {
        return m_colorEnabled;
    }

    /**
     * Enable / disable ANSI color support.
     *
     * @param   colorEnabled
     *          whether ANSI color support is enabled.
     */
    public void setColorEnabled (boolean colorEnabled)
    {
        m_colorEnabled = colorEnabled;
    }

    /**
     * Print a color only if ANSI color is enabled.
     *
     * @param   color
     *          the color string.
     */
    public void color (String color)
    {
        if (m_colorEnabled)
            m_pw.print (color);
    }

    public String fill (String str)
    {
        int size = 75;
        if (str.length () < size)
        {
            char[] chars = new char[size - str.length ()];
            for (int i = 0; i < chars.length; ++i)
                chars[i] = '-';
            return str + new String (chars);
        }
        return str;
    }

    /**
     * @return  the escape
     */
    public Escape getEscape ()
    {
        return m_escape;
    }

    @Override
    public void showSuccess (JaqyInterpreter interpreter)
    {
        String str = m_successHandler.getString (interpreter);
        if (str != null)
        {
            m_pw.println (str);
        }
    }

    @Override
    public void showSuccessUpdate (JaqyInterpreter interpreter)
    {
        String str = m_updateHandler.getString (interpreter);
        if (str != null)
        {
            m_pw.println (str);
        }
    }

    @Override
    public void showActivityCount (JaqyInterpreter interpreter)
    {
        String str = m_activityCountHandler.getString (interpreter);
        if (str != null)
        {
            m_pw.println (str);
        }
    }

    @Override
    public void showIteration (JaqyInterpreter interpreter)
    {
        String str = m_iterationHandler.getString (interpreter);
        if (str != null)
        {
            m_pw.println (str);
        }
    }

    /**
     * @return  the promptHandler
     */
    public StateHandler getPromptHandler ()
    {
        return m_promptHandler;
    }

    /**
     * @param   promptHandler
     *          the promptHandler to set
     */
    public void setPromptHandler (StateHandler promptHandler)
    {
        if (promptHandler == null)
            promptHandler = DefaultStateHandlers.promptHandler;
        m_promptHandler = promptHandler;
    }

    /**
     * @return  the titleHandler
     */
    public StateHandler getTitleHandler ()
    {
        return m_titleHandler;
    }

    /**
     * @param   titleHandler
     *          the titleHandler to set
     */
    public void setTitleHandler (StateHandler titleHandler, JaqyInterpreter interpreter)
    {
        if (titleHandler == null)
            titleHandler = DefaultStateHandlers.titleHandler;
        m_titleHandler = titleHandler;
        showTitle (interpreter);
    }

    public StateHandler getSuccessHandler ()
    {
        return m_successHandler;
    }

    public void setSuccessHandler (StateHandler successHandler)
    {
        if (successHandler == null)
            successHandler = DefaultStateHandlers.successHandler;
        m_successHandler = successHandler;
    }

    public StateHandler getUpdateHandler ()
    {
        return m_updateHandler;
    }

    public void setUpdateHandler (StateHandler updateHandler)
    {
        if (updateHandler == null)
            updateHandler = DefaultStateHandlers.updateHandler;
        m_updateHandler = updateHandler;
    }

    /**
     * @return  the errorHandler
     */
    public StateHandler getErrorHandler ()
    {
        return m_errorHandler;
    }

    /**
     * @param   errorHandler
     *          the errorHandler to set
     */
    public void setErrorHandler (StateHandler errorHandler)
    {
        if (errorHandler == null)
            errorHandler = DefaultStateHandlers.errorHandler;
        m_errorHandler = errorHandler;
    }

    public StateHandler getActivityCountHandler ()
    {
        return m_activityCountHandler;
    }

    public void setActivityCountHandler (StateHandler activityCountHandler)
    {
        if (activityCountHandler == null)
            activityCountHandler = DefaultStateHandlers.activityCountHandler;
        m_activityCountHandler = activityCountHandler;
    }

    public StateHandler getIterationHandler ()
    {
        return m_iterationHandler;
    }

    public void setIterationHandler (StateHandler iterationHandler)
    {
        if (iterationHandler == null)
            iterationHandler = DefaultStateHandlers.iterationHandler;
        m_iterationHandler = iterationHandler;
    }
}
