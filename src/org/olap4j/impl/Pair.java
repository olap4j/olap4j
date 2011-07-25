/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import java.util.Map;

/**
 * Pair of values.
 *
 * <p>Because a pair implements {@link #equals(Object)}, {@link #hashCode()} and
 * {@link #compareTo(Pair)}, it can be used in any kind of
 * {@link java.util.Collection}.
 *
 * @author jhyde
 * @version $Id$
 * @since Apr 19, 2007
 */
class Pair<L, R>
    implements Comparable<Pair<L, R>>, Map.Entry<L, R>
{
    public L left;
    public R right;

    /**
     * Creates a pair.
     *
     * @param left Left value
     * @param right Right value
     */
    public Pair(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair<L, R> pair = (Pair<L, R>) obj;
            return Olap4jUtil.equal(this.left, pair.left)
                && Olap4jUtil.equal(this.right, pair.right);
        }
        return false;
    }

    public int hashCode() {
        // formula required by Map contract
        final int k = left == null ? 0 : left.hashCode();
        final int k1 = right == null ? 0 : right.hashCode();
        return k ^ k1;
    }

    public int compareTo(Pair<L, R> that) {
        int c = compare((Comparable) this.left, (Comparable)that.left);
        if (c == 0) {
            c = compare((Comparable) this.right, (Comparable)that.right);
        }
        return c;
    }

    public String toString() {
        return "<" + left + ", " + right + ">";
    }

    // implement Map.Entry
    public L getKey() {
        return left;
    }

    // implement Map.Entry
    public R getValue() {
        return right;
    }

    // implement Map.Entry
    public R setValue(R value) {
        R previous = right;
        right = value;
        return previous;
    }

    private static <C extends Comparable<C>> int compare(C c1, C c2) {
        if (c1 == null) {
            if (c2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (c2 == null) {
            return 1;
        } else {
            return c1.compareTo(c2);
        }
    }
}

// End Pair.java
