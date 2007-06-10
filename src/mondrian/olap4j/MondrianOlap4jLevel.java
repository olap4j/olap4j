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

import java.util.Locale;
import java.util.List;

/**
 * <code>MondrianOlap4jLevel</code> ...
 *
 * @author jhyde
 * @version $Id$
 * @since May 25, 2007
 */
class MondrianOlap4jLevel implements Level {
    private final MondrianOlap4jSchema olap4jSchema;
    private final mondrian.olap.Level level;

    public MondrianOlap4jLevel(
        MondrianOlap4jSchema olap4jSchema,
        mondrian.olap.Level level)
    {
        this.olap4jSchema = olap4jSchema;
        this.level = level;
    }

    public int getDepth() {
        return level.getDepth();
    }

    public Hierarchy getHierarchy() {
        return new MondrianOlap4jHierarchy(olap4jSchema, level.getHierarchy());
    }

    public Dimension getDimension() {
        return new MondrianOlap4jDimension(olap4jSchema, level.getDimension());
    }

    public Type getLevelType() {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public NamedList<Property> getProperties() {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public Member findMember(String memberName) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public List<Member> getMembers() {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public String getName() {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public String getUniqueName() {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public String getCaption(Locale locale) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public String getDescription(Locale locale) {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }
}

// End MondrianOlap4jLevel.java
