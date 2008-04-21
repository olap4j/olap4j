/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.metadata.Cube;
import org.olap4j.metadata.Dimension;
import org.olap4j.metadata.Member;
import org.olap4j.*;
import org.olap4j.mdx.AxisNode;
import org.olap4j.mdx.CallNode;
import org.olap4j.mdx.CubeNode;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.MemberNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.Syntax;

import java.util.*;
import java.sql.SQLException;

/**
 * Query model.
 *
 * @author jhyde, jdixon
 * @version $Id$
 * @since May 29, 2007
 */
public class Query {

    protected final String name;
    protected Map<Axis, QueryAxis> axes = new HashMap<Axis, QueryAxis>();
    protected QueryAxis across;
    protected QueryAxis down;
    protected QueryAxis filter;
    protected QueryAxis unused;
    protected final Cube cube;
    protected Map<String, QueryDimension> dimensionMap =
        new HashMap<String, QueryDimension>();
    private final OlapConnection connection;
    private final SelectionFactory selectionFactory =
        new SelectionFactory(this);

    public Query(String name, Cube cube) throws SQLException {
        super();
        this.name = name;
        this.cube = cube;
        this.connection =
            cube.getSchema().getCatalog().getMetaData()
                .getConnection().unwrap(OlapConnection.class);
        this.unused = new QueryAxis(this, null);
        for (Dimension dimension : cube.getDimensions()) {
            QueryDimension queryDimension = new QueryDimension(
                this, dimension);
            unused.getDimensions().add(queryDimension);
            dimensionMap.put(queryDimension.getName(), queryDimension);
        }
        across = new QueryAxis(this, Axis.COLUMNS);
        down = new QueryAxis(this, Axis.ROWS);
        filter = new QueryAxis(this, Axis.FILTER);
        axes.put(null, unused);
        axes.put(Axis.COLUMNS, across);
        axes.put(Axis.ROWS, down);
        axes.put(Axis.FILTER, filter);
    }

    /**
     * Returns the MDX parse tree behind this Query.
     */
    public SelectNode getSelect() {
        return new Olap4jNodeConverter().toOlap4j(this);
    }

    public Cube getCube() {
        return cube;
    }

    public QueryDimension getDimension(String name) {
        return dimensionMap.get(name);
    }

    /**
     * Swaps rows and columns axes. Only applicable if there are two axes.
     */
    public void swapAxes() {
        // Only applicable if there are two axes - plus filter and unused.
        if (axes.size() != 4) {
            throw new IllegalArgumentException();
        }
        List<QueryDimension> tmpAcross = new ArrayList<QueryDimension>();
        tmpAcross.addAll(across.getDimensions());

        List<QueryDimension> tmpDown = new ArrayList<QueryDimension>();
        tmpDown.addAll(down.getDimensions());

        across.getDimensions().clear();
        down.getDimensions().clear();

        across.getDimensions().addAll(tmpDown);
        down.getDimensions().addAll(tmpAcross);
    }

    public Map<Axis, QueryAxis> getAxes() {
        return axes;
    }

    public QueryAxis getunusedAxis() {
        return unused;
    }

    public boolean validate() throws OlapException {
        for (Dimension dimension :  cube.getDimensions()) {
            QueryDimension queryDimension =
                getDimension(dimension.getName());
            if (queryDimension == null) {
                // TODO log this better
                return false;
            }
            Member member = dimension.getDefaultHierarchy().getDefaultMember();
            if (queryDimension.getAxis() == null ||
                queryDimension.getAxis().getLocation() == null) {
                queryDimension.getSelections().clear();
                queryDimension.getSelections().add(queryDimension.createSelection(member));
            } else {
                if (queryDimension.getSelections().size() == 0) {
                    queryDimension.getSelections().add(queryDimension.createSelection(member));
                }
            }
        }
        return true;
    }

    public CellSet execute() throws OlapException {
        SelectNode mdx = getSelect();
        OlapStatement olapStatement = connection.createStatement();
        return olapStatement.executeOlapQuery(mdx);
    }

    public String getName() {
        return name;
    }

    public Locale getLocale() {
        return Locale.getDefault(); // todo:
    }

    public SelectionFactory getSelectionFactory() {
        return selectionFactory;
    }
    
    private static class Olap4jNodeConverter {

        public SelectNode toOlap4j(Query query) {
            List<IdentifierNode> list = Collections.emptyList();
            List<ParseTreeNode> withList = Collections.emptyList();
            List<QueryAxis> axisList = new ArrayList<QueryAxis>();
            axisList.add(query.getAxes().get(Axis.COLUMNS));
            axisList.add(query.getAxes().get(Axis.ROWS));

            AxisNode filterAxis = null;
            if (query.getAxes().containsKey(Axis.FILTER)) {
                final QueryAxis axis = query.getAxes().get(Axis.FILTER);
                if (!axis.dimensions.isEmpty()) {
                    filterAxis = toOlap4j(axis);
                }
            }
            return new SelectNode(
                null,
                withList,
                toOlap4j(axisList),
                new CubeNode(
                    null,
                    query.getCube()),
                filterAxis,
                list);
        }

        private CallNode generateSetCall(ParseTreeNode... args) {
            final CallNode callNode = new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    args
                );
            
            return callNode;
        }

        private CallNode generateListSetCall(List<ParseTreeNode> cnodes) {
            final CallNode callNode = new CallNode(
                    null,
                    "{}",
                    Syntax.Braces,
                    cnodes
                );
            return callNode;
        }

        private CallNode generateListTupleCall(List<ParseTreeNode> cnodes) {
            final CallNode callNode = new CallNode(
                    null,
                    "()",
                    Syntax.Parentheses,
                    cnodes
                );
            return callNode;
        }

        protected CallNode getMemberSet(QueryDimension dimension) {
            final CallNode cnode = new CallNode(
                  null,
                  "{}",
                  Syntax.Braces,
                  toOlap4j(dimension)
                );
            return cnode;
        }

        protected CallNode crossJoin(QueryDimension dim1, QueryDimension dim2) {
            return new CallNode(
                    null,
                    "CrossJoin",
                    Syntax.Function,
                    getMemberSet(dim1),
                    getMemberSet(dim2));
        }

        //
        // This method merges the selections into a single
        // MDX axis selection.  Right now we do a simple 
        // crossjoin
        //
        private AxisNode toOlap4j(QueryAxis axis) {
            CallNode callNode = null;
            int numDimensions = axis.getDimensions().size();
            if( axis.getLocation() == Axis.FILTER ) {
                // need a tuple
                // need a crossjoin
                List<ParseTreeNode> members = new ArrayList<ParseTreeNode>();
                for( int dimNo = 0; dimNo < numDimensions; dimNo++ ) {
                    QueryDimension dimension = 
                        axis.getDimensions().get( dimNo );
                    if( dimension.getSelections().size() == 1 ) {
                        members.addAll(toOlap4j(dimension));
                    }
                }
                callNode = generateListTupleCall(members);
            } else if( numDimensions == 1 ) {
                QueryDimension dimension = axis.getDimensions().get( 0 );
                List<ParseTreeNode> members = toOlap4j(dimension);
                callNode = generateListSetCall(members);
            } else if( numDimensions == 2 ) {
                callNode = 
                    crossJoin( axis.getDimensions().get(0), 
                        axis.getDimensions().get(1));
            } else {
                // need a longer crossjoin
                // start from the back of the list;
                List<QueryDimension> dims = axis.getDimensions();
                callNode = getMemberSet(dims.get(dims.size() - 1));
                for( int i = dims.size() - 2; i >= 0; i-- ) {
                    CallNode memberSet = getMemberSet(dims.get(i));
                    callNode = new CallNode(
                                null,
                                "CrossJoin",
                                Syntax.Function,
                                memberSet,
                                callNode);
                }
            }
            return new AxisNode(
                    null,
                    false,
                    axis.getLocation(),
                    new ArrayList<IdentifierNode>(),
                    callNode);
                
        }

        private List<ParseTreeNode> toOlap4j(QueryDimension dimension) {
            List<ParseTreeNode> members = new ArrayList<ParseTreeNode>();
            for (Selection selection : dimension.getSelections()) {
                members.add(toOlap4j(selection));
            }
            return members;
        }

        private ParseTreeNode toOlap4j(Selection selection) {
            try {
                return toOlap4j(selection.getMember(), selection.getOperator());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private ParseTreeNode toOlap4j(Member member, Selection.Operator oper) {
            ParseTreeNode node = null;
            try {
                switch(oper) {
                    case MEMBER:
                        node = new MemberNode(null, member);
                        break;
                    case SIBLINGS:
                        node = new CallNode(
                                null,
                                "Siblings",
                                Syntax.Property,
                                new MemberNode(null, member)
                            );
                        break;
                    case CHILDREN:
                        node = new CallNode(
                                null,
                                "Children",
                                Syntax.Property,
                                new MemberNode(null, member)
                            );
                        break;
                    case INCLUDE_CHILDREN:
                        node = generateSetCall(
                                new MemberNode(null, member),
                                toOlap4j(member, Selection.Operator.CHILDREN)
                               );
                        break;
                    case DESCENDANTS:
                        node = new CallNode(
                                null,
                                "Descendants",
                                Syntax.Function,
                                new MemberNode(null, member)
                            );
                        break;
                    case ANCESTORS:
                        node = new CallNode(
                                null,
                                "Ascendants",
                                Syntax.Function,
                                new MemberNode(null, member)
                            );
                        break;
                    default:
                        System.out.println("NOT IMPLEMENTED: " + oper);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return node;
        }

        private List<AxisNode> toOlap4j(List<QueryAxis> axes) {
            final ArrayList<AxisNode> axisList = new ArrayList<AxisNode>();
            for (QueryAxis axis : axes) {
                axisList.add(toOlap4j(axis));
            }
            return axisList;
        }
    }
}

// End Query.java
