/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.metadata.*;

import java.util.List;

/**
 * Implementation of {@link org.olap4j.metadata.Hierarchy}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 4, 2007
 */
class XmlaOlap4jHierarchy
    extends XmlaOlap4jElement
    implements Hierarchy, Named
{
    final XmlaOlap4jDimension olap4jDimension;
    final NamedList<XmlaOlap4jLevel> levels =
        new NamedListImpl<XmlaOlap4jLevel>();
    private final boolean all;
    private final String defaultMemberUniqueName;

    XmlaOlap4jHierarchy(
        XmlaOlap4jDimension olap4jDimension,
        String uniqueName,
        String name,
        String caption,
        String description,
        boolean all,
        String defaultMemberUniqueName) throws OlapException
    {
        super(uniqueName, name, caption, description);
        assert olap4jDimension != null;
        this.olap4jDimension = olap4jDimension;
        this.all = all;
        this.defaultMemberUniqueName = defaultMemberUniqueName;
    }

    public Dimension getDimension() {
        return olap4jDimension;
    }

    public NamedList<Level> getLevels() {
        return Olap4jUtil.cast(levels);
    }

    public boolean hasAll() {
        return all;
    }

    public Member getDefaultMember() throws OlapException {
        if (defaultMemberUniqueName == null) {
            return null;
        }
        return olap4jDimension.olap4jCube.getMetadataReader()
            .lookupMemberByUniqueName(
                defaultMemberUniqueName);
    }

    public NamedList<Member> getRootMembers() throws OlapException {
        final List<XmlaOlap4jMember> memberList =
            olap4jDimension.olap4jCube.getMetadataReader().getLevelMembers(
                levels.get(0));
        final NamedList<XmlaOlap4jMember> list =
            new NamedListImpl<XmlaOlap4jMember>();
        list.addAll(memberList);
        return Olap4jUtil.cast(list);
    }
}

// End XmlaOlap4jHierarchy.java
