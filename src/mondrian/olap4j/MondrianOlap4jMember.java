/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import org.olap4j.metadata.*;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;
import org.olap4j.OlapException;
import org.olap4j.mdx.ParseTreeNode;

import java.util.List;
import java.util.Locale;

/**
 * Implementation of {@link Member} as a wrapper around a mondrian
 * {@link mondrian.olap.Member}.
 *
 * @author jhyde
 * @version $Id$
 * @since May 25, 2007
 */
class MondrianOlap4jMember implements Member, Named {
    private final mondrian.olap.Member mondrianMember;
    private final MondrianOlap4jSchema olap4jSchema;

    MondrianOlap4jMember(
        MondrianOlap4jSchema olap4jSchema,
        mondrian.olap.Member mondrianMember)
    {
        this.olap4jSchema = olap4jSchema;
        this.mondrianMember = mondrianMember;
    }

    public NamedList<MondrianOlap4jMember> getChildMembers() {
        final mondrian.olap.Member[] children =
            olap4jSchema.schemaReader.getMemberChildren(
                mondrianMember);
        return new AbstractNamedList<MondrianOlap4jMember>() {
            public MondrianOlap4jMember get(int index) {
                return new MondrianOlap4jMember(olap4jSchema, children[index]);
            }

            public int size() {
                return children.length;
            }
        };
    }

    public Member getParentMember() {
        return new MondrianOlap4jMember(olap4jSchema, mondrianMember);
    }

    public Level getLevel() {
        return new MondrianOlap4jLevel(olap4jSchema, mondrianMember.getLevel());
    }

    public Hierarchy getHierarchy() {
        return new MondrianOlap4jHierarchy(
            olap4jSchema, mondrianMember.getHierarchy());
    }

    public Dimension getDimension() {
        return new MondrianOlap4jDimension(
            olap4jSchema, mondrianMember.getDimension());
    }

    public Type getMemberType() {
        throw new UnsupportedOperationException();
    }

    public boolean isChildOrEqualTo(Member member) {
        throw new UnsupportedOperationException();
    }

    public boolean isCalculated() {
        throw new UnsupportedOperationException();
    }

    public int getSolveOrder() {
        throw new UnsupportedOperationException();
    }

    public ParseTreeNode getExpression() {
        throw new UnsupportedOperationException();
    }

    public List<Member> getAncestorMembers() {
        throw new UnsupportedOperationException();
    }

    public boolean isCalculatedInQuery() {
        throw new UnsupportedOperationException();
    }

    public Object getPropertyValue(String propertyName) {
        throw new UnsupportedOperationException();
    }

    public String getPropertyFormattedValue(String propertyName) {
        throw new UnsupportedOperationException();
    }

    public void setProperty(String name, Object value) throws OlapException {
        throw new UnsupportedOperationException();
    }

    public List<Property> getProperties() {
        throw new UnsupportedOperationException();
    }

    public int getOrdinal() {
        throw new UnsupportedOperationException();
    }

    public boolean isHidden() {
        throw new UnsupportedOperationException();
    }

    public int getDepth() {
        throw new UnsupportedOperationException();
    }

    public Member getDataMember() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public String getUniqueName() {
        return mondrianMember.getUniqueName();
    }

    public String getCaption(Locale locale) {
        throw new UnsupportedOperationException();
    }

    public String getDescription(Locale locale) {
        throw new UnsupportedOperationException();
    }

}

// End MondrianOlap4jMember.java
