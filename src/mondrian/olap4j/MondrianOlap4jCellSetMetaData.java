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

import org.olap4j.CellSetMetaData;
import org.olap4j.CellSetAxisMetaData;
import org.olap4j.Axis;
import org.olap4j.metadata.Property;
import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Hierarchy;

import java.util.List;
import java.util.ArrayList;
import java.sql.SQLException;

import mondrian.olap.Query;
import mondrian.olap.QueryAxis;
import mondrian.olap.type.Type;
import mondrian.olap.type.SetType;
import mondrian.olap.type.TupleType;

/**
 * Implementation of {@link org.olap4j.CellSetMetaData}
 * for the Mondrian OLAP engine.
 *
 * @author jhyde
 * @version $Id: $
 * @since Jun 13, 2007
 */
class MondrianOlap4jCellSetMetaData implements CellSetMetaData {
    private final MondrianOlap4jStatement olap4jStatement;
    private final Query query;

    MondrianOlap4jCellSetMetaData(
        MondrianOlap4jStatement olap4jStatement,
        Query query)
    {
        this.olap4jStatement = olap4jStatement;
        this.query = query;
    }

    // implement CellSetMetaData

    public List<Property> getCellProperties() {
        throw new UnsupportedOperationException();
    }

    public Cube getCube() {
        return olap4jStatement.olap4jConnection.toOlap4j(query.getCube());
    }

    public List<CellSetAxisMetaData> getAxesMetaData() {
        final List<CellSetAxisMetaData> list =
            new ArrayList<CellSetAxisMetaData>();
        final MondrianOlap4jConnection olap4jConnection =
            olap4jStatement.olap4jConnection;
        for (final QueryAxis queryAxis : query.getAxes()) {
            list.add(
                new CellSetAxisMetaData() {
                    public Axis getAxis() {
                        return olap4jConnection.toOlap4j(
                            queryAxis.getAxisOrdinal());
                    }

                    public List<Hierarchy> getHierarchies() {
                        final SetType setType =
                            (SetType) queryAxis.getSet().getType();
                        final Type type = setType.getElementType();
                        List<Hierarchy> hierarchyList =
                            new ArrayList<Hierarchy>();
                        if (type instanceof TupleType) {
                            final TupleType tupleType = (TupleType) type;
                            for (Type elementType : tupleType.elementTypes) {
                                hierarchyList.add(
                                    olap4jConnection.toOlap4j(
                                        elementType.getHierarchy()));
                            }
                        } else {
                            hierarchyList.add(
                                olap4jConnection.toOlap4j(type.getHierarchy()));
                        }
                        return hierarchyList;
                    }

                    public List<Property> getProperties() {
                        throw new UnsupportedOperationException();
                    }
                }
            );
        }
        return list;
    }

    // implement ResultSetMetaData

    public int getColumnCount() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isSearchable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isCurrency(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int isNullable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isSigned(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getColumnLabel(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getColumnName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getSchemaName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getPrecision(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getScale(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getTableName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getCatalogName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getColumnType(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getColumnTypeName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isReadOnly(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isWritable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public String getColumnClassName(int column) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // implement Wrapper

    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (false) {
            return null;
        }
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (false) {
            return false;
        }
        throw new UnsupportedOperationException();
    }
}

// End MondrianOlap4jCellSetMetaData.java
