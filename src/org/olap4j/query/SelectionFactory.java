/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.metadata.Member;

/**
 * Contains factory methods for creating implementations of {@link Selection}.
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

    Selection createMemberSelection(
        Member member,
        Selection.Operator operator)
    {
        return
            new SelectionImpl(
                member,
                member.getDimension(),
                member.getHierarchy().getUniqueName(),
                member.getLevel().getUniqueName(),
                member.getUniqueName(),
                operator)
        {
        };
    }
}

// End SelectionFactory.java
