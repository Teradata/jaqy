package com.teradata.jaqy.helper;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.interfaces.JaqyHelperFactory;

public class TeradataHelperFactory implements JaqyHelperFactory
{
	@Override
	public JaqyHelper getHelper (JaqyConnection conn)
	{
		return new TeradataHelper (conn);
	}
}
