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
package org.olap4j.mdx;

import org.olap4j.type.Type;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Parse tree model for an MDX {@code DRILLTHROUGH} statement.
 *
 * @author jhyde
 * @since Feb 24, 2012
 */
public class DrillThroughNode implements ParseTreeNode {
    private final ParseRegion region;
    private final SelectNode select;
    private final int maxRowCount;
    private final int firstRowOrdinal;
    private final List<ParseTreeNode> returnList;

    /**
     * Creates a DrillThroughNode.
     *
     * @param region Region of source code from which this node was created
     * @param select Select statement
     * @param maxRowCount Maximum number of rows to return, or -1
     * @param firstRowOrdinal Ordinal of first row to return, or -1
     * @param returnList List of columns to return
     */
    public DrillThroughNode(
        ParseRegion region,
        SelectNode select,
        int maxRowCount,
        int firstRowOrdinal,
        List<ParseTreeNode> returnList)
    {
        this.region = region;
        this.select = select;
        this.maxRowCount = maxRowCount;
        this.firstRowOrdinal = firstRowOrdinal;
        this.returnList = returnList;
    }

    public ParseRegion getRegion() {
        return region;
    }

    public <T> T accept(ParseTreeVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Type getType() {
        // not an expression, so has no type
        return null;
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        ParseTreeWriter pw = new ParseTreeWriter(sw);
        unparse(pw);
        return sw.toString();
    }

    public void unparse(ParseTreeWriter writer) {
        final PrintWriter pw = writer.getPrintWriter();
        pw.print("DRILLTHROUGH");
        if (maxRowCount >= 0) {
            pw.print(" MAXROWS ");
            pw.print(maxRowCount);
        }
        if (firstRowOrdinal >= 0) {
            pw.print(" FIRSTROWSET ");
            pw.print(firstRowOrdinal);
        }
        pw.print(" ");
        select.unparse(writer);
        if (returnList != null) {
            MdxUtil.unparseList(writer, returnList, " RETURN ", ", ", "");
        }
    }

    public DrillThroughNode deepCopy() {
        return new DrillThroughNode(
            region,
            select.deepCopy(),
            maxRowCount,
            firstRowOrdinal,
            MdxUtil.deepCopyList(returnList));
    }
}

// End DrillThroughNode.java
