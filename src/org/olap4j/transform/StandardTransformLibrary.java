/*
// $Id$
//
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
package org.olap4j.transform;

import org.olap4j.Axis;
import org.olap4j.CellSet;

/**
 * Standard transformations library
 *
 * NOTE: is this really needed since transforms' ctors have the same
 * parameters as these functions? This serves only as a place to conveniently
 * regroup transforms in a "library".
 *
 * @author etdub
 * @author jhyde
 * @version $Id$
 * @since Jul 28, 2008
 */
public class StandardTransformLibrary {

    public static MdxQueryTransform createDrillReplaceTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        return new DrillReplaceTransform(
            axis,
            positionOrdinalInAxis,
            memberOrdinalInPosition,
            cellSet);
    }

    public static MdxQueryTransform createDrillDownOnPositionTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        return new DrillDownOnPositionTransform(
            axis,
            positionOrdinalInAxis,
            memberOrdinalInPosition,
            cellSet);
    }

    public static MdxQueryTransform createRollUpLevelTransform(
        Axis axis,
        int positionOrdinalInAxis,
        int memberOrdinalInPosition,
        CellSet cellSet)
    {
        return new RollUpLevelTransform(
            axis,
            positionOrdinalInAxis,
            memberOrdinalInPosition,
            cellSet);
    }

    // many other transforms ...
}

// End StandardTransformLibrary.java
