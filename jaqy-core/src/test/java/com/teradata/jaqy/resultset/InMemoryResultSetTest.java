/*
 * Copyright (c) 2021 Teradata
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
package com.teradata.jaqy.resultset;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.teradata.jaqy.Globals;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.helper.DummyHelper;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.utils.FileUtils;
import com.teradata.jaqy.utils.ResultSetUtils;

/**
 * @author	Heng Yuan
 */
public class InMemoryResultSetTest
{
	private Connection getConnection () throws SQLException
	{
		Properties properties = new Properties ();
		Connection conn = DriverManager.getConnection ("jdbc:sqlite::memory:", properties);
		return conn;
	}

	private void createTable (Connection conn) throws SQLException
	{
		try (Statement stmt = conn.createStatement ())
		{
			stmt.execute ("CREATE TABLE MyTable (a INTEGER, b TEXT, c BLOB);");
		}

		try (Statement stmt = conn.createStatement ())
		{
			stmt.execute ("INSERT INTO MyTable VALUES (1, 'abcdefg', X'deadbeef');");
			stmt.execute ("INSERT INTO MyTable VALUES (2, '1', X'deadbeef');");
			stmt.execute ("INSERT INTO MyTable VALUES (3, '0', X'deadbeef');");
			stmt.execute ("INSERT INTO MyTable VALUES (4, 'abcdefg', X'deadbeef');");
			stmt.execute ("INSERT INTO MyTable VALUES (5, 'abcdefg', X'deadbeef');");
		}
	}

	private void dropTable (Connection conn) throws SQLException
	{
		try (Statement stmt = conn.createStatement ())
		{
			stmt.execute ("DROP TABLE MyTable;");
		}
	}

	private InMemoryResultSet copyTable (Connection conn, JaqyHelper helper, JaqyInterpreter interpreter) throws SQLException
	{
		try (Statement stmt = conn.createStatement ())
		{
			stmt.execute ("SELECT * FROM MyTable ORDER BY 1;");
			ResultSet rs = stmt.getResultSet ();
			InMemoryResultSet newRS = ResultSetUtils.copyResultSet (rs, 0, helper, interpreter);
			rs.close ();
			return newRS;
		}
	}

	@Test
	public void test1 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		JaqyHelper helper = DummyHelper.getInstance ();

		try (Connection conn = getConnection ())
		{
			createTable (conn);
			InMemoryResultSet rs = copyTable (conn, helper, interpreter);
			dropTable (conn);

			Assert.assertEquals (ResultSet.FETCH_FORWARD, rs.getFetchDirection ());
			Assert.assertEquals (5, rs.getFetchSize ());
			rs.setFetchSize (1);
			Assert.assertEquals (5, rs.getFetchSize ());
			Assert.assertEquals (ResultSet.TYPE_SCROLL_INSENSITIVE, rs.getType ());
			Assert.assertEquals (ResultSet.CONCUR_READ_ONLY, rs.getConcurrency ());
			Assert.assertEquals (ResultSet.HOLD_CURSORS_OVER_COMMIT, rs.getHoldability ());
			Assert.assertNull (rs.getWarnings ());
			rs.clearWarnings ();
			Assert.assertNull (rs.getWarnings ());
			Assert.assertNotNull (rs.getMetaData ());

			Assert.assertEquals (1, rs.findColumn ("a"));
			Assert.assertEquals (2, rs.findColumn ("b"));
			Assert.assertEquals (3, rs.findColumn ("c"));

			Assert.assertEquals (0, rs.getRow ());
			Assert.assertEquals (true, rs.isBeforeFirst ());
			Assert.assertEquals (false, rs.isAfterLast ());
			Assert.assertEquals (false, rs.isFirst ());
			Assert.assertEquals (false, rs.isLast ());

			// next
			Assert.assertEquals (true, rs.next ());
			Assert.assertEquals (1, rs.getInt (1));

			Assert.assertEquals (1, rs.getRow ());
			Assert.assertEquals (false, rs.isBeforeFirst ());
			Assert.assertEquals (false, rs.isAfterLast ());
			Assert.assertEquals (true, rs.isFirst ());
			Assert.assertEquals (false, rs.isLast ());

			Assert.assertEquals (true, rs.getBoolean (1));
			Assert.assertEquals ((byte)1, rs.getByte (1));
			Assert.assertEquals ((short)1, rs.getShort (1));
			Assert.assertEquals (1L, rs.getLong (1));
			Assert.assertEquals (1.0f, rs.getFloat (1), 0.0f);
			Assert.assertEquals (1.0, rs.getDouble (1), 0.0);
			Assert.assertEquals (new BigDecimal (1.0), rs.getBigDecimal (1));
			Assert.assertEquals ("1", rs.getString (1));
			Assert.assertEquals (false, rs.wasNull ());

			String s1 = "abcdefg";

			Assert.assertEquals (s1, rs.getString (2));
			Assert.assertEquals (s1, rs.getNString (2));

			Assert.assertEquals (0, FileUtils.compare (new StringReader (s1), rs.getCharacterStream (2)));
			Assert.assertEquals (0, FileUtils.compare (new StringReader (s1), rs.getNCharacterStream (2)));
			Assert.assertEquals (0, FileUtils.compare (new ByteArrayInputStream (s1.getBytes ()), rs.getAsciiStream (2)));
			Assert.assertEquals (0, FileUtils.compare (new ByteArrayInputStream (s1.getBytes ()), rs.getUnicodeStream (2)));

			byte[] b1 = { (byte)0xde, (byte)0xad, (byte)0xbe, (byte)0xef };

			Assert.assertArrayEquals (b1, rs.getBytes (3));
			Assert.assertEquals (0, FileUtils.compare (new ByteArrayInputStream (b1), rs.getBinaryStream (3)));

			// next
			Assert.assertEquals (true, rs.next ());

			Assert.assertEquals (2, rs.getRow ());

			Assert.assertEquals (2, rs.getInt (1));
			Assert.assertEquals (true, rs.getBoolean (2));

			// next
			Assert.assertEquals (true, rs.next ());

			Assert.assertEquals (3, rs.getRow ());

			Assert.assertEquals (false, rs.getBoolean (2));

			rs.absolute (3);
			Assert.assertEquals (3, rs.getRow ());
			Assert.assertEquals (3, rs.getInt (1));

			rs.setFetchDirection (ResultSet.FETCH_REVERSE);
			Assert.assertEquals (ResultSet.FETCH_REVERSE, rs.getFetchDirection ());
			rs.next ();
			Assert.assertEquals (2, rs.getRow ());
			Assert.assertEquals (2, rs.getInt (1));

			rs.setFetchDirection (ResultSet.FETCH_FORWARD);
			rs.next ();
			Assert.assertEquals (3, rs.getRow ());
			Assert.assertEquals (3, rs.getInt (1));

			// next
			Assert.assertEquals (true, rs.next ());
			Assert.assertEquals (4, rs.getRow ());
			Assert.assertEquals (4, rs.getInt (1));

			// next
			Assert.assertEquals (true, rs.next ());
			Assert.assertEquals (5, rs.getInt (1));

			Assert.assertEquals (5, rs.getRow ());
			Assert.assertEquals (false, rs.isBeforeFirst ());
			Assert.assertEquals (false, rs.isAfterLast ());
			Assert.assertEquals (false, rs.isFirst ());
			Assert.assertEquals (true, rs.isLast ());

			// next
			Assert.assertEquals (false, rs.next ());

			Assert.assertEquals (6, rs.getRow ());
			Assert.assertEquals (false, rs.isBeforeFirst ());
			Assert.assertEquals (true, rs.isAfterLast ());
			Assert.assertEquals (false, rs.isFirst ());
			Assert.assertEquals (false, rs.isLast ());

			// first
			rs.first ();
			Assert.assertEquals (1, rs.getInt (1));

			Assert.assertEquals (1, rs.getRow ());
			Assert.assertEquals (false, rs.isBeforeFirst ());
			Assert.assertEquals (false, rs.isAfterLast ());
			Assert.assertEquals (true, rs.isFirst ());
			Assert.assertEquals (false, rs.isLast ());

			// last
			rs.last ();
			Assert.assertEquals (5, rs.getInt (1));

			Assert.assertEquals (5, rs.getRow ());
			Assert.assertEquals (false, rs.isBeforeFirst ());
			Assert.assertEquals (false, rs.isAfterLast ());
			Assert.assertEquals (false, rs.isFirst ());
			Assert.assertEquals (true, rs.isLast ());

			// beforeFirst
			rs.beforeFirst ();

			Assert.assertEquals (0, rs.getRow ());
			Assert.assertEquals (true, rs.isBeforeFirst ());
			Assert.assertEquals (false, rs.isAfterLast ());
			Assert.assertEquals (false, rs.isFirst ());
			Assert.assertEquals (false, rs.isLast ());

			// afterLast
			rs.afterLast ();

			Assert.assertEquals (6, rs.getRow ());
			Assert.assertEquals (false, rs.isBeforeFirst ());
			Assert.assertEquals (true, rs.isAfterLast ());
			Assert.assertEquals (false, rs.isFirst ());
			Assert.assertEquals (false, rs.isLast ());

			rs.close ();
		}
	}

	@Test (expected = SQLException.class)
	public void test2 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		JaqyHelper helper = DummyHelper.getInstance ();

		try (Connection conn = getConnection ())
		{
			createTable (conn);
			InMemoryResultSet rs = copyTable (conn, helper, interpreter);
			dropTable (conn);

			rs.findColumn ("d");
		}
	}

	@Test (expected = SQLException.class)
	public void test3 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		JaqyHelper helper = DummyHelper.getInstance ();

		try (Connection conn = getConnection ())
		{
			createTable (conn);
			InMemoryResultSet rs = copyTable (conn, helper, interpreter);
			dropTable (conn);

			rs.next ();
			rs.getInt (0);
		}
	}

	@Test (expected = SQLException.class)
	public void test4 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		JaqyHelper helper = DummyHelper.getInstance ();

		try (Connection conn = getConnection ())
		{
			createTable (conn);
			InMemoryResultSet rs = copyTable (conn, helper, interpreter);
			dropTable (conn);

			rs.next ();
			rs.getInt (4);
		}
	}

	@Test (expected = SQLException.class)
	public void test5 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		JaqyHelper helper = DummyHelper.getInstance ();

		try (Connection conn = getConnection ())
		{
			createTable (conn);
			InMemoryResultSet rs = copyTable (conn, helper, interpreter);
			dropTable (conn);

			rs.next ();
			rs.getInt (3);
		}
	}

	@Test (expected = SQLException.class)
	public void test6 () throws Exception
	{
		Globals globals = new Globals (null, null);
		JaqyInterpreter interpreter = new JaqyInterpreter (globals, null, null);
		JaqyHelper helper = DummyHelper.getInstance ();

		try (Connection conn = getConnection ())
		{
			createTable (conn);
			InMemoryResultSet rs = copyTable (conn, helper, interpreter);
			dropTable (conn);

			// rs.next ();
			rs.getInt (1);
		}
	}
}
