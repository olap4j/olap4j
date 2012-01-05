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
package org.olap4j.type;

/**
 * Subclass of {@link NumericType} which guarantees fixed number of decimal
 * places. In particular, a decimal with zero scale is an integer.
 *
 * @author jhyde
 * @since May 3, 2005
 * @version $Id$
 */
public class DecimalType extends NumericType {
    private final int precision;
    private final int scale;

    /**
     * Creates a decimal type with precision and scale.
     *
     * <p>Examples:<ul>
     * <li>123.45 has precision 5, scale 2.
     * <li>12,345,000 has precision 5, scale -3.
     * </ul>
     *
     * <p>The largest value is 10 ^ (precision - scale). Hence the largest
     * <code>DECIMAL(5, -3)</code> value is 10 ^ 8.
     *
     * @param precision Maximum number of decimal digits which a value of
     *   this type can have.
     *   Must be greater than zero.
     *   Use {@link Integer#MAX_VALUE} if the precision is unbounded.
     * @param scale Number of digits to the right of the decimal point.
     */
    public DecimalType(int precision, int scale) {
        super();
        assert precision > 0 : "expected precision > 0";
        this.precision = precision;
        this.scale = scale;
    }

    /**
     * Returns the maximum number of decimal digits which a value of
     * this type can have.
     *
     * @return maximum precision allowed for values of this type
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * Returns the number of digits to the right of the decimal point.
     *
     * @return number of digits to the right of the decimal point
     */
    public int getScale() {
        return scale;
    }

    public String toString() {
        return precision == Integer.MAX_VALUE
            ? "DECIMAL(" + scale + ")"
            : "DECIMAL(" + precision + ", " + scale + ")";
    }
}

// End DecimalType.java
