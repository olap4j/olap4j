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
package org.olap4j.metadata;

import java.sql.Wrapper;
import java.util.List;

/**
 * Use of a {@link Dimension} within a {@link MeasureGroup}.
 */
public interface MeasureGroupDimension extends Wrapper {
    /**
     * Returns the {@link MeasureGroup} that this {@code MeasureGroupDimension}
     * belongs to.
     *
     * @return measure group this measure group dimension belongs to; never null
     */
    MeasureGroup getMeasureGroup();

    /**
     * Returns the {@link Dimension} that this {@code MeasureGroupDimension}
     * is a use of.
     *
     * @return dimension this measure group dimension is a use of; never null
     */
    Dimension getDimension();

    /**
     * Returns a list of dimensions for the reference dimension.
     *
     * @return list of dimensions for the reference dimension
     */
    List<Dimension> getDimensionPath();

    /**
     * Returns the {@link Hierarchy} that determines the granularity this of
     * the {@link MeasureGroup} with respect to this {@link Dimension}.
     * Returns {@code null} if the dimension is at root granularity.
     *
     * @return granularity hierarchy
     */
    Hierarchy getGranularityHierarchy();

    /**
     * Returns the number of instances a measure in the measure group can have
     * for a single dimension member. Possible values include: "ONE", "MANY".
     */
    String getMeasureGroupCardinality();

    /**
     * Returns the number of instances a dimension member in the measure group
     * can have for a single instance of a measure group measure. Possible
     * values include: "ONE", "MANY".
     */
    String getDimensionCardinality();

    /**
     * Returns whether the dimension is a fact dimension.
     */
    boolean isFactDimension();

}

// End MeasureGroupDimension.java
