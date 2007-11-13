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

import mondrian.olap.Id;
import mondrian.olap.SchemaReader;
import org.olap4j.metadata.*;

import java.util.*;

/**
 * Implementation of {@link Cube}
 * for the Mondrian OLAP engine.
 *
 * @author jhyde
 * @version $Id$
 * @since May 24, 2007
 */
class MondrianOlap4jCube implements Cube, Named {
    private final mondrian.olap.Cube cube;
    final MondrianOlap4jSchema olap4jSchema;

    MondrianOlap4jCube(
        mondrian.olap.Cube cube,
        MondrianOlap4jSchema olap4jSchema)
    {
        this.cube = cube;
        this.olap4jSchema = olap4jSchema;
    }

    public Schema getSchema() {
        return olap4jSchema;
    }

    public int hashCode() {
        return olap4jSchema.hashCode()
            ^ cube.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof MondrianOlap4jCube) {
            MondrianOlap4jCube that = (MondrianOlap4jCube) obj;
            return this.olap4jSchema == that.olap4jSchema
                && this.cube.equals(that.cube);
        }
        return false;
    }

    public NamedList<Dimension> getDimensions() {
        List<MondrianOlap4jDimension> list =
            new NamedListImpl<MondrianOlap4jDimension>();
        for (mondrian.olap.Dimension dimension : cube.getDimensions()) {
            list.add(
                new MondrianOlap4jDimension(
                    olap4jSchema, dimension));
        }
        return (NamedList) list;
    }

    public List<Measure> getMeasures() {
        final Level measuresLevel =
            getDimensions().get("Measures").getDefaultHierarchy()
                .getLevels().get(0);
        return (List) measuresLevel.getMembers();
    }

    public NamedList<NamedSet> getSets() {
        final NamedListImpl<MondrianOlap4jNamedSet> list =
            new NamedListImpl<MondrianOlap4jNamedSet>();
        final MondrianOlap4jConnection olap4jConnection =
            olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;
        for (mondrian.olap.NamedSet namedSet : cube.getNamedSets()) {
            list.add(olap4jConnection.toOlap4j(cube, namedSet));
        }
        return (NamedList) list;
    }

    public Collection<Locale> getSupportedLocales() {
        throw new UnsupportedOperationException();
    }

    public String getName() {
        return cube.getName();
    }

    public String getUniqueName() {
        return cube.getUniqueName();
    }

    public String getCaption(Locale locale) {
        // todo: i81n
        return cube.getCaption();
    }

    public String getDescription(Locale locale) {
        // todo: i81n
        return cube.getDescription();
    }

    public Member lookupMember(String... nameParts) {
        final MondrianOlap4jConnection olap4jConnection =
            olap4jSchema.olap4jCatalog.olap4jDatabaseMetaData.olap4jConnection;
        final SchemaReader schemaReader =
            cube.getSchemaReader(olap4jConnection.connection.getRole());

        final List<Id.Segment> segmentList = new ArrayList<Id.Segment>();
        for (String namePart : nameParts) {
            segmentList.add(new Id.Segment(namePart, Id.Quoting.QUOTED));
        }
        final mondrian.olap.Member member =
            schemaReader.getMemberByUniqueName(segmentList, false);
        if (member == null) {
            return null;
        }
        return olap4jConnection.toOlap4j(member);
    }

    public List<Member> lookupMembers(
        Set<Member.TreeOp> treeOps,
        String... nameParts)
    {
        final Member member = lookupMember(nameParts);
        if (member == null) {
            return Collections.emptyList();
        }

        // Add ancestors and/or the parent. Ancestors are prepended, to ensure
        // hierarchical order.
        final List<Member> list = new ArrayList<Member>();
        if (treeOps.contains(Member.TreeOp.ANCESTORS)) {
            for (Member m = member.getParentMember();
                m != null;
                m = m.getParentMember()) {
                list.add(0, m);
            }
        } else if (treeOps.contains(Member.TreeOp.PARENT)) {
            final Member parentMember = member.getParentMember();
            if (parentMember != null) {
                list.add(parentMember);
            }
        }

        // Add siblings. Siblings which occur after the member are deferred,
        // because they occur after children and descendants in the
        // hierarchical ordering.
        List<Member> remainingSiblingsList = null;
        if (treeOps.contains(Member.TreeOp.SIBLINGS)) {
            final Member parentMember = member.getParentMember();
            NamedList<? extends Member> siblingMembers;
            if (parentMember != null) {
                siblingMembers = parentMember.getChildMembers();
            } else {
                siblingMembers = member.getHierarchy().getRootMembers();
            }
            List<Member> targetList = list;
            for (Member siblingMember : siblingMembers) {
                if (siblingMember.equals(member)) {
                    targetList =
                        remainingSiblingsList =
                            new ArrayList<Member>();
                } else {
                    targetList.add(siblingMember);
                }
            }
        }

        // Add the member itself.
        if (treeOps.contains(Member.TreeOp.SELF)) {
            list.add(member);
        }

        // Add descendants and/or children.
        if (treeOps.contains(Member.TreeOp.DESCENDANTS)) {
            for (Member childMember : member.getChildMembers()) {
                list.add(childMember);
                addDescendants(list, childMember);
            }
        } else if (treeOps.contains(Member.TreeOp.CHILDREN)) {
            for (Member childMember : member.getChildMembers()) {
                list.add(childMember);
            }
        }
        // Lastly, add siblings which occur after the member itself. They
        // occur after all of the descendants in the hierarchical ordering.
        if (remainingSiblingsList != null) {
            list.addAll(remainingSiblingsList);
        }
        return list;
    }

    private static void addDescendants(List<Member> list, Member member) {
        for (Member childMember : member.getChildMembers()) {
            list.add(childMember);
            addDescendants(list, childMember);
        }
    }
}

// End MondrianOlap4jCube.java
