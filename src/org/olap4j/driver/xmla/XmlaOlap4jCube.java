/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.mdx.IdentifierSegment;
import org.olap4j.metadata.*;

import java.lang.ref.SoftReference;
import java.util.*;

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
    private final String caption;
    private final String description;

    final NamedList<XmlaOlap4jDimension> dimensions;
    final Map<String, XmlaOlap4jDimension> dimensionsByUname =
        new HashMap<String, XmlaOlap4jDimension>();
    private NamedList<XmlaOlap4jHierarchy> hierarchies = null;
    final Map<String, XmlaOlap4jHierarchy> hierarchiesByUname =
        new HashMap<String, XmlaOlap4jHierarchy>();
    final Map<String, XmlaOlap4jLevel> levelsByUname =
        new HashMap<String, XmlaOlap4jLevel>();
    final List<XmlaOlap4jMeasure> measures =
        new ArrayList<XmlaOlap4jMeasure>();
    private final NamedList<XmlaOlap4jNamedSet> namedSets;
    private final MetadataReader metadataReader;

    /**
     * Creates an XmlaOlap4jCube.
     *
     * @param olap4jSchema Schema
     * @param name Name
     * @param caption Caption
     * @param description Description
     * @throws org.olap4j.OlapException on error
     */
    XmlaOlap4jCube(
        XmlaOlap4jSchema olap4jSchema,
        String name,
        String caption,
        String description) throws OlapException
    {
        assert olap4jSchema != null;
        assert description != null;
        assert name != null;
        this.olap4jSchema = olap4jSchema;
        this.name = name;
        this.caption = caption;
        this.description = description;
        final Map<String, XmlaOlap4jMeasure> measuresMap =
            new HashMap<String, XmlaOlap4jMeasure>();
        this.metadataReader =
            new CachingMetadataReader(
                new RawMetadataReader(),
                measuresMap);
        final XmlaOlap4jConnection olap4jConnection =
            olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;

        final XmlaOlap4jConnection.Context context =
            new XmlaOlap4jConnection.Context(this, null, null, null);

        String[] restrictions = {
            "CATALOG_NAME", olap4jSchema.olap4jCatalog.getName(),
            "SCHEMA_NAME", olap4jSchema.getName(),
            "CUBE_NAME", getName()
        };

        this.dimensions = new DeferredNamedListImpl<XmlaOlap4jDimension>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_DIMENSIONS,
            context,
            new XmlaOlap4jConnection.DimensionHandler(this),
            restrictions);

        // populate measures up front; a measure is needed in every query
        olap4jConnection.populateList(
            measures,
            context,
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_MEASURES,
            new XmlaOlap4jConnection.MeasureHandler(),
            restrictions);
        for (XmlaOlap4jMeasure measure : measures) {
            measuresMap.put(measure.getUniqueName(), measure);
        }

        // populate named sets
        namedSets = new DeferredNamedListImpl<XmlaOlap4jNamedSet>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_SETS,
            context,
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

    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVisible() {
        return true;
    }

    public NamedList<Dimension> getDimensions() {
        return Olap4jUtil.cast(dimensions);
    }

    public NamedList<Hierarchy> getHierarchies() {
        // This is a costly operation. It forces the init
        // of all dimensions and all hierarchies.
        // We defer it to this point.
        if (this.hierarchies == null) {
            this.hierarchies = new NamedListImpl<XmlaOlap4jHierarchy>();
            for (XmlaOlap4jDimension dim : this.dimensions) {
                this.hierarchies.addAll(dim.hierarchies);
            }
        }
        return Olap4jUtil.cast(hierarchies);
    }

    public boolean isDrillThroughEnabled() {
        // XMLA does not implement drillthrough yet.
        return false;
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

    public Member lookupMember(
        List<IdentifierSegment> segmentList)
        throws OlapException
    {
        StringBuilder buf = new StringBuilder();
        for (IdentifierSegment segment : segmentList) {
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
        List<IdentifierSegment> nameParts) throws OlapException
    {
        StringBuilder buf = new StringBuilder();
        for (IdentifierSegment namePart : nameParts) {
            if (buf.length() > 0) {
                buf.append('.');
            }
            buf.append(namePart);
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
        private final Map<String, XmlaOlap4jMeasure> measuresMap;

        private final Map<String, SoftReference<XmlaOlap4jMember>> memberMap =
            new HashMap<String, SoftReference<XmlaOlap4jMember>>();

        private final Map<
            XmlaOlap4jLevel,
            SoftReference<List<XmlaOlap4jMember>>> levelMemberListMap =
            new HashMap<
                XmlaOlap4jLevel,
                SoftReference<List<XmlaOlap4jMember>>>();

        /**
         * Creates a CachingMetadataReader.
         *
         * @param metadataReader Underlying metadata reader
         * @param measuresMap Map of measures by unique name, inherited from the
         *     cube and used read-only by this reader
         */
        CachingMetadataReader(
            MetadataReader metadataReader,
            Map<String, XmlaOlap4jMeasure> measuresMap)
        {
            super(metadataReader);
            this.measuresMap = measuresMap;
        }

        public XmlaOlap4jMember lookupMemberByUniqueName(
            String memberUniqueName) throws OlapException
        {
            // First, look in measures map.
            XmlaOlap4jMeasure measure =
                measuresMap.get(memberUniqueName);
            if (measure != null) {
                return measure;
            }

            // Next, look in cache.
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
            if (member != null
                && member.getDimension().type != Dimension.Type.MEASURE)
            {
                memberMap.put(
                    memberUniqueName,
                    new SoftReference<XmlaOlap4jMember>(member));
            }
            return member;
        }

        public void lookupMembersByUniqueName(
            List<String> memberUniqueNames,
            Map<String, XmlaOlap4jMember> memberMap) throws OlapException
        {
            final ArrayList<String> remainingMemberUniqueNames =
                new ArrayList<String>();
            for (String memberUniqueName : memberUniqueNames) {
                // First, look in measures map.
                XmlaOlap4jMeasure measure =
                    measuresMap.get(memberUniqueName);
                if (measure != null) {
                    memberMap.put(memberUniqueName, measure);
                    continue;
                }

                // Next, look in cache.
                final SoftReference<XmlaOlap4jMember> memberRef =
                    this.memberMap.get(memberUniqueName);
                final XmlaOlap4jMember member;
                if (memberRef != null
                    && (member = memberRef.get()) != null)
                {
                    memberMap.put(memberUniqueName, member);
                    continue;
                }

                remainingMemberUniqueNames.add(memberUniqueName);
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
                        if (!(member instanceof Measure)
                            && member.getDimension().type
                               != Dimension.Type.MEASURE)
                        {
                            this.memberMap.put(
                                memberName,
                                new SoftReference<XmlaOlap4jMember>(member));
                        }
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
            if (level.olap4jHierarchy.olap4jDimension.type
                != Dimension.Type.MEASURE)
            {
                levelMemberListMap.put(
                    level,
                    new SoftReference<List<XmlaOlap4jMember>>(memberList));
            }
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
                Olap4jUtil.enumSetOf(Member.TreeOp.SELF),
                memberUniqueName,
                list);
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

        public void lookupMembersByUniqueName(
            List<String> memberUniqueNames,
            Map<String, XmlaOlap4jMember> memberMap) throws OlapException
        {
            if (olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData
                    .olap4jConnection.getDatabase()
                    .indexOf("Provider=Mondrian") != -1)
            {
                mondrianMembersLookup(memberUniqueNames, memberMap);
            } else {
                genericMembersLookup(memberUniqueNames, memberMap);
            }
        }

        /**
         * Looks up members; optimized for Mondrian servers.
         *
         * @param memberUniqueNames A list of the members to lookup
         * @param memberMap Output map of members keyed by unique name
         * @throws OlapException Gets thrown for communication errors
         */
        private void mondrianMembersLookup(
            List<String> memberUniqueNames,
            Map<String, XmlaOlap4jMember> memberMap) throws OlapException
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
            for (XmlaOlap4jMember member : memberList) {
                if (member != null) {
                    memberMap.put(member.getUniqueName(), member);
                }
            }
        }

        /**
         * Looks up members.
         *
         * @param memberUniqueNames A list of the members to lookup
         * @param memberMap Output map of members keyed by unique name
         * @throws OlapException Gets thrown for communication errors
         */
        private void genericMembersLookup(
            List<String> memberUniqueNames,
            Map<String, XmlaOlap4jMember> memberMap) throws OlapException
        {
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
                        "CATALOG_NAME",
                        olap4jSchema.olap4jCatalog.getName(),
                        "SCHEMA_NAME",
                        olap4jSchema.getName(),
                        "CUBE_NAME", getName(),
                        "MEMBER_UNIQUE_NAME",
                        memberUniqueName,
                        "TREE_OP",
                        String.valueOf(treeOpMask)
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
                    .isEmpty())
                {
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
