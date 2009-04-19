/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2009-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.CellSet;

import java.io.PrintWriter;

/**
 * Converts a {@link CellSet} into text.
 *
 * <p><b>This interface is experimental. It is not part of the olap4j
 * specification and is subject to change without notice.</b></p>
 *
 * @author jhyde
 * @version $Id$
 * @since Apr 15, 2009
 */
public interface CellSetFormatter {
    /**
     * Formats a CellSet as text to a PrintWriter.
     *
     * @param cellSet Cell set
     * @param pw Print writer
     */
    void format(
        CellSet cellSet,
        PrintWriter pw);
}

// End CellSetFormatter.java
