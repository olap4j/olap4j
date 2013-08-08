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

import java.util.List;

public interface MeasureGroup extends MetadataElement {
    /**
     * Returns the {@link Cube} this {@code MeasureGroup} belongs to.
     *
     * @return cube this measure group belongs to; never null
     */
    Cube getCube();

    /**
     * Returns the {@link MeasureGroupDimension}s in this {@code MeasureGroup}.
     *
     * @return dimension uses in this measure group
     */
    NamedList<MeasureGroupDimension> getDimensions();

    /**
     * Returns the measures in this {@code MeasureGroup}. The measures belong to
     * the same cube, but may be a subset.
     *
     * @see org.olap4j.metadata.Cube#getMeasures()
     *
     * @return measures in this measure group
     */
    // NOTE: This is a NamedList whereas Cube.getMeasures is just a List. Why?
    // Because Cube measures include calculated measures, so the names may not
    // be unique. Measure group measures are all stored, so have unique names.
    NamedList<Measure> getMeasures();

    /**
     * Returns whether this {@code MeasureGroup} is write-enabled.
     */
    boolean isWriteEnabled();
}

// End MeasureGroup.java
