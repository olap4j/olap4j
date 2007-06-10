/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.metadata.Member;

/**
 * Contains factory methods for creating {@link SelectionImpl}s.
 *
 * <p>Created using {@link Query#getSelectionFactory()}.
 *
 * @author jhyde
 * @version $Id$
 * @since May 30, 2007
 */
public class SelectionFactory {
    private final Query query;

    /**
     * Creates a SelectionFactory. Called from {@link Query}.
     * 
     * @param query Owning query
     */
    SelectionFactory(Query query) {
        this.query = query;
    }

    Selection createMemberSelection(Member member) {
        throw new UnsupportedOperationException();
    }
}

// End SelectionFactory.java
