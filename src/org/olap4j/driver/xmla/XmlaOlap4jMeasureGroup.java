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
import org.olap4j.impl.NamedListImpl;
import org.olap4j.impl.Olap4jUtil;
import org.olap4j.metadata.*;

/**
 * Implementation of {@link org.olap4j.metadata.MeasureGroup}
 * for XML/A providers.
 */
class XmlaOlap4jMeasureGroup
    extends XmlaOlap4jElement
    implements MeasureGroup, Named
{
    final XmlaOlap4jCube olap4jCube;

    final NamedList<XmlaOlap4jMeasureGroupDimension> measureGroupDimensions;
    final NamedList<XmlaOlap4jMeasure> measures =
        new NamedListImpl<XmlaOlap4jMeasure>();
    private final boolean writeEnabled;

    XmlaOlap4jMeasureGroup(
        XmlaOlap4jCube olap4jCube,
        String uniqueName,
        String name,
        String caption,
        String description,
        boolean writeEnabled) throws OlapException
    {
        super(uniqueName, name, caption, description);
        assert olap4jCube != null;
        this.olap4jCube = olap4jCube;
        this.writeEnabled = writeEnabled;

        final XmlaOlap4jConnection olap4jConnection =
            olap4jCube.olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData
                .olap4jConnection;

        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(olap4jCube, null, null, null);

        String[] restrictions = {
            "CATALOG_NAME",
            olap4jCube.olap4jSchema.olap4jCatalog.getName(),
            "SCHEMA_NAME",
            olap4jCube.olap4jSchema.getName(),
            "CUBE_NAME",
            olap4jCube.getName(),
            "MEASUREGROUP_NAME",
            getName()
        };

        this.measureGroupDimensions =
            new DeferredNamedListImpl<XmlaOlap4jMeasureGroupDimension>(
                XmlaOlap4jConnection.MetadataRequest
                    .MDSCHEMA_MEASUREGROUP_DIMENSIONS,
                context,
                new XmlaOlap4jConnection.MeasureGroupDimensionHandler(this),
                restrictions);

        // REVIEW: Measures can be taken from the cube.
        // REVIEW: Subset of measures.
        olap4jConnection.populateList(
            measures,
            context,
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEASURES,
            new XmlaOlap4jConnection.MeasureHandler(),
            restrictions);
    }

    public boolean equals(Object obj) {
        return obj == this
            || obj instanceof XmlaOlap4jMeasureGroup
            && uniqueName.equals(((XmlaOlap4jMeasureGroup) obj).uniqueName);
    }

    public Cube getCube() {
        return olap4jCube;
    }

    public NamedList<MeasureGroupDimension> getDimensions() {
        return Olap4jUtil.cast(measureGroupDimensions);
    }

    public NamedList<Measure> getMeasures() {
        return Olap4jUtil.cast(measures);
    }

    public boolean isWriteEnabled() {
        return writeEnabled;
    }
}

// End XmlaOlap4jMeasureGroup.java
