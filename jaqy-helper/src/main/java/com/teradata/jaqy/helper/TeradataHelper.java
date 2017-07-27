package com.teradata.jaqy.helper;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.PropertyTableResultSet;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.utils.PropertyTableUtils;
import com.teradata.jdbc.jdbc_4.TDResultSet;

class TeradataHelper implements JaqyHelper
{
	private final JaqyConnection m_conn;

	public boolean isShowStmt (int activityType)
	{
		return activityType == 49;
	}

	public TeradataHelper (JaqyConnection conn)
	{
		m_conn = conn;
	}

	@Override
	public JaqyResultSet getResultSet (ResultSet rs) throws SQLException
	{
		if (rs == null)
			return null;
		if (rs instanceof TDResultSet)
		{
			TDResultSet tdRS = ((TDResultSet)rs);
			int activityType = tdRS.getExecuteActivityType ();
			if (activityType == 49)	// SHOW statement
			{
				// For SHOW statements, we need to replace '\r' with '\n';
				PropertyTable pt = PropertyTableUtils.createPropertyTable (rs);
				rs.close ();
				for (String[] row : pt.getRows ())
				{
					for (int i = 0; i < row.length; ++i)
					{
						if (row[i] != null)
						{
							row[i] = row[i].replace ('\r', '\n');
						}
					}
				}
				ResultSet newRS = new PropertyTableResultSet (pt);
				return DummyHelper.getInstance ().getResultSet (newRS);
			}
		}
		return new JaqyResultSet (rs, this);
	}

	@Override
	public JaqyConnection getConnection ()
	{
		return m_conn;
	}
}
