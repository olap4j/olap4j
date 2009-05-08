/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;

/**
 * Abstract implementation of {@link Selection}.
 *
 * @author jhyde
 * @version $Id$
 * @since May 30, 2007
 */
abstract class SelectionImpl implements Selection {

    protected Member member;
    protected String dimensionName;
    protected String hierarchyName;
    protected String levelName;
    protected String memberName;
    protected Dimension dimension;
    protected Operator operator = Operator.MEMBER;

    /**
     * Creates a SelectionImpl.
     *
     * @pre operator != null
     */
    public SelectionImpl(
        Member member,
        Dimension dimension,
        String hierarchyName,
        String levelName,
        String memberName,
        Operator operator)
    {
        super();
        this.member = member;
        this.dimension = dimension;
        this.hierarchyName = hierarchyName;
        this.levelName = levelName;
        this.memberName = memberName;
        this.operator = operator;
    }

    public String getName() {
        return memberName;
    }

    public void setName(String name) {
        memberName = name;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public Member getMember() {
        return member;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }

    public String getHierarchyName() {
        return hierarchyName;
    }

    public void setHierarchyName(String hierarchyName) {
        this.hierarchyName = hierarchyName;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        assert operator != null;
        this.operator = operator;
    }
}

// End SelectionImpl.java
