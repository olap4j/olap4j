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
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.metadata.*;

import java.util.*;
import java.lang.ref.SoftReference;

/**
 * Implementation of {@link Cube}
 * for XML/A providers.
 *
 * @author jhyde
 * @version $Id$
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
    private final MetadataReader metadataReader;

    /**
     * Creates an XmlaOlap4jCube.
     *
     * @param olap4jSchema Schema
     * @param name Name
     * @param description Description
     * @param connection
     */
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
        this.metadataReader =
            new CachingMetadataReader(
                new RawMetadataReader());
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
        // replace temporary member versions of measures in cache with final
        // measures
        for (XmlaOlap4jMeasure measure : measures) {
            ((CachingMetadataReader) metadataReader).memberMap.put(
                measure.getUniqueName(),
                new SoftReference<XmlaOlap4jMember>(measure));
        }
        if (!measures.isEmpty()) {
            final XmlaOlap4jHierarchy measuresHierarchy =
                measures.get(0).getHierarchy();
            for (XmlaOlap4jLevel level : measuresHierarchy.levels) {
                final List<Member> memberList = level.getMembers();
                final List<Measure> measureList =
                    new ArrayList<Measure>(memberList.size());
                for (Member member : memberList) {
                    final SoftReference<XmlaOlap4jMember> measureRef =
                        ((CachingMetadataReader) metadataReader).memberMap.get(
                            member.getUniqueName());
                    // gc not possible - we hold all members in 'measures' field.
                    assert measureRef.get() != null;
                    measureList.add((Measure) measureRef.get());
                }
                ((CachingMetadataReader) metadataReader).levelMemberListMap.put(
                    level,
                    new SoftReference<List<XmlaOlap4jMember>>(
                        Olap4jUtil.<XmlaOlap4jMember>cast(measureList)));
            }
        }
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

    /**
     * Finds a member, given its fully qualfieid name.
     *
     * @param segmentList List of the segments of the name
     * @return Member, or null if not found
     * @throws OlapException on error
     */
    private Member lookupMember(
        List<IdentifierNode.Segment> segmentList) throws OlapException
    {
        StringBuilder buf = new StringBuilder();
        for (IdentifierNode.Segment segment : segmentList) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(segment.toString());
        }
        final String uniqueName = buf.toString();
        return getMetadataReader().lookupMemberByUniqueName(uniqueName);
    }

    /**
     * Returns this cube's metadata reader.
     *
     * <p>Not part of public olap4j API.
     *
     * @return metadata reader
     */
    MetadataReader getMetadataReader() {
        return metadataReader;
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
        getMetadataReader().lookupMemberRelatives(
            treeOps, uniqueName, list);
        return Olap4jUtil.cast(list);
    }

    /**
     * Abstract implementation of MemberReader that delegates all operations
     * to an underlying MemberReader.
     */
    private static abstract class DelegatingMetadataReader
        implements MetadataReader
    {
        private final MetadataReader metadataReader;

        /**
         * Creates a DelegatingMetadataReader.
         *
         * @param metadataReader Underlying metadata reader
         */
        DelegatingMetadataReader(MetadataReader metadataReader) {
            this.metadataReader = metadataReader;
        }

        public XmlaOlap4jMember lookupMemberByUniqueName(
            String memberUniqueName) throws OlapException
        {
            return metadataReader.lookupMemberByUniqueName(memberUniqueName);
        }

        public void lookupMembersByUniqueName(
            List<String> memberUniqueNames,
            Map<String, XmlaOlap4jMember> memberMap) throws OlapException
        {
            metadataReader.lookupMembersByUniqueName(
                memberUniqueNames, memberMap);
        }

        public void lookupMemberRelatives(
            Set<Member.TreeOp> treeOps,
            String memberUniqueName,
            List<XmlaOlap4jMember> list) throws OlapException
        {
            metadataReader.lookupMemberRelatives(
                treeOps, memberUniqueName, list);
        }

        public List<XmlaOlap4jMember> getLevelMembers(
            XmlaOlap4jLevel level)
            throws OlapException
        {
            return metadataReader.getLevelMembers(level);
        }
    }

    /**
     * Implementation of MemberReader that reads from an underlying member
     * reader and caches the results.
     *
     * <p>Caches are {@link Map}s containing
     * {@link java.lang.ref.SoftReference}s to cached objects, so can be
     * cleared when memory is in short supply.
     */
    private static class CachingMetadataReader
        extends DelegatingMetadataReader
    {
        private final Map<String, SoftReference<XmlaOlap4jMember>> memberMap =
            new HashMap<String, SoftReference<XmlaOlap4jMember>>();

        private final Map<XmlaOlap4jLevel, SoftReference<List<XmlaOlap4jMember>>>
            levelMemberListMap =
            new HashMap<XmlaOlap4jLevel, SoftReference<List<XmlaOlap4jMember>>>();

        /**
         * Creates a CachingMetadataReader.
         *
         * @param metadataReader Underlying metadata reader
         */
        CachingMetadataReader(MetadataReader metadataReader) {
            super(metadataReader);
        }

        public XmlaOlap4jMember lookupMemberByUniqueName(
            String memberUniqueName) throws OlapException
        {
            final SoftReference<XmlaOlap4jMember> memberRef =
                memberMap.get(memberUniqueName);
            if (memberRef != null) {
                final XmlaOlap4jMember member = memberRef.get();
                if (member != null) {
                    return member;
                }
            }
            final XmlaOlap4jMember member =
                super.lookupMemberByUniqueName(memberUniqueName);
            memberMap.put(
                memberUniqueName,
                new SoftReference<XmlaOlap4jMember>(member));
            return member;
        }

        public void lookupMembersByUniqueName(
            List<String> memberUniqueNames,
            Map<String, XmlaOlap4jMember> memberMap) throws OlapException
        {
            final ArrayList<String> remainingMemberUniqueNames =
                new ArrayList<String>();
            for (String memberUniqueName : memberUniqueNames) {
                final SoftReference<XmlaOlap4jMember> memberRef =
                    this.memberMap.get(memberUniqueName);
                final XmlaOlap4jMember member;
                if (memberRef != null &&
                    (member = memberRef.get()) != null) {
                    memberMap.put(memberUniqueName, member);
                } else {
                    remainingMemberUniqueNames.add(memberUniqueName);
                }
            }
            // If any of the member names were not in the cache, look them up
            // by delegating.
            if (!remainingMemberUniqueNames.isEmpty()) {
                super.lookupMembersByUniqueName(
                    remainingMemberUniqueNames,
                    memberMap);
                // Add the previously missing members into the cache.
                for (String memberName : remainingMemberUniqueNames) {
                    XmlaOlap4jMember member = memberMap.get(memberName);
                    if (member != null) {
                        this.memberMap.put(
                            memberName,
                            new SoftReference<XmlaOlap4jMember>(member));
                    }
                }
            }
        }

        public List<XmlaOlap4jMember> getLevelMembers(
            XmlaOlap4jLevel level)
            throws OlapException
        {
            final SoftReference<List<XmlaOlap4jMember>> memberListRef =
                levelMemberListMap.get(level);
            if (memberListRef != null) {
                final List<XmlaOlap4jMember> memberList = memberListRef.get();
                if (memberList != null) {
                    return memberList;
                }
            }
            final List<XmlaOlap4jMember> memberList =
                super.getLevelMembers(level);
            levelMemberListMap.put(
                level,
                new SoftReference<List<XmlaOlap4jMember>>(memberList));
            return memberList;
        }
    }

    /**
     * Implementation of MetadataReader that reads from the XMLA provider,
     * without caching.
     */
    private class RawMetadataReader implements MetadataReader {
        public XmlaOlap4jMember lookupMemberByUniqueName(
            String memberUniqueName)
            throws OlapException
        {
            NamedList<XmlaOlap4jMember> list =
                new NamedListImpl<XmlaOlap4jMember>();
            lookupMemberRelatives(
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

        /* (non-Javadoc)
         * @see org.olap4j.driver.xmla.MetadataReader
         *     #lookupMembersByUniqueName(java.util.List, java.util.Map)
         */
        public void lookupMembersByUniqueName(
            List<String> memberUniqueNames,
            Map<String, XmlaOlap4jMember> memberMap) throws OlapException
        {
            if (olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData
                .olap4jConnection.getDataSourceInfo()
                    .indexOf("Provider=Mondrian") != -1) //$NON-NLS-1$
            {
                memberMap.putAll(this.mondrianMembersLookup(memberUniqueNames));
            } else {
                memberMap.putAll(this.genericMembersLookup(memberUniqueNames));
            }
        }

        /**
         * This is an optimized method for Mondrian servers members lookup.
         * @param memberUniqueNames A list of the members to lookup
         * @return A map of members with their unique name as a key
         * @throws OlapException Gets thrown for communication errors
         */
        private Map<String,XmlaOlap4jMember> mondrianMembersLookup(
            List<String> memberUniqueNames) throws OlapException
        {
            final XmlaOlap4jConnection.Context context =
                new XmlaOlap4jConnection.Context(
                    XmlaOlap4jCube.this, null, null, null);
            final List<XmlaOlap4jMember> memberList =
                new ArrayList<XmlaOlap4jMember>();
            olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection
                .populateList(
                    memberList,
                    context,
                    XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEMBERS,
                    new XmlaOlap4jConnection.MemberHandler(),
                    new Object[] {
                        "CATALOG_NAME", olap4jSchema.olap4jCatalog.getName(),
                        "SCHEMA_NAME", olap4jSchema.getName(),
                        "CUBE_NAME", getName(),
                        "MEMBER_UNIQUE_NAME", memberUniqueNames
                    });
            final Map<String,XmlaOlap4jMember> memberMap =
                new HashMap<String,XmlaOlap4jMember>(memberUniqueNames.size());
            for (XmlaOlap4jMember member : memberList) {
                if (member != null) {
                    memberMap.put(member.getUniqueName(), member);
                }
            }
            return memberMap;
        }

        /**
         * This is an generic method for members lookup.
         * @param memberUniqueNames A list of the members to lookup
         * @return A map of members with their unique name as a key
         * @throws OlapException Gets thrown for communication errors
         */
        private Map<String,XmlaOlap4jMember> genericMembersLookup(
                List<String> memberUniqueNames) throws OlapException
        {
            final Map<String,XmlaOlap4jMember> memberMap =
                new HashMap<String,XmlaOlap4jMember>(memberUniqueNames.size());
            // Iterates through member names
            for (String currentMemberName : memberUniqueNames) {
                // Only lookup if it is not in the map yet
                if (!memberMap.containsKey(currentMemberName)) {
                    XmlaOlap4jMember member =
                        this.lookupMemberByUniqueName(currentMemberName);
                    // Null members might mean calculated members
                    if (member != null) {
                        memberMap.put(member.getUniqueName(), member);
                    }
                }
            }
            return memberMap;
        }

        public void lookupMemberRelatives(
            Set<Member.TreeOp> treeOps,
            String memberUniqueName,
            List<XmlaOlap4jMember> list) throws OlapException
        {
            final XmlaOlap4jConnection.Context context =
                new XmlaOlap4jConnection.Context(
                    XmlaOlap4jCube.this, null, null, null);
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
                    new Object[] {
                        "CATALOG_NAME", olap4jSchema.olap4jCatalog.getName(),
                        "SCHEMA_NAME", olap4jSchema.getName(),
                        "CUBE_NAME", getName(),
                        "MEMBER_UNIQUE_NAME", memberUniqueName,
                        "TREE_OP", String.valueOf(treeOpMask)
                    });
        }

        public List<XmlaOlap4jMember> getLevelMembers(
            XmlaOlap4jLevel level)
            throws OlapException
        {
            assert level.olap4jHierarchy.olap4jDimension.olap4jCube
                == XmlaOlap4jCube.this;
            final XmlaOlap4jConnection.Context context =
                new XmlaOlap4jConnection.Context(level);
            List<XmlaOlap4jMember> list = new ArrayList<XmlaOlap4jMember>();
            // If this is a level in the [Measures] dimension, we want to
            // return objects that implement the Measure interface. During
            // bootstrap, the list will be empty, and we need to return the
            // regular Member objects which have the extra properties that are
            // returned by MSCHEMA_MEMBERS but not MDSCHEMA_MEASURES.
            switch (level.getDimension().getDimensionType()) {
            case MEASURE:
                if (!level.olap4jHierarchy.olap4jDimension.olap4jCube.measures
                    .isEmpty()) {
                    return Olap4jUtil.cast(
                        level.olap4jHierarchy.olap4jDimension.olap4jCube
                            .measures);
                }
                break;
            }
            olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection
                .populateList(
                    list,
                    context,
                    XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEMBERS,
                    new XmlaOlap4jConnection.MemberHandler(),
                    new Object[] {
                        "CATALOG_NAME", olap4jSchema.olap4jCatalog.getName(),
                        "SCHEMA_NAME", olap4jSchema.getName(),
                        "CUBE_NAME", getName(),
                        "DIMENSION_UNIQUE_NAME",
                        level.olap4jHierarchy.olap4jDimension.getUniqueName(),
                        "HIERARCHY_UNIQUE_NAME",
                        level.olap4jHierarchy.getUniqueName(),
                        "LEVEL_UNIQUE_NAME", level.getUniqueName()
                    });
            return list;
        }
    }
}

// End XmlaOlap4jCube.java
