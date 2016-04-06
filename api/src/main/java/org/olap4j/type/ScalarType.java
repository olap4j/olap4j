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
package org.olap4j.type;

import org.olap4j.metadata.*;

/**
 * Base class for types which represent scalar values.
 *
 * <p>An instance of this class means a scalar value of unknown type.
 * Usually one of the derived classes {@link NumericType},
 * {@link StringType}, {@link BooleanType} is used instead.
 *
 * @author jhyde
 * @since Feb 17, 2005
 */
public class ScalarType implements Type {
    public boolean usesDimension(Dimension dimension, boolean maybe) {
        return false;
    }

    public Hierarchy getHierarchy() {
        return null;
    }

    public Level getLevel() {
        return null;
    }

    public Dimension getDimension() {
        return null;
    }
}

// End ScalarType.java
