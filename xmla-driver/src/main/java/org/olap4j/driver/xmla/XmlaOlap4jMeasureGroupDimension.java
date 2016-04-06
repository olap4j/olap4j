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
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.MeasureGroupDimension;

import java.util.List;

/**
 * Implementation of {@link org.olap4j.metadata.MeasureGroupDimension}
 * for XML/A providers.
 */
class XmlaOlap4jMeasureGroupDimension
    implements MeasureGroupDimension, Named
{
    final XmlaOlap4jMeasureGroup olap4jMeasureGroup;
    final XmlaOlap4jDimension olap4jDimension;
    final XmlaOlap4jHierarchy olap4jGranularityHierarchy; // may be null

    XmlaOlap4jMeasureGroupDimension(
        XmlaOlap4jMeasureGroup olap4jMeasureGroup,
        XmlaOlap4jDimension olap4jDimension,
        XmlaOlap4jHierarchy olap4jGranularityHierarchy) throws OlapException
    {
        assert olap4jMeasureGroup != null;
        assert olap4jDimension != null;
        this.olap4jMeasureGroup = olap4jMeasureGroup;
        this.olap4jDimension = olap4jDimension;
        this.olap4jGranularityHierarchy = olap4jGranularityHierarchy;
    }

    public boolean equals(Object obj) {
        return obj == this
            || obj instanceof XmlaOlap4jMeasureGroupDimension
            && olap4jMeasureGroup.equals(
                ((XmlaOlap4jMeasureGroupDimension) obj).olap4jMeasureGroup)
            && olap4jDimension.equals(
                ((XmlaOlap4jMeasureGroupDimension) obj).olap4jDimension);
    }

    public String getName() {
        return olap4jDimension.name;
    }

    public XmlaOlap4jMeasureGroup getMeasureGroup() {
        return olap4jMeasureGroup;
    }

    public XmlaOlap4jDimension getDimension() {
        return olap4jDimension;
    }

    public XmlaOlap4jHierarchy getGranularityHierarchy() {
        return olap4jGranularityHierarchy;
    }

    public List<Dimension> getDimensionPath() {
        return null; // FIXME
    }

    public String getMeasureGroupCardinality() {
        return null; // FIXME
    }

    public String getDimensionCardinality() {
        return null; // FIXME
    }

    public boolean isFactDimension() {
        return false; // FIXME
    }

    public boolean isWrapperFor(Class<?> iface) {
        return iface.isInstance(this);
    }

    public <T> T unwrap(Class<T> iface) {
        return iface.cast(this);
    }

}

// End XmlaOlap4jMeasureGroupDimension.java
