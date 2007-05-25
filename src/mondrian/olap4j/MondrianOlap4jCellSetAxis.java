/*
// $Id: $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package mondrian.olap4j;

import org.olap4j.*;
import org.olap4j.Position;
import org.olap4j.metadata.Hierarchy;
import org.olap4j.metadata.Member;
import org.olap4j.metadata.Property;

import java.util.*;

/**
 * <code>MondrianOlap4jCellSetAxis</code> ...
 *
 * @author jhyde
 * @version $Id: $
 * @since May 24, 2007
 */
class MondrianOlap4jCellSetAxis implements CellSetAxis, CellSetAxisMetaData {
    private final MondrianOlap4jCellSet olap4jCellSet;
    private final mondrian.olap.QueryAxis queryAxis;
    private final mondrian.olap.Axis axis;

    MondrianOlap4jCellSetAxis(
        MondrianOlap4jCellSet olap4jCellSet,
        mondrian.olap.QueryAxis queryAxis,
        mondrian.olap.Axis axis)
    {
        assert olap4jCellSet != null;
        assert queryAxis != null;
        assert axis != null;
        this.olap4jCellSet = olap4jCellSet;
        this.queryAxis = queryAxis;
        this.axis = axis;
    }

    public int getOrdinal() {
        return queryAxis.getAxisOrdinal().logicalOrdinal();
    }

    public CellSet getCellSet() {
        return olap4jCellSet;
    }

    public CellSetAxisMetaData getAxisMetaData() {
        throw new UnsupportedOperationException();
    }

    public List<Position> getPositions() {
        return new AbstractList<Position>() {
            public Position get(final int index) {
                final mondrian.olap.Position mondrianPosition =
                    axis.getPositions().get(index);
                return new MondrianOlap4jPosition(mondrianPosition, index);
            }

            public int size() {
                return axis.getPositions().size();
            }
        };
    }

    public int getPositionCount() {
        throw new UnsupportedOperationException();
    }

    public ListIterator<Position> iterate() {
        throw new UnsupportedOperationException();
    }

    // implement CellSetAxisMetaData

    public org.olap4j.Axis getAxis() {
        throw new UnsupportedOperationException();
    }

    public List<Hierarchy> getHierarchies() {
        throw new UnsupportedOperationException();
    }

    public List<Property> getProperties() {
        throw new UnsupportedOperationException();
    }

    private class MondrianOlap4jPosition implements Position {
        private final mondrian.olap.Position mondrianPosition;
        private final int index;

        public MondrianOlap4jPosition(
            mondrian.olap.Position mondrianPosition, int index) {
            this.mondrianPosition = mondrianPosition;
            this.index = index;
        }

        public List<Member> getMembers() {
            return new AbstractList<Member>() {

                public Member get(int index) {
                    final mondrian.olap.Member mondrianMember =
                        mondrianPosition.get(index);
                    return new MondrianOlap4jMember(
                        olap4jCellSet.olap4jStatement.olap4jConnection.olap4jSchema,
                        mondrianMember);
                }

                public int size() {
                    return mondrianPosition.size();
                }
            };
        }

        public int getOrdinal() {
            return index;
        }
    }
}

// End MondrianOlap4jCellSetAxis.java
