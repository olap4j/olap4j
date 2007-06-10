/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

import java.io.PrintWriter;

/**
 * <code>ParseTreeWriter</code> ...
 *
 * @author jhyde
 * @version $Id: $
 * @since Jun 4, 2007
 */
public class ParseTreeWriter {
    private final PrintWriter pw;

    public ParseTreeWriter(PrintWriter pw) {
        this.pw = pw;
    }

    public PrintWriter getPrintWriter() {
        return pw;
    }
}

// End ParseTreeWriter.java
