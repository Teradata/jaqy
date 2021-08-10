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
package com.teradata.jaqy.connection;

/**
 * This class contains a list of flags to indicate JDBC features NOT
 * supported.  Thus, when a flag is false, the feature is supported.
 * The flags should be named so to reflect this behavior.
 *
 * @author  Heng Yuan
 */
public class JdbcFeatures
{
    //----------- Connection -------------------------------
    /** Does not support catalog */
    public boolean noCatalog;
    /** Does not support schema */
    public boolean noSchema;

    // ---------- Data types -------------------------
    /** Does the driver supports CharacterStream / BinaryStream? */
    public boolean noStream;
    /** Does the stream requiring the length parameter */
    public boolean streamLength = true;
    /** Does the setObject requiring the data type */
    public boolean setObjectType = true;

    // ---------- DatabaseMetaData -------------------------

    //----------- ResultSet --------------------------------
    /** Only supports TYPE_FORWARD_ONLY */
    public boolean forwardOnlyRS;

    //----------- ResultSetMetaData ------------------------
    /** Does not support RSMD.getCatalogName () */
    public boolean noRSMDCatalog;
    /** Does not support RSMD.getSchemaName () */
    public boolean noRSMDSchema;
    /** Does not support RSMD.getTableName () */
    public boolean noRSMDTable;
    /** Does not support RSMD.isSearchable () */
    public boolean noRSMDSearchable;
    /** Does not support RSMD.isSigned () */
    public boolean noRSMDSigned;
    /** Does not support RSMD.isDefinitelyWritable () */
    public boolean noRSMDDefinitelyWritable;
    /** Does not support RSMD.isWritable () */
    public boolean noRSMDWritable;
}
