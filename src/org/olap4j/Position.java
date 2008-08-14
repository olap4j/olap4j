/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2006-2006 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j;

import org.olap4j.metadata.Member;

import java.util.List;

/**
 * Position on one of the {@link CellSetAxis} objects in a {@link CellSet}.
 *
 * <p>An axis has a particular dimensionality, that is, a set of one or more
 * dimensions which will appear on that axis, and every position on that axis
 * will have a member of each of those dimensions. For example, in the MDX
 * query</p>
 *
 * <blockquote>
 *  <code>SELECT {[Measures].[Unit Sales], [Measures].[Store Sales]} ON
 *  COLUMNS,<br>
 * &nbsp;&nbsp;&nbsp; CrossJoin(<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {[Gender].Members},<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {[Product].[Food],
 *  [Product].[Drink]}) ON ROWS<br>
 *  FROM [Sales]</code>
 * </blockquote>
 *
 * <p>the <code>COLUMNS</code> axis has dimensionality
 * {<code>[Measures]</code>} and the <code>ROWS</code> axis has dimensionality
 * {<code>[Gender]</code>, <code>[Product]</code>}. In the result,</p>
 *
 *  <table border="1" id="table1" cellpadding="3">
 *      <tr>
 *          <td bgcolor="#E0E0E0"><b><i>Gender</i></b></td>
 *          <td bgcolor="#E0E0E0"><b><i>Product</i></b></td>
 *          <td bgcolor="#E0E0E0"><b>Unit Sales</b></td>
 *          <td bgcolor="#E0E0E0"><b>Store Sales</b></td>
 *      </tr>
 *      <tr>
 *          <td bgcolor="#E0E0E0"><b>All Gender</b></td>
 *          <td bgcolor="#E0E0E0"><b>Food</b></td>
 *          <td align="right">191,940</td>
 *          <td align="right">409,035.59</td>
 *      </tr>
 *      <tr>
 *          <td bgcolor="#E0E0E0"><b>All Gender</b></td>
 *          <td bgcolor="#E0E0E0"><b>Drink</b></td>
 *          <td align="right">24,597</td>
 *          <td align="right">48,836.21</td>
 *      </tr>
 *      <tr>
 *          <td bgcolor="#E0E0E0"><b>F</b></td>
 *          <td bgcolor="#E0E0E0"><b>Food</b></td>
 *          <td align="right">94,814</td>
 *          <td align="right">203,094.17</td>
 *      </tr>
 *      <tr>
 *          <td bgcolor="#E0E0E0"><b>F</b></td>
 *          <td bgcolor="#E0E0E0"><b>Drink</b></td>
 *          <td align="right">12,202</td>
 *          <td align="right">24,457.37</td>
 *      </tr>
 *      <tr>
 *          <td bgcolor="#E0E0E0"><b>M</b></td>
 *          <td bgcolor="#E0E0E0"><b>Food</b></td>
 *          <td align="right">97,126</td>
 *          <td align="right">205,941.42</td>
 *      </tr>
 *      <tr>
 *          <td bgcolor="#E0E0E0"><b>M</b></td>
 *          <td bgcolor="#E0E0E0"><b>Drink</b></td>
 *          <td align="right">12,395</td>
 *          <td align="right">24,378.84</td>
 *      </tr>
 *  </table>
 *
 * <p>each of the six positions on the <code>ROWS</code> axis has two members,
 * consistent with its dimensionality of 2. The <code>COLUMNS</code> axis has
 * two positions, each with one member.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since Aug 22, 2006
 */
public interface Position {
    /**
     * Returns the list of Member objects at this position.
     *
     * <p>Recall that the {@link CellSetAxisMetaData#getHierarchies()}
     * method describes the hierarchies which occur on an axis. The positions on
     * that axis must conform. Suppose that the ROWS axis of a given statement
     * returns <code>{[Gender], [Store]}</code>. Then every Position on
     * that axis will have two members: the first a member of the [Gender]
     * dimension, the second a member of the [Store] dimension.</p>
     *
     * @return A list of Member objects at this Position.
     */
    public List<Member> getMembers();

    /**
     * Returns the zero-based ordinal of this Position on its
     * {@link CellSetAxis}.
     *
     * @return ordinal of this Position
     */
    int getOrdinal();
}

// End Position.java
