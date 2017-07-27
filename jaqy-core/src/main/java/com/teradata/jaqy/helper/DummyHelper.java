package com.teradata.jaqy.helper;

import java.sql.ResultSet;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.utils.DummyConnection;

public class DummyHelper implements JaqyHelper
{
	private final static JaqyHelper s_instance = new DummyHelper ();

	public static JaqyHelper getInstance ()
	{
		return s_instance;
	}

	private final JaqyConnection m_connection = new JaqyConnection (new DummyConnection ());

	private DummyHelper ()
	{
	}

	@Override
	public JaqyResultSet getResultSet (ResultSet rs)
	{
		return new JaqyResultSet (rs, this);
	}

	@Override
	public JaqyConnection getConnection ()
	{
		return m_connection;
	}
}
