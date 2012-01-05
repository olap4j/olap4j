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
package org.olap4j.impl;

import java.util.Iterator;

/**
 * Iterator over the coordinates of a hyper-rectangle.
 *
 * <p>For example, {@code new CoordinateIterator(new int[] {3, 2})} generates
 * the pairs {@code {0, 0}, {0, 1}, {1, 0}, {1, 1}, {2, 0}, {2, 1} }.
 *
 * @author jhyde
 * @version $Id$
 * @since Apr 7, 2009
 */
public class CoordinateIterator implements Iterator<int[]> {
    private final int[] dimensions;
    private final boolean littleEndian;
    private final int[] current;
    private boolean hasNext;

    /**
     * Creates a big-endian coordinate iterator.
     *
     * @param dimensions Array containing the number of elements of each
     * coordinate axis
     */
    public CoordinateIterator(int[] dimensions) {
        this(dimensions, false);
    }

    /**
     * Creates a coordinate iterator.
     *
     * @param dimensions Array containing the number of elements of each
     * @param littleEndian Whether coordinate 0 is the least significant
     * (and fastest changing) coordinate
     */
    public CoordinateIterator(int[] dimensions, boolean littleEndian) {
        this.dimensions = dimensions;
        this.littleEndian = littleEndian;
        this.current = new int[dimensions.length];
        this.hasNext = true;
        for (int dimension : dimensions) {
            if (dimension <= 0) {
                // an axis is empty. no results will be produced
                hasNext = false;
                break;
            }
        }
    }

    /**
     * Creates an {@link Iterable} that yields a
     * {@link org.olap4j.impl.CoordinateIterator}.
     *
     * <p>Useful in a foreach loop, for example:
     *
     * <blockquote>int[] dimensions;
     * for (int[] coords : CoordinateIterator.iterate(dimensions)) {
     *     foo(coords);
     * }
     * </blockquote>
     *
     * @param dimensions Array containing the number of elements of each
     * coordinate axis
     *
     * @return Iterable
     */
    public static Iterable<int[]> iterate(final int[] dimensions) {
        return new Iterable<int[]>() {
            public Iterator<int[]> iterator() {
                return new CoordinateIterator(dimensions, true);
            }
        };
    }

    public boolean hasNext() {
        return hasNext;
    }

    public int[] next() {
        final int[] result = current.clone();
        moveToNext();
        return result;
    }

    private void moveToNext() {
        if (littleEndian) {
            for (int offset = 0; offset < dimensions.length; ++offset) {
                int k = ++current[offset];
                if (k < dimensions[offset]) {
                    return;
                }
                current[offset] = 0;
            }
        } else {
            for (int offset = dimensions.length - 1; offset >= 0; --offset) {
                int k = ++current[offset];
                if (k < dimensions[offset]) {
                    return;
                }
                current[offset] = 0;
            }
        }
        hasNext = false;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}

// End CoordinateIterator.java
