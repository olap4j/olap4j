/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2011 Julian Hyde and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import java.util.Collections;
import java.util.List;

import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.Catalog;
import org.olap4j.metadata.Database;
import org.olap4j.metadata.NamedList;

/**
 * XMLA implementation of a database metadata object.
 * @version $Id:$
 * @author LBoudreau
 */
class XmlaOlap4jDatabase implements Database, Named {

    private final NamedList<XmlaOlap4jCatalog> catalogs;
    private final XmlaOlap4jConnection olap4jConnection;
    private final String name;
    private final String description;
    private final String providerName;
    private final String url;
    private final String dataSourceInfo;
    private final List<ProviderType> providerType;
    private final List<AuthenticationMode> authenticationMode;

    public XmlaOlap4jDatabase(
            XmlaOlap4jConnection olap4jConnection,
            String name,
            String description,
            String providerName,
            String url,
            String dataSourceInfo,
            List<ProviderType> providerType,
            List<AuthenticationMode> authenticationMode)
    {
        this.olap4jConnection = olap4jConnection;
        this.name = name;
        this.description = description;
        this.providerName = providerName;
        this.url = url;
        this.dataSourceInfo = dataSourceInfo;
        this.providerType =
            Collections.unmodifiableList(providerType);
        this.authenticationMode =
            Collections.unmodifiableList(authenticationMode);
        this.catalogs =
            new DeferredNamedListImpl<XmlaOlap4jCatalog>(
                XmlaOlap4jConnection.MetadataRequest.DBSCHEMA_CATALOGS,
                new XmlaOlap4jConnection.Context(
                    olap4jConnection,
                    (XmlaOlap4jDatabaseMetaData) olap4jConnection
                        .getMetaData(),
                    null, null, null, null, null, null),
                new XmlaOlap4jConnection.CatalogHandler(this),
                null);
    }

    public List<AuthenticationMode> getAuthenticationModes()
            throws OlapException
    {
        return authenticationMode;
    }

    public NamedList<Catalog> getCatalogs() throws OlapException {
        return Olap4jUtil.cast(catalogs);
    }

    public String getDescription() throws OlapException {
        return this.description;
    }

    public String getName() {
        return this.name;
    }

    public OlapConnection getOlapConnection() {
        return this.olap4jConnection;
    }

    public String getProviderName() throws OlapException {
        return this.providerName;
    }

    public List<ProviderType> getProviderTypes() throws OlapException {
        return this.providerType;
    }

    public String getURL() throws OlapException {
        return this.url;
    }

    public String getDataSourceInfo() throws OlapException {
        return this.dataSourceInfo;
    }
}
// End XmlaOlap4jDatabase