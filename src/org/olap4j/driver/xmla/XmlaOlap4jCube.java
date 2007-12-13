/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.metadata.*;

import java.util.*;

/**
 * Implementation of {@link Cube}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id: $
 * @since Dec 4, 2007
 */
class XmlaOlap4jCube implements Cube, Named
{
    final XmlaOlap4jSchema olap4jSchema;
    private final String name;
    private final String description;

    final NamedList<XmlaOlap4jDimension> dimensions =
        new NamedListImpl<XmlaOlap4jDimension>();
    final Map<String, XmlaOlap4jDimension> dimensionsByUname =
        new HashMap<String, XmlaOlap4jDimension>();
    private final NamedList<XmlaOlap4jHierarchy> hierarchies =
        new NamedListImpl<XmlaOlap4jHierarchy>();
    final Map<String, XmlaOlap4jHierarchy> hierarchiesByUname =
        new HashMap<String, XmlaOlap4jHierarchy>();
    final Map<String, XmlaOlap4jLevel> levelsByUname =
        new HashMap<String, XmlaOlap4jLevel>();
    private final NamedList<XmlaOlap4jMeasure> measures =
        new NamedListImpl<XmlaOlap4jMeasure>();
    private final NamedList<XmlaOlap4jNamedSet> namedSets =
        new NamedListImpl<XmlaOlap4jNamedSet>();

    XmlaOlap4jCube(
        XmlaOlap4jSchema olap4jSchema,
        String name,
        String description) throws OlapException
    {
        assert olap4jSchema != null;
        assert description != null;
        assert name != null;
        this.olap4jSchema = olap4jSchema;
        this.name = name;
        this.description = description;
        final XmlaOlap4jConnection olap4jConnection =
            olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;

        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(this, null, null, null);

        String[] restrictions = {
            "CATALOG_NAME", olap4jSchema.olap4jCatalog.getName(),
            "SCHEMA_NAME", olap4jSchema.getName(),
            "CUBE_NAME", getName()
        };
        // populate dimensions (without their hierarchies at first)
        olap4jConnection.populateList(
            dimensions, context,
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_DIMENSIONS,
            new XmlaOlap4jConnection.DimensionHandler(),
            restrictions);
        for (XmlaOlap4jDimension dimension : dimensions) {
            dimensionsByUname.put(dimension.getUniqueName(), dimension);
        }
        // populate hierarchies (referencing dimensions)
        olap4jConnection.populateList(
            hierarchies, context,
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_HIERARCHIES,
            new XmlaOlap4jConnection.HierarchyHandler(),
            restrictions);
        // now we have hierarchies, populate dimension->hierarchy and
        // cube->hierarchy mappings
        for (XmlaOlap4jHierarchy hierarchy : hierarchies) {
            hierarchy.olap4jDimension.hierarchies.add(hierarchy);
            hierarchiesByUname.put(hierarchy.getUniqueName(), hierarchy);
        }
        // populate levels (referencing hierarchies); use a temp list because
        // we don't need a mapping from cube->level
        NamedList<XmlaOlap4jLevel> levels =
            new NamedListImpl<XmlaOlap4jLevel>();
        olap4jConnection.populateList(
            levels, context,
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_LEVELS,
            new XmlaOlap4jConnection.LevelHandler(),
            restrictions);
        // now we have levels, populate hierarchy->level and cube->level
        // mappings
        for (XmlaOlap4jLevel level : levels) {
            level.olap4jHierarchy.levels.add(level);
            levelsByUname.put(level.getUniqueName(), level);
        }
        // populate measures
        olap4jConnection.populateList(
            measures, context,
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEASURES,
            new XmlaOlap4jConnection.MeasureHandler(),
            restrictions);
        // populate named sets
        olap4jConnection.populateList(
            namedSets, context,
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_SETS,
            new XmlaOlap4jConnection.NamedSetHandler(),
            restrictions);
    }

    public Schema getSchema() {
        return olap4jSchema;
    }

    public String getName() {
        return name;
    }

    public String getUniqueName() {
        return "[" + name + "]";
    }

    public String getCaption(Locale locale) {
        return name;
    }

    public String getDescription(Locale locale) {
        return description;
    }

    public NamedList<Dimension> getDimensions() {
        return Olap4jUtil.cast(dimensions);
    }

    public NamedList<Hierarchy> getHierarchies() {
        return Olap4jUtil.cast(hierarchies);
    }

    public List<Measure> getMeasures() {
        return Olap4jUtil.cast(measures);
    }

    public NamedList<NamedSet> getSets() {
        return Olap4jUtil.cast(namedSets);
    }

    public Collection<Locale> getSupportedLocales() {
        return Collections.singletonList(Locale.getDefault());
    }

    public Member lookupMember(String... nameParts) throws OlapException {
        List<IdentifierNode.Segment> segmentList =
            new ArrayList<IdentifierNode.Segment>();
        for (String namePart : nameParts) {
            segmentList.add(new IdentifierNode.Segment(namePart));
        }
        return lookupMember(segmentList);
    }

    private Member lookupMember(
        List<IdentifierNode.Segment> segmentList) throws OlapException
    {
        if (true) {
            StringBuilder buf = new StringBuilder();
            for (IdentifierNode.Segment segment : segmentList) {
                if (buf.length() > 0) {
                    buf.append('.');
                }
                buf.append(segment.toString());
            }
            final String uniqueName = buf.toString();
            return lookupMemberByUniqueName(uniqueName);
        } else {
            final Hierarchy hierarchy =
                getHierarchies().get(segmentList.get(0).getName());
            final NamedList<Member> rootMembers = hierarchy.getRootMembers();
            Member member = rootMembers.get(segmentList.get(1).getName());
            int k = 1;
            if (member == null) {
                if (rootMembers.size() == 1
                    && rootMembers.get(0).isAll()) {
                    member = rootMembers.get(0);
                    ++k;
                } else {
                    return null;
                }
            }
            while (k < segmentList.size()) {

            }
            return member;
        }
    }

    /**
     * Looks up a member by its unique name.
     *
     * <p>Not part of public olap4j API.
     *
     * @param memberUniqueName Unique name of member
     * @return Member, or null if not found
     * @throws OlapException if error occurs
     */
    XmlaOlap4jMember lookupMemberByUniqueName(
        String memberUniqueName)
        throws OlapException
    {
        NamedList<XmlaOlap4jMember> list =
            new NamedListImpl<XmlaOlap4jMember>();
        lookupMembersByUniqueName(
            EnumSet.of(Member.TreeOp.SELF), memberUniqueName, list);
        switch (list.size()) {
        case 0:
            return null;
        case 1:
            return list.get(0);
        default:
            throw new IllegalArgumentException(
                "more than one member with unique name '"
                    + memberUniqueName
                    + "'");
        }
    }

    /**
     * Looks a member by its unique name and returns members related by
     * the specified tree-operations.
     *
     * <p>Not part of public olap4j API.
     *
     * @param memberUniqueName Unique name of member
     *
     * @param treeOps Collection of tree operations to travel relative to
     * given member in order to create list of members
     *
     * @param list list to be populated with members related to the given
     * member, or empty set if the member is not found
     *
     * @throws OlapException if error occurs
     */
    void lookupMembersByUniqueName(
        Set<Member.TreeOp> treeOps,
        String memberUniqueName,
        List<XmlaOlap4jMember> list) throws OlapException
    {
        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(this, null, null, null);
        int treeOpMask = 0;
        for (Member.TreeOp treeOp : treeOps) {
            treeOpMask |= treeOp.xmlaOrdinal();
        }
        olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection
            .populateList(
                list,
                context,
                XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEMBERS,
                new XmlaOlap4jConnection.MemberHandler(),
                "CATALOG_NAME", olap4jSchema.olap4jCatalog.getName(),
                "SCHEMA_NAME", olap4jSchema.getName(),
                "CUBE_NAME", getName(),
                "MEMBER_UNIQUE_NAME", memberUniqueName,
                "TREE_OP", String.valueOf(treeOpMask));
    }

    /**
     * Returns true if two objects are equal, or are both null.
     *
     * @param s First object
     * @param t Second object
     * @return whether objects are equal
     */
    static <T> boolean equal(T s, T t) {
        return (s == null) ? (t == null) : s.equals(t);
    }

    public List<Member> lookupMembers(
        Set<Member.TreeOp> treeOps,
        String... nameParts) throws OlapException
    {
        StringBuilder buf = new StringBuilder();
        for (String namePart : nameParts) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(new IdentifierNode.Segment(namePart));
        }
        final String uniqueName = buf.toString();
        final List<XmlaOlap4jMember> list =
            new ArrayList<XmlaOlap4jMember>();
        lookupMembersByUniqueName(treeOps, uniqueName, list);
//        Collections.sort(list, new MemberComparator());
        return Olap4jUtil.cast(list);
    }

    /**
     * Looks a member by its unique name and returns members related by
     * the specified tree-operations.
     *
     * <p>Not part of public olap4j API.
     *
     * @param level Level
     *
     * @param list list to be populated with members related to the given level
     *
     * @throws OlapException if error occurs
     */
    void lookupLevelMembers(
        XmlaOlap4jLevel level,
        List<XmlaOlap4jMember> list) throws OlapException
    {
        assert level.olap4jHierarchy.olap4jDimension.olap4jCube == this;
        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(level);
        olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection
            .populateList(
                list,
                context,
                XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEMBERS,
                new XmlaOlap4jConnection.MemberHandler(),
                "CATALOG_NAME", olap4jSchema.olap4jCatalog.getName(),
                "SCHEMA_NAME", olap4jSchema.getName(),
                "CUBE_NAME", getName(),
                "DIMENSION_UNIQUE_NAME",
                level.olap4jHierarchy.olap4jDimension.getUniqueName(),
                "HIERARCHY_UNIQUE_NAME",
                level.olap4jHierarchy.getUniqueName(),
                "LEVEL_UNIQUE_NAME", level.getUniqueName());
    }

    // NOT USED
    private static class MemberComparator
        implements Comparator<XmlaOlap4jMember>
    {
        public int compare(XmlaOlap4jMember m1, XmlaOlap4jMember m2) {
            if (equal(m1, m2)) {
                return 0;
            }
            while (true) {
                int depth1 = m1.getDepth(),
                        depth2 = m2.getDepth();
                if (depth1 < depth2) {
                    m2 = m2.getParentMember();
                    if (Olap4jUtil.equal(m1, m2)) {
                        return -1;
                    }
                } else if (depth1 > depth2) {
                    m1 = m1.getParentMember();
                    if (equal(m1, m2)) {
                        return 1;
                    }
                } else {
                    m1 = m1.getParentMember();
                    m2 = m2.getParentMember();
                    if (equal(m1, m2)) {
                        // The previous values of m1 and m2 are siblings.
                        // We do not have access to the ordering key, if
                        // we assume that (a) the siblings were returned in
                        // the correct order, and (b) the sort is stable,
                        // then the first member is the earlier one.
                        return -1;
                    }
                }
            }
        }
    }
}

// End XmlaOlap4jCube.java
