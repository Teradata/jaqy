package com.teradata.jaqy.helper;

import java.sql.ResultSet;

import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;

public class DefaultHelper implements JaqyHelper
{
	private final JaqyConnection m_conn;

	public DefaultHelper (JaqyConnection conn)
	{
		m_conn = conn;
	}

	@Override
	public JaqyResultSet getResultSet (ResultSet rs)
	{
		return new JaqyResultSet (rs, this);
	}

	@Override
	public JaqyConnection getConnection ()
	{
		return m_conn;
	}
}
