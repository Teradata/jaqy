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
package com.teradata.jaqy.command;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.teradata.jaqy.*;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.utils.SQLUtils;
import com.teradata.jaqy.utils.SessionUtils;
import com.teradata.jaqy.utils.StringUtils;

/**
 * @author	Heng Yuan
 */
public class InfoCommand extends JaqyCommandAdapter
{
	public InfoCommand ()
	{
		super ("info.txt");
	}

	@Override
	public String getDescription ()
	{
		return "provides information for the database";
	}

	@Override
	public CommandArgumentType getArgumentType ()
	{
		return CommandArgumentType.sql;
	}

	@Override
	public void execute (String[] args, Globals globals, JaqyInterpreter interpreter) throws SQLException
	{
		if (args.length == 0)
		{
			interpreter.error ("need to provide the information type.");
			return;
		}

		if (!SessionUtils.checkOpen (interpreter))
			return;
			
		String type = args[0].toLowerCase ();
		if ("behavior".equals (type) ||
			"behaviors".equals (type) ||
			"client".equals (type) ||
			"feature".equals (type) ||
			"features".equals (type) ||
			"function".equals (type) ||
			"functions".equals (type) ||
			"keyword".equals (type) ||
			"keywords".equals (type) ||
			"limit".equals (type) ||
			"limits".equals (type) ||
			"schema".equals (type) ||
			"schemas".equals (type) ||
			"server".equals (type) ||
			"table".equals (type) ||
			"type".equals (type) ||
			"types".equals (type) ||
			"user".equals (type))
		{
			// dummy
		}
		else
		{
			interpreter.error ("invalid information type: " + args[0]);
			return;
		}

		Session session = interpreter.getSession ();
		JaqyConnection conn = session.getConnection ();
		if (conn == null ||
			conn.isClosed ())
		{
			interpreter.println ("No active sessions.");
			return;
		}

		DatabaseMetaData metaData = conn.getMetaData ();
		if ("behavior".equals (type) ||
				 "behaviors".equals (type))
			listBehaviors (interpreter, metaData);
		else if ("client".equals (type))
			listClient (interpreter, metaData);
		else if ("feature".equals (type) ||
				 "features".equals (type))
			listFeatures (interpreter, metaData);
		else if ("function".equals (type) ||
				 "functions".equals (type))
			listFunctions (interpreter, metaData);
		else if ("keyword".equals (type) ||
				 "keywords".equals (type))
			listKeywords (interpreter, metaData);
		else if ("limit".equals (type) ||
				 "limits".equals (type))
			listLimits (interpreter, metaData);
		else if ("schema".equals (type) ||
				 "schemas".equals (type))
			listSchemas (interpreter, args, conn);
		else if ("server".equals (type))
			listServer (interpreter, metaData);
		else if ("table".equals (type))
			listTableTypes (interpreter, metaData);
		else if ("type".equals (type) ||
				 "types".equals (type))
			listTypes (interpreter, metaData);
		else if ("user".equals (type))
			listUser (interpreter, metaData);
	}

	private void listUser (JaqyInterpreter interpreter, DatabaseMetaData metaData) throws SQLException
	{
		interpreter.println (metaData.getUserName ());
	}

	private void listKeywords (JaqyInterpreter interpreter, DatabaseMetaData metaData) throws SQLException
	{
		interpreter.println (metaData.getSQLKeywords ());
	}

	private void listFunctions (JaqyInterpreter interpreter, DatabaseMetaData metaData)
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Value" });
		try
		{
			pt.addRow (new String[]{ "Numeric functions", metaData.getNumericFunctions () });
			pt.addRow (new String[]{ "String functions", metaData.getStringFunctions () });
			pt.addRow (new String[]{ "System functions", metaData.getSystemFunctions () });
			pt.addRow (new String[]{ "Date/Time functions", metaData.getTimeDateFunctions () });
		}
		catch (Throwable t)
		{
		}

		interpreter.print (pt);
	}

	private void listServer (JaqyInterpreter interpreter, DatabaseMetaData metaData)
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Value" });
		try
		{
			pt.addRow (new String[]{ "Schema Term", metaData.getSchemaTerm ()});
			pt.addRow (new String[]{ "Procedure Term", metaData.getProcedureTerm ()});
			pt.addRow (new String[]{ "Catalog Term", metaData.getCatalogTerm ()});
			pt.addRow (new String[]{ "User", metaData.getUserName ()});
			pt.addRow (new String[]{ "URL", metaData.getURL ()});
			pt.addRow (new String[]{ "Ready only", metaData.isReadOnly () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Database name", metaData.getDatabaseProductName ()});
			pt.addRow (new String[]{ "Database version", metaData.getDatabaseProductVersion ()});
			pt.addRow (new String[]{ "Driver name", metaData.getDriverName ()});
			pt.addRow (new String[]{ "Driver version", metaData.getDriverVersion ()});
		}
		catch (Throwable t)
		{
		}

		interpreter.print (pt);
	}

	private void listBehaviors (JaqyInterpreter interpreter, DatabaseMetaData metaData)
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Value" });
		try
		{
			pt.addRow (new String[]{ "NULLs are sorted high", metaData.nullsAreSortedHigh () ? "Yes" : "No" });
			pt.addRow (new String[]{ "NULLs are sorted low", metaData.nullsAreSortedLow () ? "Yes" : "No" });
			pt.addRow (new String[]{ "NULLs are sorted at start", metaData.nullsAreSortedAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "NULLs are sorted at end", metaData.nullsAreSortedAtEnd () ? "Yes" : "No" });
			pt.addRow (new String[]{ "NULL + non-null is NULL", metaData.nullPlusNonNullIsNull () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Use local files", metaData.usesLocalFiles () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Use local file per table", metaData.usesLocalFilePerTable () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Identifier quote string", metaData.getIdentifierQuoteString () });
			pt.addRow (new String[]{ "Search string escape", metaData.getSearchStringEscape () });
			pt.addRow (new String[]{ "Extra name characters", metaData.getExtraNameCharacters () });
	
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Catalog separator", metaData.getCatalogSeparator () });
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Is catalog at start", metaData.isCatalogAtStart () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Default Transaction Isolation", getIsolationLevel (metaData.getDefaultTransactionIsolation ()) });
			pt.addRow (new String[]{ "ResultSet holdability", getHoldability (metaData.getResultSetHoldability ()) });
	
			pt.addRow (new String[]{ "LOB update on copy", metaData.locatorsUpdateCopy () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Row ID lifetime", metaData.getRowIdLifetime ().toString () });
			pt.addRow (new String[]{ "Auto-commit failure closes all ResultSets", metaData.autoCommitFailureClosesAllResultSets () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Auto-generated key always return", metaData.generatedKeyAlwaysReturned () ? "Yes" : "No" });
		}
		catch (Throwable t)
		{
		}
		interpreter.print (pt);
	}

	private void listFeatures (JaqyInterpreter interpreter, DatabaseMetaData metaData)
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Supported" });
		try
		{
			pt.addRow (new String[]{ "ALT TABLE ADD column", metaData.supportsAlterTableWithAddColumn () ? "Yes" : "No" });
			pt.addRow (new String[]{ "ALT TABLE DROP column", metaData.supportsAlterTableWithDropColumn () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Column aliasing", metaData.supportsColumnAliasing () ? "Yes" : "No" });
			pt.addRow (new String[]{ "CONVERT", metaData.supportsConvert () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Table correlation names", metaData.supportsTableCorrelationNames () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Different table correlation names", metaData.supportsDifferentTableCorrelationNames () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Expression in ORDER BY", metaData.supportsExpressionsInOrderBy () ? "Yes" : "No" });
			pt.addRow (new String[]{ "ORDER BY unrelated", metaData.supportsOrderByUnrelated () ? "Yes" : "No" });
			
			pt.addRow (new String[]{ "GROUP BY", metaData.supportsGroupBy () ? "Yes" : "No" });
			pt.addRow (new String[]{ "GROUP BY unrelated", metaData.supportsGroupByUnrelated () ? "Yes" : "No" });
			pt.addRow (new String[]{ "GROUP BY beyond select", metaData.supportsGroupByBeyondSelect () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "LIKE escape clause", metaData.supportsLikeEscapeClause () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Multiple result set", metaData.supportsMultipleResultSets () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Multple transactions", metaData.supportsMultipleTransactions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Non-nullable columns", metaData.supportsNonNullableColumns () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Minimum SQL grammar", metaData.supportsMinimumSQLGrammar () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Core SQL grammar", metaData.supportsCoreSQLGrammar () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Extended SQL grammar", metaData.supportsExtendedSQLGrammar () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "SQL92 entry", metaData.supportsANSI92EntryLevelSQL () ? "Yes" : "No" });
			pt.addRow (new String[]{ "SQL92 intermediate", metaData.supportsANSI92IntermediateSQL () ? "Yes" : "No" });
			pt.addRow (new String[]{ "SQL92 full", metaData.supportsANSI92FullSQL () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Integrity Enhancement Facility", metaData.supportsIntegrityEnhancementFacility () ? "Yes" : "No" });
			pt.addRow (new String[]{ "OUTER JOIN", metaData.supportsOuterJoins () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Full OUTER JOIN", metaData.supportsFullOuterJoins () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Limited OUTER JOIN", metaData.supportsLimitedOuterJoins () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Schemas in DML", metaData.supportsSchemasInDataManipulation () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Schemas in procedure calls", metaData.supportsSchemasInProcedureCalls () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Schemas in table DDL", metaData.supportsSchemasInTableDefinitions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Schemas in index DDL", metaData.supportsSchemasInIndexDefinitions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Schemas in privilege definitions", metaData.supportsSchemasInPrivilegeDefinitions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Catalog in DML", metaData.supportsCatalogsInDataManipulation () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Catalog in procedure calls", metaData.supportsCatalogsInProcedureCalls () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Catalog in table DDL", metaData.supportsCatalogsInTableDefinitions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Catalog in index DDL", metaData.supportsCatalogsInIndexDefinitions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Catalog in privilege definitions", metaData.supportsCatalogsInPrivilegeDefinitions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Positioned DELETE", metaData.supportsPositionedDelete () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Positioned UPDATE", metaData.supportsPositionedUpdate () ? "Yes" : "No" });
			pt.addRow (new String[]{ "SELECT FOR UPDATE", metaData.supportsSelectForUpdate () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Stored Procedure", metaData.supportsStoredProcedures () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Subqueries in comparisons", metaData.supportsSubqueriesInComparisons () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Subqueries in EXISTS", metaData.supportsSubqueriesInExists () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Subqueries in IN", metaData.supportsSubqueriesInIns () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Subqueries in quantified expressions", metaData.supportsSubqueriesInQuantifieds () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Correlated subqueries", metaData.supportsCorrelatedSubqueries () ? "Yes" : "No" });
			pt.addRow (new String[]{ "UNION", metaData.supportsUnion () ? "Yes" : "No" });
			pt.addRow (new String[]{ "UNION ALL", metaData.supportsUnionAll () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Open cursor across commits", metaData.supportsOpenCursorsAcrossCommit () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Open cursor across rollbacks", metaData.supportsOpenCursorsAcrossRollback () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Open statement across commits", metaData.supportsOpenStatementsAcrossCommit () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Open statement across rollbacks", metaData.supportsOpenStatementsAcrossRollback () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Transactions", metaData.supportsTransactions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "DDL and DML in one transaction", metaData.supportsDataDefinitionAndDataManipulationTransactions () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Only DML in one transaction", metaData.supportsDataManipulationTransactionsOnly () ? "Yes" : "No" });
			pt.addRow (new String[]{ "DDL causes transaction commit", metaData.dataDefinitionCausesTransactionCommit () ? "Yes" : "No" });
			pt.addRow (new String[]{ "DDL ignored in transactions", metaData.dataDefinitionIgnoredInTransactions () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Batch updates", metaData.supportsBatchUpdates () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Save points", metaData.supportsSavepoints () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Named parameters", metaData.supportsNamedParameters () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Multiple open results", metaData.supportsMultipleOpenResults () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Get auto-generated keys", metaData.supportsGetGeneratedKeys () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "Statement pooling", metaData.supportsStatementPooling () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "SQL Function", metaData.supportsStoredFunctionsUsingCallSyntax () ? "Yes" : "No" });
	
			pt.addRow (new String[]{ "REF CURSOR", metaData.supportsRefCursors () ? "Yes" : "No" });
		}
		catch (Throwable t)
		{
		}
		interpreter.print (pt);
	}

	private void listLimits (JaqyInterpreter interpreter, DatabaseMetaData metaData)
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Limit" });
		try
		{
			pt.addRow (new String[]{ "Max binary literal length", getNumberString (metaData.getMaxBinaryLiteralLength ()) });
			pt.addRow (new String[]{ "Max character literal length", getNumberString (metaData.getMaxCharLiteralLength ()) });
			pt.addRow (new String[]{ "Max column name length", getNumberString (metaData.getMaxColumnNameLength ()) });
			pt.addRow (new String[]{ "Max columns in GROUP BY", getNumberString (metaData.getMaxColumnsInGroupBy ()) });
			pt.addRow (new String[]{ "Max columns in an index", getNumberString (metaData.getMaxColumnsInIndex ()) });
			pt.addRow (new String[]{ "Max columns in ORDER BY", getNumberString (metaData.getMaxColumnsInOrderBy ()) });
			pt.addRow (new String[]{ "Max columns in SELECT", getNumberString (metaData.getMaxColumnsInSelect ()) });
			pt.addRow (new String[]{ "Max columns in a table", getNumberString (metaData.getMaxColumnsInTable ()) });
			pt.addRow (new String[]{ "Max connections", getNumberString (metaData.getMaxConnections ()) });
			pt.addRow (new String[]{ "Max cursor name length", getNumberString (metaData.getMaxCursorNameLength ()) });
			pt.addRow (new String[]{ "Max index length", getNumberString (metaData.getMaxIndexLength ()) });
			pt.addRow (new String[]{ "Max schema name length", getNumberString (metaData.getMaxSchemaNameLength ()) });
			pt.addRow (new String[]{ "Max procedure name length", getNumberString (metaData.getMaxProcedureNameLength ()) });
			pt.addRow (new String[]{ "Max catalog name length", getNumberString (metaData.getMaxCatalogNameLength ()) });
			pt.addRow (new String[]{ "Max row size", getNumberString (metaData.getMaxRowSize ()) });
			pt.addRow (new String[]{ "Max row size include BLOB", metaData.doesMaxRowSizeIncludeBlobs () ? "Yes" : "No" });
			pt.addRow (new String[]{ "Max statement length", getNumberString (metaData.getMaxStatementLength ()) });
			pt.addRow (new String[]{ "Max statements", getNumberString (metaData.getMaxStatements ()) });
			pt.addRow (new String[]{ "Max table name length", getNumberString (metaData.getMaxTableNameLength ()) });
			pt.addRow (new String[]{ "Max tables in SELECT", getNumberString (metaData.getMaxTablesInSelect ()) });
			pt.addRow (new String[]{ "Max user name length", getNumberString (metaData.getMaxUserNameLength ()) });
			pt.addRow (new String[]{ "Max logical LOB size", getNumberString (metaData.getMaxLogicalLobSize ()) });
		}
		catch (Throwable t)
		{
		}
		interpreter.print (pt);
	}

	private void listSchemas (JaqyInterpreter interpreter, String[] args, JaqyConnection conn) throws SQLException
	{
		args = StringUtils.shiftArgs (args, 1);
		if (args.length == 0)
		{
			interpreter.println (conn.getSchema ());
			return;
		}

		DatabaseMetaData metaData = conn.getMetaData ();
		ResultSet rs = null;
		try
		{
			if (args.length == 1 &&
				"all".equals (args[0]))
			{
				rs = metaData.getSchemas ();
			}
			else
			{
				String[] options = SQLUtils.getSchemaOptions (conn, args);
				rs = metaData.getSchemas (options[0], options[1]);
			}
			interpreter.print (rs);
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
		}
		finally
		{
			try
			{
				rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void listTableTypes (JaqyInterpreter interpreter, DatabaseMetaData metaData) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getTableTypes ();
			interpreter.print (rs);
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
		}
		finally
		{
			try
			{
				rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void listTypes (JaqyInterpreter interpreter, DatabaseMetaData metaData) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getTypeInfo ();
			interpreter.print (rs);
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
		}
		finally
		{
			try
			{
				rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void listClient (JaqyInterpreter interpreter, DatabaseMetaData metaData) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getClientInfoProperties ();
			interpreter.print (rs);
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
		}
		finally
		{
			try
			{
				rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private static String getNumberString (int value)
	{
		return Integer.toString (value);
	}

	private static String getNumberString (long value)
	{
		return Long.toString (value);
	}

	private static String getIsolationLevel (int value)
	{
		switch (value)
		{
			case Connection.TRANSACTION_NONE:
				return "None";
			case Connection.TRANSACTION_READ_COMMITTED:
				return "Read committed";
			case Connection.TRANSACTION_READ_UNCOMMITTED:
				return "Read uncommitted";
			case Connection.TRANSACTION_REPEATABLE_READ:
				return "Repeatable read";
			case Connection.TRANSACTION_SERIALIZABLE:
				return "Serializable";
			default:
				return "Unknown";
		}
	}

	private static String getHoldability (int value)
	{
		switch (value)
		{
			case ResultSet.CLOSE_CURSORS_AT_COMMIT:
				return "Close cursors at commit";
			case ResultSet.HOLD_CURSORS_OVER_COMMIT:
				return "Hold cursors over commit";
			default:
				return "Unknown";
		}
	}
}
