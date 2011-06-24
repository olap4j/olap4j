/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.olap4j.Axis;
import org.olap4j.mdx.AxisNode;
import org.olap4j.mdx.CallNode;
import org.olap4j.mdx.CubeNode;
import org.olap4j.mdx.DimensionNode;
import org.olap4j.mdx.IdentifierNode;
import org.olap4j.mdx.LevelNode;
import org.olap4j.mdx.LiteralNode;
import org.olap4j.mdx.MemberNode;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.mdx.SelectNode;
import org.olap4j.mdx.Syntax;
import org.olap4j.metadata.Level;
import org.olap4j.metadata.Member;

/**
 * Utility class to convert a Query object to a SelectNode.
 */
abstract class Olap4jNodeConverter {

    public static SelectNode toOlap4j(Query query) {
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

    private static CallNode generateSetCall(ParseTreeNode... args) {
        return
            new CallNode(
                null,
                "{}",
                Syntax.Braces,
                args);
    }

    private static CallNode generateListSetCall(List<ParseTreeNode> cnodes) {
        return
            new CallNode(
                null,
                "{}",
                Syntax.Braces,
                cnodes);
    }

    private static CallNode generateListTupleCall(List<ParseTreeNode> cnodes) {
        return
            new CallNode(
                null,
                "()",
                Syntax.Parentheses,
                cnodes);
    }

    protected static CallNode generateCrossJoin(List<ParseTreeNode> selections)
    {
        ParseTreeNode sel1 = selections.remove(0);
        if (sel1 instanceof MemberNode) {
            sel1 = generateSetCall(sel1);
        }
        if (selections.size() == 1) {
            ParseTreeNode sel2 = selections.get(0);
            if (sel2 instanceof MemberNode) {
                sel2 = generateSetCall(sel2);
            }
            return new CallNode(
                null, "CrossJoin", Syntax.Function, sel1, sel2);
        } else {
            return new CallNode(
                null, "CrossJoin", Syntax.Function, sel1,
                generateCrossJoin(selections));
        }
    }

    protected static CallNode generateUnion(List<List<ParseTreeNode>> unions) {
        if (unions.size() > 2) {
            List<ParseTreeNode> first = unions.remove(0);
            return new CallNode(
                null, "Union", Syntax.Function,
                generateCrossJoin(first),
                generateUnion(unions));
        } else {
            return new CallNode(
                null, "Union", Syntax.Function,
                generateCrossJoin(unions.get(0)),
                generateCrossJoin(unions.get(1)));
        }
    }

    protected static CallNode generateHierarchizeUnion(
        List<List<ParseTreeNode>> unions)
    {
        return new CallNode(
            null, "Hierarchize", Syntax.Function,
            generateUnion(unions));
    }

    /**
     * Algorithm:<ul>
     * <li>generate all combinations of dimension groups
     * <li>skip the selection if has a context
     * <li>for all the selections with context, resolve them last
     * <li>union all combinations
     * </ul>
     */
    private static void generateUnionsRecursively(
        QueryAxis axis,
        int dim,
        List<ParseTreeNode> curr,
        List<List<ParseTreeNode>> unions,
        List<Selection> selsWithContext,
        List<List<ParseTreeNode>> contextUnions)
    {
        ParseTreeNode exceptSet = null;
        QueryDimension qDim = axis.getDimensions().get(dim);

        List<Selection> exclusionSelections = qDim.getExclusions();
        List<ParseTreeNode> exclusionNodes = new ArrayList<ParseTreeNode>();

        // Check if any exclusions are selected for this dimension
        // and convert them to a list of nodes and then later a set
        for (Selection exclusion : exclusionSelections) {
            exclusionNodes.add(toOlap4j(exclusion));
        }
        if (exclusionNodes.size() > 0) {
            exceptSet = generateListSetCall(exclusionNodes);
        }

        for (Selection sel : qDim.getInclusions()) {
            ParseTreeNode selectionNode = toOlap4j(sel);
            // If a the querydimension should return only hierarchy
            // consistent results, generate a filter that checks
            // inclusions for ancestors in higher levels
            if (qDim.isHierarchyConsistent()
                    && qDim.getInclusions().size() > 1)
            {
                Integer currentDepth = null;
                if (sel.getRootElement() instanceof Member) {
                    currentDepth = ((Member)sel.getRootElement()).getDepth();
                } else if (sel.getRootElement() instanceof Level) {
                    currentDepth = ((Level)sel.getRootElement()).getDepth();
                }
                selectionNode =
                   toHierarchyConsistentNode(selectionNode, currentDepth, qDim);
            }
            // If a sort Order was specified for this dimension
            // apply it for this inclusion
            if (qDim.getSortOrder() != null) {
                CallNode currentMemberNode =
                    new CallNode(
                        null,
                        "CurrentMember",
                        Syntax.Property,
                        new DimensionNode(null, sel.getDimension()));
                CallNode currentMemberNameNode =
                    new CallNode(
                        null,
                        "Name",
                        Syntax.Property,
                        currentMemberNode);
                selectionNode =
                    new CallNode(
                        null,
                        "Order",
                        Syntax.Function,
                        generateSetCall(selectionNode),
                        currentMemberNameNode,
                        LiteralNode.createSymbol(
                            null,
                            qDim.getSortOrder().name()));
            }
            // If there are exlclusions wrap the ordered selection
            // in an Except() function
            if (exceptSet != null) {
                selectionNode =
                    new CallNode(
                        null,
                        "Except",
                        Syntax.Function,
                        generateSetCall(selectionNode),
                        exceptSet);
            }
            if (sel.getSelectionContext() != null
                && sel.getSelectionContext().size() > 0)
            {
                // selections that have a context are treated
                // differently than the rest of the MDX generation
                if (!selsWithContext.contains(sel)) {
                    ArrayList<ParseTreeNode> sels =
                        new ArrayList<ParseTreeNode>();
                    for (int i = 0; i < axis.getDimensions().size(); i++) {
                        if (dim == i) {
                            sels.add(selectionNode);
                        } else {
                            // return the selections in the correct
                            // dimensional order
                            QueryDimension dimension =
                                axis.getDimensions().get(i);

                            boolean found = false;
                            for (Selection selection
                                : sel.getSelectionContext())
                            {
                                if (selection.getDimension().equals(
                                        dimension.getDimension()))
                                {
                                    sels.add(toOlap4j(selection));
                                    found = true;
                                }
                            }
                            if (!found) {
                                // add the first selection of the dimension
                                if (dimension.getInclusions().size() > 0) {
                                    sels.add(toOlap4j(
                                        dimension.getInclusions().get(0)));
                                }
                            }
                        }
                    }
                    contextUnions.add(sels);
                    selsWithContext.add(sel);
                }
            } else {
                List<ParseTreeNode> ncurr = new ArrayList<ParseTreeNode>();
                if (curr != null) {
                    ncurr.addAll(curr);
                }
                ncurr.add(selectionNode);
                if (dim == axis.getDimensions().size() - 1) {
                    // last dimension
                    unions.add(ncurr);
                } else {
                    generateUnionsRecursively(
                        axis, dim + 1, ncurr, unions, selsWithContext,
                        contextUnions);
                }
            }
        }
    }

    /*
     * This method merges the selections into a single
     * MDX axis selection.  Right now we do a simple
     * crossjoin.
     * It might return null if there are no dimensions placed on the axis.
     */
    private static AxisNode toOlap4j(QueryAxis axis) {
        CallNode callNode = null;
        int numDimensions = axis.getDimensions().size();
        if (numDimensions == 0) {
            return null;
        } else if (numDimensions == 1) {
            QueryDimension dimension = axis.getDimensions().get(0);
            List<ParseTreeNode> members = toOlap4j(dimension);
            callNode = generateListSetCall(members);
        } else {
            // generate union sets of selections in each dimension
            List<List<ParseTreeNode>> unions =
                new ArrayList<List<ParseTreeNode>>();
            List<Selection> selsWithContext = new ArrayList<Selection>();
            List<List<ParseTreeNode>> contextUnions =
                new ArrayList<List<ParseTreeNode>>();
            generateUnionsRecursively(
                axis, 0, null, unions, selsWithContext, contextUnions);
            unions.addAll(contextUnions);
            if (unions.size() > 1) {
                callNode = generateHierarchizeUnion(unions);
            } else {
                callNode = generateCrossJoin(unions.get(0));
            }
        }

        // We might need to sort the whole axis.
        ParseTreeNode sortedNode = null;
        if (axis.getSortOrder() != null) {
            LiteralNode evaluatorNode =
                 LiteralNode.createSymbol(
                     null,
                     axis.getSortIdentifierNodeName());
            sortedNode =
                new CallNode(
                    null,
                    "Order",
                    Syntax.Function,
                    callNode,
                    evaluatorNode,
                    LiteralNode.createSymbol(
                        null, axis.getSortOrder().name()));
        } else {
            sortedNode = callNode;
        }

        return new AxisNode(
            null,
            axis.isNonEmpty(),
            axis.getLocation(),
            new ArrayList<IdentifierNode>(),
            sortedNode);
    }

    private static List<ParseTreeNode> toOlap4j(QueryDimension dimension) {
        // Let's build a first list of included members.
        List<ParseTreeNode> includeList = new ArrayList<ParseTreeNode>();
        Map<Integer, List<ParseTreeNode>> levelNodes =
            new HashMap<Integer, List<ParseTreeNode>>();
        for (Selection selection : dimension.getInclusions()) {
            ParseTreeNode selectionNode = toOlap4j(selection);
            // If a the querydimension should return only hierarchy
            // consistent results, generate a filter that checks
            // inclusions for ancestors in higher levels
            if (dimension.isHierarchyConsistent()
                && dimension.getInclusions().size() > 1)
            {
                Integer curdepth = 0;
                if (selection.getRootElement() instanceof Member) {
                    curdepth = ((Member)selection.getRootElement()).getDepth();
                } else if (selection.getRootElement() instanceof Level) {
                    curdepth = ((Level)selection.getRootElement()).getDepth();
                }

                if (levelNodes.get(curdepth) != null) {
                    levelNodes.get(curdepth).add(selectionNode);
                } else {
                    List<ParseTreeNode> nodes = new ArrayList<ParseTreeNode>();
                    nodes.add(selectionNode);
                    levelNodes.put(curdepth, nodes);
                }
            } else {
                includeList.add(selectionNode);
            }
        }
        if (dimension.isHierarchyConsistent()
            && dimension.getInclusions().size() > 1)
        {
            Integer levelDepths[] =
                levelNodes.keySet()
                    .toArray(new Integer[levelNodes.keySet().size()]);

            Arrays.sort(levelDepths);

            for (Integer depth : levelDepths) {
                ParseTreeNode levelNode =
                    generateListSetCall(levelNodes.get(depth));

                levelNode =
                    toHierarchyConsistentNode(levelNode, depth, dimension);
                includeList.add(levelNode);
            }
        }
        // If a sort order was specified, we need to wrap the inclusions in an
        // Order() mdx function.
        List<ParseTreeNode> orderedList = new ArrayList<ParseTreeNode>();
        if (dimension.getSortOrder() != null) {
            CallNode currentMemberNode = new CallNode(
                null,
                "CurrentMember",
                Syntax.Property,
                new DimensionNode(null, dimension.getDimension()));
            CallNode currentMemberNameNode = new CallNode(
                null,
                "Name",
                Syntax.Property,
                currentMemberNode);
            orderedList.add(
                new CallNode(
                    null,
                    "Order",
                    Syntax.Function,
                    generateListSetCall(includeList),
                    currentMemberNameNode,
                    LiteralNode.createSymbol(
                        null, dimension.getSortOrder().name())));
        } else {
            orderedList.addAll(includeList);
        }

        // We're not done yet. We might have to exclude members from the
        // inclusion, so we might have to wrap the list in a mdx Except()
        // function call.
        List<ParseTreeNode> listWithExclusions =
            new ArrayList<ParseTreeNode>();
        if (dimension.getExclusions().size() > 0) {
            List<ParseTreeNode> excludedMembers =
                new ArrayList<ParseTreeNode>();
            for (Selection selection : dimension.getExclusions()) {
                excludedMembers.add(toOlap4j(selection));
            }
            listWithExclusions.add(
                new CallNode(
                    null,
                    "Except",
                    Syntax.Function,
                    generateListSetCall(orderedList),
                    generateListSetCall(excludedMembers)));
        } else {
            listWithExclusions.addAll(orderedList);
        }

        // Do we need to wrap it all in a Hierarchize function?
        List<ParseTreeNode> listWithHierarchy =
            new ArrayList<ParseTreeNode>();
        if (dimension.getHierarchizeMode() != null) {
            CallNode hierarchyNode;
            // There are two modes available, PRE and POST.
            if (dimension.getHierarchizeMode().equals(
                    QueryDimension.HierarchizeMode.PRE))
            {
                // In pre mode, we don't add the "POST" literal.
                hierarchyNode = new CallNode(
                    null,
                    "Hierarchize",
                    Syntax.Function,
                    generateListSetCall(listWithExclusions));
            } else if (dimension.getHierarchizeMode().equals(
                    QueryDimension.HierarchizeMode.POST))
            {
                hierarchyNode = new CallNode(
                    null,
                    "Hierarchize",
                    Syntax.Function,
                    generateListSetCall(listWithExclusions),
                    LiteralNode.createSymbol(
                        null, dimension.getHierarchizeMode().name()));
            } else {
                throw new RuntimeException("Missing value handler.");
            }
            listWithHierarchy.add(hierarchyNode);
        } else {
            listWithHierarchy.addAll(listWithExclusions);
        }

        return listWithHierarchy;
    }

    private static ParseTreeNode toOlap4j(Selection selection) {
        try {
            return selection.visit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static ParseTreeNode toOlap4j(
        Member member,
        Selection.Operator oper)
    {
        ParseTreeNode node = null;
        try {
            switch (oper) {
            case MEMBER:
                node = new MemberNode(null, member);
                break;
            case SIBLINGS:
                node =
                    new CallNode(
                        null,
                        "Siblings",
                        Syntax.Property,
                        new MemberNode(null, member));
                break;
            case CHILDREN:
                node =
                    new CallNode(
                        null,
                        "Children",
                        Syntax.Property,
                        new MemberNode(null, member));
                break;
            case INCLUDE_CHILDREN:
                node =
                    generateSetCall(
                        new MemberNode(null, member),
                        toOlap4j(member, Selection.Operator.CHILDREN));
                break;
            case DESCENDANTS:
                node =
                    new CallNode(
                        null,
                        "Descendants",
                        Syntax.Function,
                        new MemberNode(null, member));
                break;
            case ANCESTORS:
                node =
                    new CallNode(
                        null,
                        "Ascendants",
                        Syntax.Function,
                        new MemberNode(null, member));
                break;
            default:
                System.out.println("NOT IMPLEMENTED: " + oper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return node;
    }

    static ParseTreeNode toOlap4j(
        Level level,
        Selection.Operator oper)
    {
        ParseTreeNode node = null;
        try {
            switch (oper) {
            case MEMBERS:
                node =
                    new CallNode(
                        null,
                        "Members",
                        Syntax.Property,
                        new LevelNode(null, level));
                break;
            default:
                System.out.println("NOT IMPLEMENTED: " + oper);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return node;
    }

    private static List<AxisNode> toOlap4j(List<QueryAxis> axes) {
        final ArrayList<AxisNode> axisList = new ArrayList<AxisNode>();
        for (QueryAxis axis : axes) {
            AxisNode axisNode = toOlap4j(axis);
            if (axisNode != null) {
                axisList.add(toOlap4j(axis));
            }
        }
        return axisList;
    }

    private static ParseTreeNode toHierarchyConsistentNode(
        ParseTreeNode selectionNode,
        Integer maxDepth,
        QueryDimension qDim)
    {
        // If a the querydimension should return only hierarchy
        // consistent results, generate a filter that checks
        // inclusions for ancestors in higher levels
        if (qDim.getInclusions().size() > 1) {
            CallNode currentMemberNode =
                new CallNode(
                    null,
                    "CurrentMember",
                    Syntax.Property,
                    new DimensionNode(null, qDim.getDimension()));

            Map<Integer, Level> levels = new HashMap<Integer, Level>();
            for (Selection s : qDim.getInclusions()) {
                if (s.getRootElement() instanceof Member) {
                    Integer d = ((Member)s.getRootElement()).getDepth();
                    if (!levels.containsKey(d)) {
                        Level lvl = ((Member)s.getRootElement()).getLevel();
                        levels.put(d, lvl);
                    }
                } else if (s.getRootElement() instanceof Level) {
                    Integer d = ((Level)s.getRootElement()).getDepth();
                    if (!levels.containsKey(d)) {
                        Level lvl = ((Level)s.getRootElement());
                        levels.put(d, lvl);
                    }
                }
            }
            Integer levelDepths[] =
                levels.keySet()
                    .toArray(new Integer[levels.keySet().size()]);

            Arrays.sort(levelDepths);

            List<CallNode> inConditions = new ArrayList<CallNode>();
            for (Integer i = 0; i < levelDepths.length - 1; i++) {
                Level currentLevel = levels.get(levelDepths[i]);
                if (levelDepths[i] < maxDepth
                    && currentLevel.getLevelType() != Level.Type.ALL)
                {
                CallNode ancestorNode =
                    new CallNode(
                            null,
                            "Ancestor",
                            Syntax.Function,
                            currentMemberNode,
                            new LevelNode(null, currentLevel));

                List <ParseTreeNode> ancestorList =
                    new ArrayList<ParseTreeNode>();

                for (Selection anc : qDim.getInclusions()) {
                    if (anc.getRootElement() instanceof Member) {
                        Level l = ((Member)anc.getRootElement()).getLevel();
                        if (l.equals(levels.get(levelDepths[i]))) {
                            ancestorList.add(anc.visit());
                        }
                    } else if (anc.getRootElement() instanceof Level) {
                        Level l = ((Level)anc.getRootElement());
                        if (l.equals(levels.get(levelDepths[i]))) {
                            ancestorList.add(anc.visit());
                        }
                    }
                }
                CallNode ancestorSet = generateListSetCall(ancestorList);
                CallNode inClause = new CallNode(
                        null,
                        "IN",
                        Syntax.Infix,
                        ancestorNode,
                        ancestorSet);
                inConditions.add(inClause);
                }
            }
            if (inConditions.size() > 0) {
                CallNode chainedIn = inConditions.get(0);
                if (inConditions.size() > 1) {
                    for (int c = 1;c < inConditions.size();c++) {
                        chainedIn = new CallNode(
                                null,
                                "AND",
                                Syntax.Infix,
                                chainedIn,
                                inConditions.get(c));
                    }
                }

                return new CallNode(
                    null,
                    "Filter",
                    Syntax.Function,
                    generateSetCall(selectionNode),
                    chainedIn);
            }
        }
        return selectionNode;
    }
}

// End Olap4jNodeConverter.java










