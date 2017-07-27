package com.teradata.jaqy.helper;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyHelperFactory;

public class DefaultHelperFactory implements JaqyHelperFactory
{
	private final static JaqyHelperFactory s_intance = new DefaultHelperFactory ();

	public static JaqyHelperFactory getInstance ()
	{
		return s_intance;
	}

	private DefaultHelperFactory ()
	{
	}

	@Override
	public JaqyHelper getHelper (JaqyConnection conn)
	{
		return new DefaultHelper (conn);
	}
}
