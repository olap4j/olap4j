/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.Named;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.*;

/**
 * Implementation of {@link org.olap4j.metadata.Dimension}
 * for XML/A providers.
 *
 * @author jhyde
 * @since Dec 4, 2007
 */
class XmlaOlap4jDimension
    extends XmlaOlap4jElement
    implements Dimension, Named
{
    final XmlaOlap4jCube olap4jCube;
    final Type type;
    final NamedList<XmlaOlap4jHierarchy> hierarchies;
    private final String defaultHierarchyUniqueName;
    private final int ordinal;

    XmlaOlap4jDimension(
        XmlaOlap4jCube olap4jCube,
        String uniqueName,
        String name,
        String caption,
        String description,
        Type type,
        String defaultHierarchyUniqueName,
        int ordinal)
    {
        super(uniqueName, name, caption, description);
        this.defaultHierarchyUniqueName = defaultHierarchyUniqueName;
        assert olap4jCube != null;
        this.olap4jCube = olap4jCube;
        this.type = type;
        this.ordinal = ordinal;

        String[] dimensionRestrictions = {
            "CATALOG_NAME",
            olap4jCube.olap4jSchema.olap4jCatalog.getName(),
            "SCHEMA_NAME",
            olap4jCube.olap4jSchema.getName(),
            "CUBE_NAME",
            olap4jCube.getName(),
            "DIMENSION_UNIQUE_NAME",
            getUniqueName()
        };

        this.hierarchies = new DeferredNamedListImpl<XmlaOlap4jHierarchy>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_HIERARCHIES,
            new XmlaOlap4jConnection.Context(
                olap4jCube.olap4jSchema.olap4jCatalog
                    .olap4jDatabaseMetaData.olap4jConnection,
                olap4jCube.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData,
                olap4jCube.olap4jSchema.olap4jCatalog,
                olap4jCube.olap4jSchema,
                olap4jCube,
                this, null, null),
            new XmlaOlap4jConnection.HierarchyHandler(olap4jCube),
            dimensionRestrictions);
    }

    public NamedList<Hierarchy> getHierarchies() {
        return Olap4jUtil.cast(hierarchies);
    }

    public Type getDimensionType() throws OlapException {
        return type;
    }

    public Hierarchy getDefaultHierarchy() {
        for (XmlaOlap4jHierarchy hierarchy : hierarchies) {
            if (hierarchy.getUniqueName().equals(defaultHierarchyUniqueName)) {
                return hierarchy;
            }
        }
        return hierarchies.get(0);
    }

    public boolean equals(Object obj) {
        return (obj instanceof XmlaOlap4jDimension)
            && this.uniqueName.equals(
                ((XmlaOlap4jDimension) obj).getUniqueName());
    }

    public int getOrdinal() {
        return ordinal;
    }
}

// End XmlaOlap4jDimension.java
