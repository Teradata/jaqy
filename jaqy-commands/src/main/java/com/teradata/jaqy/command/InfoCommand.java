/*
 * Copyright (c) 2017-2018 Teradata
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

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import com.teradata.jaqy.CommandArgumentType;
import com.teradata.jaqy.JaqyInterpreter;
import com.teradata.jaqy.PropertyTable;
import com.teradata.jaqy.Session;
import com.teradata.jaqy.connection.JaqyConnection;
import com.teradata.jaqy.connection.JaqyDefaultResultSet;
import com.teradata.jaqy.interfaces.JaqyHelper;
import com.teradata.jaqy.schema.TypeMap;
import com.teradata.jaqy.utils.DatabaseMetaDataUtils;
import com.teradata.jaqy.utils.ResultSetMetaDataUtils;
import com.teradata.jaqy.utils.SessionUtils;

/**
 * @author	Heng Yuan
 */
public class InfoCommand extends JaqyCommandAdapter
{
	private final static String getYesNo (boolean b)
	{
		return b ? "Yes" : "No";
	}

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
	public void execute (String[] args, boolean silent, boolean interactive, JaqyInterpreter interpreter) throws SQLException
	{
		if (args.length == 0)
		{
			interpreter.error ("need to provide the information type.");
		}

		SessionUtils.checkOpen (interpreter);

		String type = args[0].toLowerCase ();
		Session session = interpreter.getSession ();
		JaqyConnection conn = session.getConnection ();
		JaqyHelper helper = conn.getHelper ();

		DatabaseMetaData metaData = conn.getMetaData ();
		if ("behavior".equals (type) ||
				 "behaviors".equals (type))
			listBehaviors (interpreter, metaData, helper);
		else if ("catalog".equals (type) ||
				 "catalogs".equals (type))
			listCatalogs (interpreter, metaData, helper);
		else if ("client".equals (type))
			listClient (interpreter, metaData, helper);
		else if ("feature".equals (type) ||
				 "features".equals (type))
			listFeatures (interpreter, metaData, helper);
		else if ("function".equals (type) ||
				 "functions".equals (type))
			listFunctions (interpreter, metaData, helper);
		else if ("importmap".equals (type))
			listImportTypeMap (interpreter, helper);
		else if ("keyword".equals (type) ||
				 "keywords".equals (type))
			listKeywords (interpreter, metaData, helper);
		else if ("limit".equals (type) ||
				 "limits".equals (type))
			listLimits (interpreter, metaData, helper);
		else if ("schema".equals (type) ||
				 "schemas".equals (type))
			listSchemas (interpreter, metaData, helper);
		else if ("server".equals (type))
			listServer (interpreter, metaData, helper);
		else if ("table".equals (type))
			listTableTypes (interpreter, metaData, helper);
		else if ("typemap".equals (type))
			listTypeMap (interpreter, helper);
		else if ("type".equals (type) ||
				 "types".equals (type))
			listTypes (interpreter, metaData, helper);
		else if ("user".equals (type))
			listUser (interpreter, metaData, helper);
		else
		{
			interpreter.error ("invalid information type: " + args[0]);
		}
	}

	private void listUser (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		interpreter.println (metaData.getUserName ());
	}

	private void listKeywords (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		interpreter.println (metaData.getSQLKeywords ());
	}

	private void listFunctions (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
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
			interpreter.getGlobals ().log (Level.INFO, t);
		}

		interpreter.print (pt);
	}

	private void listServer (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Value" });
		try
		{
			pt.addRow (new String[]{ "User", metaData.getUserName ()});
			pt.addRow (new String[]{ "URL", metaData.getURL ()});
			pt.addRow (new String[]{ "Ready only", getYesNo (metaData.isReadOnly ()) });
			pt.addRow (new String[]{ "Database product name", metaData.getDatabaseProductName ()});
			pt.addRow (new String[]{ "Database product version", metaData.getDatabaseProductVersion ()});
			pt.addRow (new String[]{ "Database major version", "" + metaData.getDatabaseMajorVersion () });
			pt.addRow (new String[]{ "Database major version", "" + metaData.getDatabaseMinorVersion () });
			pt.addRow (new String[]{ "Driver name", metaData.getDriverName ()});
			pt.addRow (new String[]{ "Driver version", metaData.getDriverVersion ()});
			pt.addRow (new String[]{ "JDBC major version", "" + metaData.getJDBCMajorVersion () });
			pt.addRow (new String[]{ "JDBC minor version", "" + metaData.getJDBCMinorVersion () });
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}

		interpreter.print (pt);
	}

	private void listBehaviors (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Value" });
		try
		{
			pt.addRow (new String[]{ "Catalog Term", metaData.getCatalogTerm ()});
			pt.addRow (new String[]{ "Schema Term", metaData.getSchemaTerm ()});
			pt.addRow (new String[]{ "Procedure Term", metaData.getProcedureTerm ()});
			pt.addRow (new String[]{ "NULLs are sorted high", getYesNo (metaData.nullsAreSortedHigh ()) });
			pt.addRow (new String[]{ "NULLs are sorted low", getYesNo (metaData.nullsAreSortedLow ()) });
			pt.addRow (new String[]{ "NULLs are sorted at start", getYesNo (metaData.nullsAreSortedAtStart ()) });
			pt.addRow (new String[]{ "NULLs are sorted at end", getYesNo (metaData.nullsAreSortedAtEnd ()) });
			pt.addRow (new String[]{ "NULL + non-null is NULL", getYesNo (metaData.nullPlusNonNullIsNull ()) });

			pt.addRow (new String[]{ "Use local files", getYesNo (metaData.usesLocalFiles ()) });
			pt.addRow (new String[]{ "Use local file per table", getYesNo (metaData.usesLocalFilePerTable ()) });

			pt.addRow (new String[]{ "Identifier quote string", metaData.getIdentifierQuoteString () });
			pt.addRow (new String[]{ "Search string escape", metaData.getSearchStringEscape () });
			pt.addRow (new String[]{ "Extra name characters", metaData.getExtraNameCharacters () });

			pt.addRow (new String[]{ "Is catalog at start", getYesNo (metaData.isCatalogAtStart ()) });
			pt.addRow (new String[]{ "Catalog separator", metaData.getCatalogSeparator () });

			pt.addRow (new String[]{ "Default Transaction Isolation", DatabaseMetaDataUtils.getIsolationLevel (metaData.getDefaultTransactionIsolation ()) });
			pt.addRow (new String[]{ "ResultSet holdability", ResultSetMetaDataUtils.getHoldability (metaData.getResultSetHoldability ()) });

			pt.addRow (new String[]{ "LOB update on copy", getYesNo (metaData.locatorsUpdateCopy ()) });
			pt.addRow (new String[]{ "Row ID lifetime", metaData.getRowIdLifetime ().toString () });
			pt.addRow (new String[]{ "Auto-commit failure closes all ResultSets", getYesNo (metaData.autoCommitFailureClosesAllResultSets ()) });
			pt.addRow (new String[]{ "Auto-generated key always return", getYesNo (metaData.generatedKeyAlwaysReturned ()) });
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		interpreter.print (pt);
	}

	private void listFeatures (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		PropertyTable pt = new PropertyTable (new String[] { "Name", "Supported" });
		try
		{
			pt.addRow (new String[]{ "All Procedures are callable", getYesNo (metaData.allProceduresAreCallable ()) });
			pt.addRow (new String[]{ "All tables are selectable", getYesNo (metaData.allTablesAreSelectable ()) });

			pt.addRow (new String[]{ "ALT TABLE ADD column", getYesNo (metaData.supportsAlterTableWithAddColumn ()) });
			pt.addRow (new String[]{ "ALT TABLE DROP column", getYesNo (metaData.supportsAlterTableWithDropColumn ()) });

			pt.addRow (new String[]{ "Column aliasing", getYesNo (metaData.supportsColumnAliasing ()) });
			pt.addRow (new String[]{ "CONVERT", getYesNo (metaData.supportsConvert ()) });
			pt.addRow (new String[]{ "Table correlation names", getYesNo (metaData.supportsTableCorrelationNames ()) });
			pt.addRow (new String[]{ "Different table correlation names", getYesNo (metaData.supportsDifferentTableCorrelationNames ()) });

			pt.addRow (new String[]{ "Expression in ORDER BY", getYesNo (metaData.supportsExpressionsInOrderBy ()) });
			pt.addRow (new String[]{ "ORDER BY unrelated", getYesNo (metaData.supportsOrderByUnrelated ()) });

			pt.addRow (new String[]{ "GROUP BY", getYesNo (metaData.supportsGroupBy ()) });
			pt.addRow (new String[]{ "GROUP BY unrelated", getYesNo (metaData.supportsGroupByUnrelated ()) });
			pt.addRow (new String[]{ "GROUP BY beyond select", getYesNo (metaData.supportsGroupByBeyondSelect ()) });

			pt.addRow (new String[]{ "LIKE escape clause", getYesNo (metaData.supportsLikeEscapeClause ()) });

			pt.addRow (new String[]{ "Multiple result set", getYesNo (metaData.supportsMultipleResultSets ()) });
			pt.addRow (new String[]{ "Multple transactions", getYesNo (metaData.supportsMultipleTransactions ()) });
			pt.addRow (new String[]{ "Non-nullable columns", getYesNo (metaData.supportsNonNullableColumns ()) });

			pt.addRow (new String[]{ "Minimum SQL grammar", getYesNo (metaData.supportsMinimumSQLGrammar ()) });
			pt.addRow (new String[]{ "Core SQL grammar", getYesNo (metaData.supportsCoreSQLGrammar ()) });
			pt.addRow (new String[]{ "Extended SQL grammar", getYesNo (metaData.supportsExtendedSQLGrammar ()) });

			pt.addRow (new String[]{ "SQL92 entry", getYesNo (metaData.supportsANSI92EntryLevelSQL ()) });
			pt.addRow (new String[]{ "SQL92 intermediate", getYesNo (metaData.supportsANSI92IntermediateSQL ()) });
			pt.addRow (new String[]{ "SQL92 full", getYesNo (metaData.supportsANSI92FullSQL ()) });

			pt.addRow (new String[]{ "Integrity Enhancement Facility", getYesNo (metaData.supportsIntegrityEnhancementFacility ()) });
			pt.addRow (new String[]{ "OUTER JOIN", getYesNo (metaData.supportsOuterJoins ()) });
			pt.addRow (new String[]{ "Full OUTER JOIN", getYesNo (metaData.supportsFullOuterJoins ()) });
			pt.addRow (new String[]{ "Limited OUTER JOIN", getYesNo (metaData.supportsLimitedOuterJoins ()) });

			pt.addRow (new String[]{ "Schemas in DML", getYesNo (metaData.supportsSchemasInDataManipulation ()) });
			pt.addRow (new String[]{ "Schemas in procedure calls", getYesNo (metaData.supportsSchemasInProcedureCalls ()) });
			pt.addRow (new String[]{ "Schemas in table DDL", getYesNo (metaData.supportsSchemasInTableDefinitions ()) });
			pt.addRow (new String[]{ "Schemas in index DDL", getYesNo (metaData.supportsSchemasInIndexDefinitions ()) });
			pt.addRow (new String[]{ "Schemas in privilege definitions", getYesNo (metaData.supportsSchemasInPrivilegeDefinitions ()) });
			pt.addRow (new String[]{ "Catalog in DML", getYesNo (metaData.supportsCatalogsInDataManipulation ()) });
			pt.addRow (new String[]{ "Catalog in procedure calls", getYesNo (metaData.supportsCatalogsInProcedureCalls ()) });
			pt.addRow (new String[]{ "Catalog in table DDL", getYesNo (metaData.supportsCatalogsInTableDefinitions ()) });
			pt.addRow (new String[]{ "Catalog in index DDL", getYesNo (metaData.supportsCatalogsInIndexDefinitions ()) });
			pt.addRow (new String[]{ "Catalog in privilege definitions", getYesNo (metaData.supportsCatalogsInPrivilegeDefinitions ()) });
			pt.addRow (new String[]{ "Positioned DELETE", getYesNo (metaData.supportsPositionedDelete ()) });
			pt.addRow (new String[]{ "Positioned UPDATE", getYesNo (metaData.supportsPositionedUpdate ()) });
			pt.addRow (new String[]{ "SELECT FOR UPDATE", getYesNo (metaData.supportsSelectForUpdate ()) });
			pt.addRow (new String[]{ "Stored Procedure", getYesNo (metaData.supportsStoredProcedures ()) });
			pt.addRow (new String[]{ "Subqueries in comparisons", getYesNo (metaData.supportsSubqueriesInComparisons ()) });
			pt.addRow (new String[]{ "Subqueries in EXISTS", getYesNo (metaData.supportsSubqueriesInExists ()) });
			pt.addRow (new String[]{ "Subqueries in IN", getYesNo (metaData.supportsSubqueriesInIns ()) });
			pt.addRow (new String[]{ "Subqueries in quantified expressions", getYesNo (metaData.supportsSubqueriesInQuantifieds ()) });
			pt.addRow (new String[]{ "Correlated subqueries", getYesNo (metaData.supportsCorrelatedSubqueries ()) });
			pt.addRow (new String[]{ "UNION", getYesNo (metaData.supportsUnion ()) });
			pt.addRow (new String[]{ "UNION ALL", getYesNo (metaData.supportsUnionAll ()) });
			pt.addRow (new String[]{ "Open cursor across commits", getYesNo (metaData.supportsOpenCursorsAcrossCommit ()) });
			pt.addRow (new String[]{ "Open cursor across rollbacks", getYesNo (metaData.supportsOpenCursorsAcrossRollback ()) });
			pt.addRow (new String[]{ "Open statement across commits", getYesNo (metaData.supportsOpenStatementsAcrossCommit ()) });
			pt.addRow (new String[]{ "Open statement across rollbacks", getYesNo (metaData.supportsOpenStatementsAcrossRollback ()) });

			pt.addRow (new String[]{ "Transactions", getYesNo (metaData.supportsTransactions ()) });
			pt.addRow (new String[]{ "DDL and DML in one transaction", getYesNo (metaData.supportsDataDefinitionAndDataManipulationTransactions ()) });
			pt.addRow (new String[]{ "Only DML in one transaction", getYesNo (metaData.supportsDataManipulationTransactionsOnly ()) });
			pt.addRow (new String[]{ "DDL causes transaction commit", getYesNo (metaData.dataDefinitionCausesTransactionCommit ()) });
			pt.addRow (new String[]{ "DDL ignored in transactions", getYesNo (metaData.dataDefinitionIgnoredInTransactions ()) });

			pt.addRow (new String[]{ "Batch updates", getYesNo (metaData.supportsBatchUpdates ()) });
			pt.addRow (new String[]{ "Save points", getYesNo (metaData.supportsSavepoints ()) });
			pt.addRow (new String[]{ "Named parameters", getYesNo (metaData.supportsNamedParameters ()) });
			pt.addRow (new String[]{ "Multiple open results", getYesNo (metaData.supportsMultipleOpenResults ()) });
			pt.addRow (new String[]{ "Get auto-generated keys", getYesNo (metaData.supportsGetGeneratedKeys ()) });

			pt.addRow (new String[]{ "Statement pooling", getYesNo (metaData.supportsStatementPooling ()) });

			pt.addRow (new String[]{ "SQL Function", getYesNo (metaData.supportsStoredFunctionsUsingCallSyntax ()) });

			pt.addRow (new String[]{ "REF CURSOR", getYesNo (metaData.supportsRefCursors ()) });

			pt.addRow (new String[]{ "Supports Mixed Case Identifiers", getYesNo (metaData.supportsMixedCaseIdentifiers ()) });
			pt.addRow (new String[]{ "Stores Upper Case Identifiers", getYesNo (metaData.storesUpperCaseIdentifiers ()) });
			pt.addRow (new String[]{ "Stores Lower Case Identifiers", getYesNo (metaData.storesLowerCaseIdentifiers ()) });
			pt.addRow (new String[]{ "Stores Mixed Case Identifiers", getYesNo (metaData.storesMixedCaseIdentifiers ()) });

			pt.addRow (new String[]{ "Supports Mixed Case Quoted Identifiers", getYesNo (metaData.supportsMixedCaseQuotedIdentifiers ()) });
			pt.addRow (new String[]{ "Stores Upper Case Quoted Identifiers", getYesNo (metaData.storesUpperCaseQuotedIdentifiers ()) });
			pt.addRow (new String[]{ "Stores Lower Case Quoted Identifiers", getYesNo (metaData.storesLowerCaseQuotedIdentifiers ()) });
			pt.addRow (new String[]{ "Stores Mixed Case Quoted Identifiers", getYesNo (metaData.storesMixedCaseQuotedIdentifiers ()) });
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		interpreter.print (pt);
	}

	private void listLimits (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
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
			pt.addRow (new String[]{ "Max row size include BLOB", getYesNo (metaData.doesMaxRowSizeIncludeBlobs ()) });
			pt.addRow (new String[]{ "Max statement length", getNumberString (metaData.getMaxStatementLength ()) });
			pt.addRow (new String[]{ "Max statements", getNumberString (metaData.getMaxStatements ()) });
			pt.addRow (new String[]{ "Max table name length", getNumberString (metaData.getMaxTableNameLength ()) });
			pt.addRow (new String[]{ "Max tables in SELECT", getNumberString (metaData.getMaxTablesInSelect ()) });
			pt.addRow (new String[]{ "Max user name length", getNumberString (metaData.getMaxUserNameLength ()) });
			pt.addRow (new String[]{ "Max logical LOB size", getNumberString (metaData.getMaxLogicalLobSize ()) });
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		interpreter.print (pt);
	}

	private void listSchemas (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getSchemas ();
			interpreter.print (helper.getResultSet (rs, interpreter));
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void listCatalogs (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getCatalogs ();
			interpreter.print (helper.getResultSet (rs, interpreter));
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}


	private void listTableTypes (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getTableTypes ();
			interpreter.print (helper.getResultSet (rs, interpreter));
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void listTypes (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getTypeInfo ();
			interpreter.print (helper.getResultSet (rs, interpreter));
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void listClient (JaqyInterpreter interpreter, DatabaseMetaData metaData, JaqyHelper helper) throws SQLException
	{
		ResultSet rs = null;
		try
		{
			rs = metaData.getClientInfoProperties ();
			interpreter.print (helper.getResultSet (rs, interpreter));
		}
		catch (SQLException ex)
		{
			throw ex;
		}
		catch (Throwable t)
		{
			interpreter.getGlobals ().log (Level.INFO, t);
		}
		finally
		{
			try
			{
				if (rs != null)
					rs.close ();
			}
			catch (Exception ex)
			{
			}
		}
	}

	private void listTypeMap (JaqyInterpreter interpreter, JaqyHelper helper) throws SQLException
	{
		TypeMap typeMap = helper.getTypeMap (false);
		JaqyDefaultResultSet rs = TypeMap.getTypeMapTable (typeMap);
		interpreter.print (rs);
		rs.close ();
	}

	private void listImportTypeMap (JaqyInterpreter interpreter, JaqyHelper helper) throws SQLException
	{
		TypeMap typeMap = helper.getTypeMap (true);
		JaqyDefaultResultSet rs = TypeMap.getTypeMapTable (typeMap);
		interpreter.print (rs);
		rs.close ();
	}

	private static String getNumberString (int value)
	{
		return Integer.toString (value);
	}

	private static String getNumberString (long value)
	{
		return Long.toString (value);
	}
}
