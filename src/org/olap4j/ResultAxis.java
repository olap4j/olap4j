/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * <code>ResultAxis</code> ...
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface ResultAxis {
    int SLICER = 0;
    int COLUMNS = 1;
    int ROWS = 2;

    /**
     * Returns a list of positions on this axis.
     * @return
     */
    List<ResultPosition> getPositions();

    /**
     * Opens an iterator
     */
    ListIterator<ResultPosition> iterate();

    enum Direction {
        ForwardOnly,
        Restartable,
    }
}

// End ResultAxis.java
