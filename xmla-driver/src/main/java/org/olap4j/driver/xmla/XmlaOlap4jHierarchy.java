/*
// Licensed to Julian Hyde under one or more contributor license
// agreements. See the NOTICE file distributed with this work for
// additional information regarding copyright ownership.
//
// Julian Hyde licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except in
// compliance with the License. You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
*/
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.*;
import org.olap4j.metadata.*;

import java.util.List;
import java.util.Set;

/**
 * Implementation of {@link org.olap4j.metadata.Hierarchy}
 * for XML/A providers.
 *
 * @author jhyde
 * @since Dec 4, 2007
 */
class XmlaOlap4jHierarchy
    extends XmlaOlap4jElement
    implements Hierarchy, Named
{
    final XmlaOlap4jDimension olap4jDimension;
    final NamedList<XmlaOlap4jLevel> levels;
    private final boolean all;
    private final String defaultMemberUniqueName;
    private final String hierarchyDisplayFolder;
    private final Structure structure;
    private final int ordinal;
    private final Set<Origin> originSet;

    XmlaOlap4jHierarchy(
        XmlaOlap4jDimension olap4jDimension,
        int ordinal,
        String uniqueName,
        String name,
        String caption,
        String description,
        String hierarchyDisplayFolder,
        boolean all,
        String defaultMemberUniqueName,
        Set<Origin> originSet,
        Structure structure) throws OlapException
    {
        super(uniqueName, name, caption, description);
        assert olap4jDimension != null;
        assert structure != null;
        assert originSet != null;
        this.olap4jDimension = olap4jDimension;
        this.ordinal = ordinal;
        this.all = all;
        this.defaultMemberUniqueName = defaultMemberUniqueName;
        this.hierarchyDisplayFolder = hierarchyDisplayFolder;
        this.structure = structure;
        this.originSet = originSet;

        String[] hierarchyRestrictions = {
            "CATALOG_NAME",
            olap4jDimension.olap4jCube.olap4jSchema.olap4jCatalog.getName(),
            "SCHEMA_NAME",
            olap4jDimension.olap4jCube.olap4jSchema.getName(),
            "CUBE_NAME",
            olap4jDimension.olap4jCube.getName(),
            "DIMENSION_UNIQUE_NAME",
            olap4jDimension.getUniqueName(),
            "HIERARCHY_UNIQUE_NAME",
            getUniqueName()
        };

        this.levels = new DeferredNamedListImpl<XmlaOlap4jLevel>(
            XmlaOlap4jConnection.MetadataRequest.MDSCHEMA_LEVELS,
            new XmlaOlap4jConnection.Context(
                olap4jDimension.olap4jCube.olap4jSchema.olap4jCatalog
                    .olap4jDatabaseMetaData.olap4jConnection,
                olap4jDimension.olap4jCube.olap4jSchema.olap4jCatalog
                    .olap4jDatabaseMetaData,
                olap4jDimension.olap4jCube.olap4jSchema.olap4jCatalog,
                olap4jDimension.olap4jCube.olap4jSchema,
                olap4jDimension.olap4jCube,
                olap4jDimension,
                this, null),
            new XmlaOlap4jConnection.LevelHandler(olap4jDimension.olap4jCube),
            hierarchyRestrictions);
    }

    public Dimension getDimension() {
        return olap4jDimension;
    }

    public Member getAllMember() throws OlapException {
        return all ? getRootMembers().get(0) : null;
    }

    public boolean isReadWrite() {
        return false; // FIXME
    }

    public int getOrdinal() {
        return ordinal;
    }

    public StructureType getStructureType() {
        return StructureType.Natural; // FIXME
    }

    public int getCardinality() {
        int n = 0;
        for (XmlaOlap4jLevel level : levels) {
            n += level.getCardinality();
        }
        return n;
    }

    public String getDisplayFolder() {
        return hierarchyDisplayFolder;
    }

    public InstanceSelection getInstanceSelection() {
        return InstanceSelection.NONE; // FIXME
    }

    public GroupingBehavior getGroupingBehavior() {
        return null; // FIXME
    }

    public Set<Origin> getOrigin() {
        return originSet;
    }

    public boolean isParentChild() {
        return originSet.containsAll(Origin.PARENT_CHILD);
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
            new NamedListImpl<XmlaOlap4jMember>(memberList);
        return Olap4jUtil.cast(list);
    }

    public Structure getStructure() {
        return structure;
    }

    public boolean equals(Object obj) {
        return obj == this
            || (obj instanceof XmlaOlap4jHierarchy)
            && this.uniqueName.equals(
                ((XmlaOlap4jHierarchy) obj).getUniqueName());
    }
}

// End XmlaOlap4jHierarchy.java
