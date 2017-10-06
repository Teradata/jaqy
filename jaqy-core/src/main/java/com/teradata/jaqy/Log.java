/*
 * Copyright (c) 2017 Teradata
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

import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * @author	Heng Yuan
 */
public class Log
{
	private final static Log s_instance = new Log ();

	private final Logger m_logger;
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

	private Log ()
	{
		m_logger = Logger.getLogger (Log.class.getName ());
		m_logger.setUseParentHandlers (false);
		Handler handler = new StreamHandler (System.out, m_formatter);
		handler.setLevel (Level.OFF);
		m_logger.addHandler (handler);
		m_logger.setLevel (Level.OFF);
	}

	public static void setLevel (String levelStr)
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

	public static String getLevel ()
	{
		Level level = s_instance.m_logger.getLevel ();
		if (level == Level.ALL)
			return "all";
		if (level == Level.INFO)
			return "info";
		if (level == Level.WARNING)
			return "warning";
		if (level == Level.OFF)
			return "off";
		return "off";
	}

	public static void setLevel (Level level)
	{
		s_instance.m_logger.setLevel (level);
		s_instance.m_logger.getHandlers ()[0].setLevel (level);
	}

	public static void log (Level level, String msg)
	{
		s_instance.m_logger.log (level, msg);
		s_instance.m_logger.getHandlers ()[0].flush ();
	}

	public static void log (Level level, Throwable t)
	{
		s_instance.m_logger.log (level, null, t);
		s_instance.m_logger.getHandlers ()[0].flush ();
	}
}
