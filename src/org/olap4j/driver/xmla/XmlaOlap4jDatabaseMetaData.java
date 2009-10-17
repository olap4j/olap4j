/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.*;
import org.olap4j.impl.*;
import org.olap4j.metadata.*;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link org.olap4j.OlapDatabaseMetaData}
 * for XML/A providers.
 *
 * <p>This class has sub-classes which implement JDBC 3.0 and JDBC 4.0 APIs;
 * it is instantiated using {@link Factory#newDatabaseMetaData}.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since May 23, 2007
 */
abstract class XmlaOlap4jDatabaseMetaData implements OlapDatabaseMetaData {
    final XmlaOlap4jConnection olap4jConnection;

    private final NamedList<XmlaOlap4jCatalog> catalogs;

    /**
     * Creates an XmlaOlap4jDatabaseMetaData.
     *
     * <p>Note that this constructor should make zero non-trivial calls, which
     * could cause deadlocks due to java.sql.DriverManager synchronization
     * issues.
     *
     * @param olap4jConnection Connection
     */
    XmlaOlap4jDatabaseMetaData(
        XmlaOlap4jConnection olap4jConnection)
    {
        this.olap4jConnection = olap4jConnection;
        this.catalogs =
            new DeferredNamedListImpl<XmlaOlap4jCatalog>(
                XmlaOlap4jConnection.MetadataRequest.DBSCHEMA_CATALOGS,
                new XmlaOlap4jConnection.Context(
                    olap4jConnection, this, null, null, null, null, null,
                    null),
                new XmlaOlap4jConnection.CatalogHandler(),
                null);
    }

    /**
     * Returns a list of catalogs in this database.
     *
     * <p>Package-local because not part of olap4j API.
     *
     * @return List of catalog objects
     */
    NamedList<Catalog> getCatalogObjects() {
        return Olap4jUtil.cast(catalogs);
    }

    /**
     * Executes a metadata query and returns the result as a JDBC
     * {@link ResultSet}.
     *
     * @param metadataRequest Name of the metadata request. Corresponds to the
     * XMLA method name, e.g. "MDSCHEMA_CUBES"
     *
     * @param patternValues Array of alternating parameter name and value
     * pairs. If the parameter value is null, it is ignored.
     *
     * @return Result set of metadata
     *
     * @throws org.olap4j.OlapException on error
     */
    private ResultSet getMetadata(
        XmlaOlap4jConnection.MetadataRequest metadataRequest,
        Object... patternValues) throws OlapException
    {
        assert patternValues.length % 2 == 0;
        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(
                olap4jConnection, null, null, null, null, null, null, null);
        List<String> patternValueList = new ArrayList<String>();
        Map<String, Matcher> predicateList = new HashMap<String, Matcher>();
        for (int i = 0; i < patternValues.length; i += 2) {
            String name = (String) patternValues[i];
            assert metadataRequest.getColumn(name) != null
                : "Request '" + metadataRequest
                + "' does not support column '" + name + "'";
            Object value = patternValues[i + 1];
            if (value == null) {
                // ignore
            } else if (value instanceof Wildcard) {
                final Wildcard wildcard = (Wildcard) value;
                if (wildcard.pattern.indexOf('%') < 0
                    && wildcard.pattern.indexOf('_') < 0)
                {
                    patternValueList.add(name);
                    patternValueList.add(wildcard.pattern);
                } else {
                    String regexp =
                        Olap4jUtil.wildcardToRegexp(
                            Collections.singletonList(wildcard.pattern));
                    final Matcher matcher = Pattern.compile(regexp).matcher("");
                    predicateList.put(name, matcher);
                }
            } else {
                patternValueList.add(name);
                patternValueList.add((String) value);
            }
        }

        String request =
            olap4jConnection.generateRequest(
                context,
                metadataRequest,
                patternValueList.toArray(
                    new String[patternValueList.size()]));

        final Element root = olap4jConnection.xxx(request);
        List<List<Object>> rowList = new ArrayList<List<Object>>();
        rowLoop:
        for (Element row : XmlaOlap4jUtil.childElements(root)) {
            final ArrayList<Object> valueList = new ArrayList<Object>();
            for (Map.Entry<String, Matcher> entry : predicateList.entrySet()) {
                final String column = entry.getKey();
                final String value =
                    XmlaOlap4jUtil.stringElement(row, column);
                final Matcher matcher = entry.getValue();
                if (!matcher.reset(value).matches()) {
                    continue rowLoop;
                }
            }
            for (XmlaOlap4jConnection.MetadataColumn column
                : metadataRequest.columns)
            {
                final String value =
                    XmlaOlap4jUtil.stringElement(row, column.xmlaName);
                valueList.add(value);
            }
            rowList.add(valueList);
        }
        List<String> headerList = new ArrayList<String>();
        for (XmlaOlap4jConnection.MetadataColumn column
            : metadataRequest.columns)
        {
            headerList.add(column.name);
        }
        return olap4jConnection.factory.newFixedResultSet(
            olap4jConnection, headerList, rowList);
    }

    /**
     * Converts a string to a wildcard object.
     *
     * @param pattern String pattern
     * @return wildcard object, or null if pattern was null
     */
    private Wildcard wildcard(String pattern) {
        return pattern == null
            ? null
            : new Wildcard(pattern);
    }

    // implement DatabaseMetaData

    public boolean allProceduresAreCallable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean allTablesAreSelectable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getURL() throws SQLException {
        return olap4jConnection.getURL();
    }

    public String getUserName() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly() throws SQLException {
        // olap4j does not currently support writeback
        return true;
    }

    public boolean nullsAreSortedHigh() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean nullsAreSortedLow() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean nullsAreSortedAtStart() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean nullsAreSortedAtEnd() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getDatabaseProductName() throws SQLException {
        throw Olap4jUtil.needToImplement(this);
    }

    public String getDatabaseProductVersion() throws SQLException {
        throw Olap4jUtil.needToImplement(this);
    }

    public String getDriverName() throws SQLException {
        return olap4jConnection.driver.getName();
    }

    public String getDriverVersion() throws SQLException {
        return olap4jConnection.driver.getVersion();
    }

    public int getDriverMajorVersion() {
        return olap4jConnection.driver.getMajorVersion();
    }

    public int getDriverMinorVersion() {
        return olap4jConnection.driver.getMinorVersion();
    }

    public boolean usesLocalFiles() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean usesLocalFilePerTable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsMixedCaseIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean storesUpperCaseIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean storesLowerCaseIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean storesMixedCaseIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getIdentifierQuoteString() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getSQLKeywords() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getNumericFunctions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getStringFunctions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getSystemFunctions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getTimeDateFunctions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getSearchStringEscape() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getExtraNameCharacters() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsAlterTableWithAddColumn() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsAlterTableWithDropColumn() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsColumnAliasing() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean nullPlusNonNullIsNull() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsConvert() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsConvert(
        int fromType, int toType) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsTableCorrelationNames() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsDifferentTableCorrelationNames()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsExpressionsInOrderBy() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsOrderByUnrelated() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsGroupBy() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsGroupByUnrelated() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsGroupByBeyondSelect() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsLikeEscapeClause() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsMultipleResultSets() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsMultipleTransactions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsNonNullableColumns() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsMinimumSQLGrammar() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsCoreSQLGrammar() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsExtendedSQLGrammar() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsANSI92EntryLevelSQL() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsANSI92IntermediateSQL() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsANSI92FullSQL() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsIntegrityEnhancementFacility() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsOuterJoins() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsFullOuterJoins() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsLimitedOuterJoins() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getSchemaTerm() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getProcedureTerm() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getCatalogTerm() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isCatalogAtStart() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getCatalogSeparator() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSchemasInDataManipulation() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSchemasInProcedureCalls() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSchemasInTableDefinitions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSchemasInIndexDefinitions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsCatalogsInDataManipulation() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsCatalogsInProcedureCalls() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsCatalogsInTableDefinitions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsCatalogsInPrivilegeDefinitions()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsPositionedDelete() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsPositionedUpdate() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSelectForUpdate() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsStoredProcedures() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSubqueriesInComparisons() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSubqueriesInExists() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSubqueriesInIns() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsSubqueriesInQuantifieds() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsCorrelatedSubqueries() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsUnion() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsUnionAll() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxBinaryLiteralLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxCharLiteralLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxColumnNameLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxColumnsInGroupBy() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxColumnsInIndex() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxColumnsInOrderBy() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxColumnsInSelect() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxColumnsInTable() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxConnections() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxCursorNameLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxIndexLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxSchemaNameLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxProcedureNameLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxCatalogNameLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxRowSize() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxStatementLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxStatements() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxTableNameLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxTablesInSelect() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getMaxUserNameLength() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getDefaultTransactionIsolation() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsTransactions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsTransactionIsolationLevel(int level)
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsDataDefinitionAndDataManipulationTransactions()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsDataManipulationTransactionsOnly()
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getProcedures(
        String catalog,
        String schemaPattern,
        String procedureNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getProcedureColumns(
        String catalog,
        String schemaPattern,
        String procedureNamePattern,
        String columnNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getTables(
        String catalog,
        String schemaPattern,
        String tableNamePattern,
        String types[]) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getSchemas() throws SQLException {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.DBSCHEMA_SCHEMATA);
    }

    public ResultSet getCatalogs() throws SQLException {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.DBSCHEMA_CATALOGS);
    }

    public ResultSet getTableTypes() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getColumns(
        String catalog,
        String schemaPattern,
        String tableNamePattern,
        String columnNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getColumnPrivileges(
        String catalog,
        String schema,
        String table,
        String columnNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getTablePrivileges(
        String catalog,
        String schemaPattern,
        String tableNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getBestRowIdentifier(
        String catalog,
        String schema,
        String table,
        int scope,
        boolean nullable) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getVersionColumns(
        String catalog, String schema, String table) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getPrimaryKeys(
        String catalog, String schema, String table) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getImportedKeys(
        String catalog, String schema, String table) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getExportedKeys(
        String catalog, String schema, String table) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getCrossReference(
        String parentCatalog,
        String parentSchema,
        String parentTable,
        String foreignCatalog,
        String foreignSchema,
        String foreignTable) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getTypeInfo() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getIndexInfo(
        String catalog,
        String schema,
        String table,
        boolean unique,
        boolean approximate) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsResultSetType(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsResultSetConcurrency(
        int type, int concurrency) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean ownUpdatesAreVisible(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean ownDeletesAreVisible(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean ownInsertsAreVisible(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean othersUpdatesAreVisible(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean othersDeletesAreVisible(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean othersInsertsAreVisible(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean updatesAreDetected(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean deletesAreDetected(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean insertsAreDetected(int type) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsBatchUpdates() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getUDTs(
        String catalog,
        String schemaPattern,
        String typeNamePattern,
        int[] types) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public OlapConnection getConnection() {
        return olap4jConnection;
    }

    public boolean supportsSavepoints() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsNamedParameters() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsMultipleOpenResults() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsGetGeneratedKeys() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public ResultSet getSuperTypes(
        String catalog,
        String schemaPattern,
        String typeNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getSuperTables(
        String catalog,
        String schemaPattern,
        String tableNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public ResultSet getAttributes(
        String catalog,
        String schemaPattern,
        String typeNamePattern,
        String attributeNamePattern) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public boolean supportsResultSetHoldability(int holdability)
        throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    public int getResultSetHoldability() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getDatabaseMajorVersion() throws SQLException {
        throw Olap4jUtil.needToImplement(this);
    }

    public int getDatabaseMinorVersion() throws SQLException {
        throw Olap4jUtil.needToImplement(this);
    }

    public int getJDBCMajorVersion() throws SQLException {
        // this driver supports jdbc 3.0 and jdbc 4.0
        // FIXME: should return 3 if the current connection is jdbc 3.0
        return 4;
    }

    public int getJDBCMinorVersion() throws SQLException {
        // this driver supports jdbc 3.0 and jdbc 4.0
        return 0;
    }

    public int getSQLStateType() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean locatorsUpdateCopy() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean supportsStatementPooling() throws SQLException {
        throw new UnsupportedOperationException();
    }

    // implement java.sql.Wrapper

    // straightforward implementation of unwrap and isWrapperFor, since this
    // class already implements the interface they most likely require:
    // DatabaseMetaData and OlapDatabaseMetaData

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        }
        throw getHelper().createException(
            "does not implement '" + iface + "'");
    }

    /**
     * Returns the error-handler.
     *
     * @return Error handler
     */
    private final XmlaHelper getHelper() {
        return olap4jConnection.helper;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    // implement OlapDatabaseMetaData

    public ResultSet getActions(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String actionNamePattern) throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_ACTIONS,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "ACTION_NAME", wildcard(actionNamePattern));
    }

    public ResultSet getDatasources() throws OlapException {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.DISCOVER_DATASOURCES);
    }

    public ResultSet getLiterals() throws OlapException {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.DISCOVER_LITERALS);
    }

    public ResultSet getDatabaseProperties(
        String dataSourceName,
        String propertyNamePattern) throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.DISCOVER_PROPERTIES);
    }

    public ResultSet getProperties(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyUniqueName,
        String levelUniqueName,
        String memberUniqueName,
        String propertyNamePattern) throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_PROPERTIES,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "DIMENSION_UNIQUE_NAME", dimensionUniqueName,
            "HIERARCHY_UNIQUE_NAME", hierarchyUniqueName,
            "LEVEL_UNIQUE_NAME", levelUniqueName,
            "MEMBER_UNIQUE_NAME", memberUniqueName,
            "PROPERTY_NAME", wildcard(propertyNamePattern));
    }

    public String getMdxKeywords() throws OlapException {
        final XmlaOlap4jConnection.MetadataRequest metadataRequest =
            XmlaOlap4jConnection.MetadataRequest.DISCOVER_KEYWORDS;
        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(
                olap4jConnection, null, null, null, null, null, null, null);
        String request =
            olap4jConnection.generateRequest(
                context, metadataRequest, new Object[0]);
        final Element root = olap4jConnection.xxx(request);
        StringBuilder buf = new StringBuilder();
        for (Element row : XmlaOlap4jUtil.childElements(root)) {
            if (buf.length() > 0) {
                buf.append(',');
            }
            final String keyword =
                XmlaOlap4jUtil.stringElement(row, "Keyword");
            buf.append(keyword);
        }
        return buf.toString();
    }

    public ResultSet getCubes(
        String catalog,
        String schemaPattern,
        String cubeNamePattern)
        throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_CUBES,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern));
    }

    public ResultSet getDimensions(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionNamePattern)
        throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_DIMENSIONS,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "DIMENSION_NAME", wildcard(dimensionNamePattern));
    }

    public ResultSet getOlapFunctions(
        String functionNamePattern) throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_FUNCTIONS,
            "FUNCTION_NAME", wildcard(functionNamePattern));
    }

    public ResultSet getHierarchies(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyNamePattern)
        throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_HIERARCHIES,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "DIMENSION_UNIQUE_NAME", dimensionUniqueName,
            "HIERARCHY_NAME", wildcard(hierarchyNamePattern));
    }

    public ResultSet getMeasures(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String measureNamePattern,
        String measureUniqueName) throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEASURES,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "MEASURE_NAME", wildcard(measureNamePattern),
            "MEASURE_UNIQUE_NAME", measureUniqueName);
    }

    public ResultSet getMembers(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyUniqueName,
        String levelUniqueName,
        String memberUniqueName,
        Set<Member.TreeOp> treeOps) throws OlapException
    {
        String treeOpString;
        if (treeOps != null) {
            int op = 0;
            for (Member.TreeOp treeOp : treeOps) {
                op |= treeOp.xmlaOrdinal();
            }
            treeOpString = String.valueOf(op);
        } else {
            treeOpString = null;
        }
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEMBERS,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "DIMENSION_UNIQUE_NAME", dimensionUniqueName,
            "HIERARCHY_UNIQUE_NAME", hierarchyUniqueName,
            "LEVEL_UNIQUE_NAME", levelUniqueName,
            "MEMBER_UNIQUE_NAME", memberUniqueName,
            "TREE_OP", treeOpString);
    }

    public ResultSet getLevels(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String dimensionUniqueName,
        String hierarchyUniqueName,
        String levelNamePattern) throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_LEVELS,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "DIMENSION_UNIQUE_NAME", dimensionUniqueName,
            "HIERARCHY_UNIQUE_NAME", hierarchyUniqueName,
            "LEVEL_NAME", wildcard(levelNamePattern));
    }

    public ResultSet getSets(
        String catalog,
        String schemaPattern,
        String cubeNamePattern,
        String setNamePattern) throws OlapException
    {
        return getMetadata(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_SETS,
            "CATALOG_NAME", catalog,
            "SCHEMA_NAME", wildcard(schemaPattern),
            "CUBE_NAME", wildcard(cubeNamePattern),
            "SET_NAME", wildcard(setNamePattern));
    }


    /**
     * Wrapper which indicates that a restriction is to be treated as a
     * SQL-style wildcard match.
     */
    static class Wildcard {
        final String pattern;

        /**
         * Creates a Wildcard.
         *
         * @param pattern Pattern
         */
        Wildcard(String pattern) {
            assert pattern != null;
            this.pattern = pattern;
        }
    }
}

// End XmlaOlap4jDatabaseMetaData.java
