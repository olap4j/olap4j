/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2009-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

import org.olap4j.*;
import org.olap4j.metadata.Member;
import org.olap4j.impl.CoordinateIterator;
import org.olap4j.impl.Olap4jUtil;

import java.io.PrintWriter;
import java.util.*;

/**
 * Formatter that can convert a {@link CellSet} into a two-dimensional text
 * layout.
 *
 * <p>With non-compact layout:
 *
 * <pre>
 *                    | 1997                                                |
 *                    | Q1                       | Q2                       |
 *                    |                          | 4                        |
 *                    | Unit Sales | Store Sales | Unit Sales | Store Sales |
 * ----+----+---------+------------+-------------+------------+-------------+
 * USA | CA | Modesto |         12 |        34.5 |         13 |       35.60 |
 *     | WA | Seattle |         12 |        34.5 |         13 |       35.60 |
 *     | CA | Fresno  |         12 |        34.5 |         13 |       35.60 |
 * </pre>
 *
 * <p>With compact layout:
 * <pre>
 *
 *                1997
 *                Q1                     Q2
 *                                       4
 *                Unit Sales Store Sales Unit Sales Store Sales
 * === == ======= ========== =========== ========== ===========
 * USA CA Modesto         12        34.5         13       35.60
 *     WA Seattle         12        34.5         13       35.60
 *     CA Fresno          12        34.5         13       35.60
 * </pre>
 *
 * <p><b>This class is experimental. It is not part of the olap4j
 * specification and is subject to change without notice.</b></p>
 *
 * @author jhyde
 * @version $Id$
 * @since Apr 15, 2009
*/
public class RectangularCellSetFormatter implements CellSetFormatter {
    private final boolean compact;

    /**
     * Creates a RectangularCellSetFormatter.
     *
     * @param compact Whether to generate compact output
     */
    public RectangularCellSetFormatter(boolean compact) {
        this.compact = compact;
    }

    public void format(CellSet cellSet, PrintWriter pw) {
        // Compute how many rows are required to display the columns axis.
        // In the example, this is 4 (1997, Q1, space, Unit Sales)
        final CellSetAxis columnsAxis;
        if (cellSet.getAxes().size() > 0) {
            columnsAxis = cellSet.getAxes().get(0);
        } else {
            columnsAxis = null;
        }
        AxisInfo columnsAxisInfo = computeAxisInfo(columnsAxis);

        // Compute how many columns are required to display the rows axis.
        // In the example, this is 3 (the width of USA, CA, Los Angeles)
        final CellSetAxis rowsAxis;
        if (cellSet.getAxes().size() > 1) {
            rowsAxis = cellSet.getAxes().get(1);
        } else {
            rowsAxis = null;
        }
        AxisInfo rowsAxisInfo = computeAxisInfo(rowsAxis);

        if (cellSet.getAxes().size() > 2) {
            int[] dimensions = new int[cellSet.getAxes().size() - 2];
            for (int i = 2; i < cellSet.getAxes().size(); i++) {
                CellSetAxis cellSetAxis = cellSet.getAxes().get(i);
                dimensions[i - 2] = cellSetAxis.getPositions().size();
            }
            for (int[] pageCoords : CoordinateIterator.iterate(dimensions)) {
                formatPage(
                    cellSet,
                    pw,
                    pageCoords,
                    columnsAxis,
                    columnsAxisInfo,
                    rowsAxis,
                    rowsAxisInfo);
            }
        } else {
            formatPage(
                cellSet,
                pw,
                new int[] {},
                columnsAxis,
                columnsAxisInfo,
                rowsAxis,
                rowsAxisInfo);
        }
    }

    /**
     * Formats a two-dimensional page.
     *
     * @param cellSet Cell set
     * @param pw Print writer
     * @param pageCoords Coordinates of page [page, chapter, section, ...]
     * @param columnsAxis Columns axis
     * @param columnsAxisInfo Description of columns axis
     * @param rowsAxis Rows axis
     * @param rowsAxisInfo Description of rows axis
     */
    private void formatPage(
        CellSet cellSet,
        PrintWriter pw,
        int[] pageCoords,
        CellSetAxis columnsAxis,
        AxisInfo columnsAxisInfo,
        CellSetAxis rowsAxis,
        AxisInfo rowsAxisInfo)
    {
        if (pageCoords.length > 0) {
            pw.println();
            for (int i = pageCoords.length - 1; i >= 0; --i) {
                int pageCoord = pageCoords[i];
                final CellSetAxis axis = cellSet.getAxes().get(2 + i);
                pw.print(axis.getAxisOrdinal() + ": ");
                final Position position =
                    axis.getPositions().get(pageCoord);
                int k = -1;
                for (Member member : position.getMembers()) {
                    if (++k > 0) {
                        pw.print(", ");
                    }
                    pw.print(member.getUniqueName());
                }
                pw.println();
            }
        }
        // Figure out the dimensions of the blank rectangle in the top left
        // corner.
        final int yOffset = columnsAxisInfo.getWidth();
        final int xOffsset = rowsAxisInfo.getWidth();

        // Populate a string matrix
        Matrix matrix =
            new Matrix(
                xOffsset
                + (columnsAxis == null
                    ? 1
                    : columnsAxis.getPositions().size()),
                yOffset
                + (rowsAxis == null
                    ? 1
                    : rowsAxis.getPositions().size()));

        // Populate corner
        for (int x = 0; x < xOffsset; x++) {
            for (int y = 0; y < yOffset; y++) {
                matrix.set(x, y, "", false, x > 0);
            }
        }

        // Populate matrix with cells representing axes
        //noinspection SuspiciousNameCombination
        populateAxis(
            matrix, columnsAxis, columnsAxisInfo, true, xOffsset);
        populateAxis(
            matrix, rowsAxis, rowsAxisInfo, false, yOffset);

        // Populate cell values
        for (Cell cell : cellIter(pageCoords, cellSet)) {
            final List<Integer> coordList = cell.getCoordinateList();
            int x = xOffsset;
            if (coordList.size() > 0) {
                x += coordList.get(0);
            }
            int y = yOffset;
            if (coordList.size() > 1) {
                y += coordList.get(1);
            }
            matrix.set(
                x, y, cell.getFormattedValue(), true, false);
        }

        int[] columnWidths = new int[matrix.width];
        int widestWidth = 0;
        for (int x = 0; x < matrix.width; x++) {
            int columnWidth = 0;
            for (int y = 0; y < matrix.height; y++) {
                MatrixCell cell = matrix.get(x, y);
                if (cell != null) {
                    columnWidth =
                        Math.max(columnWidth, cell.value.length());
                }
            }
            columnWidths[x] = columnWidth;
            widestWidth = Math.max(columnWidth, widestWidth);
        }

        // Create a large array of spaces, for efficient printing.
        char[] spaces = new char[widestWidth + 1];
        Arrays.fill(spaces, ' ');
        char[] equals = new char[widestWidth + 1];
        Arrays.fill(equals, '=');
        char[] dashes = new char[widestWidth + 3];
        Arrays.fill(dashes, '-');

        if (compact) {
            for (int y = 0; y < matrix.height; y++) {
                for (int x = 0; x < matrix.width; x++) {
                    if (x > 0) {
                        pw.print(' ');
                    }
                    final MatrixCell cell = matrix.get(x, y);
                    final int len;
                    if (cell != null) {
                        if (cell.sameAsPrev) {
                            len = 0;
                        } else {
                            if (cell.right) {
                                int padding =
                                    columnWidths[x] - cell.value.length();
                                pw.write(spaces, 0, padding);
                                pw.print(cell.value);
                                continue;
                            }
                            pw.print(cell.value);
                            len = cell.value.length();
                        }
                    } else {
                        len = 0;
                    }
                    if (x == matrix.width - 1) {
                        // at last column; don't bother to print padding
                        break;
                    }
                    int padding = columnWidths[x] - len;
                    pw.write(spaces, 0, padding);
                }
                pw.println();
                if (y == yOffset - 1) {
                    for (int x = 0; x < matrix.width; x++) {
                        if (x > 0) {
                            pw.write(' ');
                        }
                        pw.write(equals, 0, columnWidths[x]);
                    }
                    pw.println();
                }
            }
        } else {
            for (int y = 0; y < matrix.height; y++) {
                for (int x = 0; x < matrix.width; x++) {
                    final MatrixCell cell = matrix.get(x, y);
                    final int len;
                    if (cell != null) {
                        if (cell.sameAsPrev) {
                            pw.print("  ");
                            len = 0;
                        } else {
                            pw.print("| ");
                            if (cell.right) {
                                int padding =
                                    columnWidths[x] - cell.value.length();
                                pw.write(spaces, 0, padding);
                                pw.print(cell.value);
                                pw.print(' ');
                                continue;
                            }
                            pw.print(cell.value);
                            len = cell.value.length();
                        }
                    } else {
                        pw.print("| ");
                        len = 0;
                    }
                    int padding = columnWidths[x] - len;
                    ++padding;
                    pw.write(spaces, 0, padding);
                }
                pw.println('|');
                if (y == yOffset - 1) {
                    for (int x = 0; x < matrix.width; x++) {
                        pw.write('+');
                        pw.write(dashes, 0, columnWidths[x] + 2);
                    }
                    pw.println('+');
                }
            }
        }
    }

    /**
     * Populates cells in the matrix corresponding to a particular axis.
     *
     * @param matrix Matrix to populate
     * @param axis Axis
     * @param axisInfo Description of axis
     * @param isColumns True if columns, false if rows
     * @param offset Ordinal of first cell to populate in matrix
     */
    private void populateAxis(
        Matrix matrix,
        CellSetAxis axis,
        AxisInfo axisInfo,
        boolean isColumns,
        int offset)
    {
        if (axis == null) {
            return;
        }
        Member[] prevMembers = new Member[axisInfo.getWidth()];
        Member[] members = new Member[axisInfo.getWidth()];
        for (int i = 0; i < axis.getPositions().size(); i++) {
            final int x = offset + i;
            Position position = axis.getPositions().get(i);
            int yOffset = 0;
            final List<Member> memberList = position.getMembers();
            for (int j = 0; j < memberList.size(); j++) {
                Member member = memberList.get(j);
                final AxisOrdinalInfo ordinalInfo =
                    axisInfo.ordinalInfos.get(j);
                while (member != null) {
                    if (member.getDepth() < ordinalInfo.minDepth) {
                        break;
                    }
                    final int y =
                        yOffset
                        + member.getDepth()
                        - ordinalInfo.minDepth;
                    members[y] = member;
                    member = member.getParentMember();
                }
                yOffset += ordinalInfo.getWidth();
            }
            boolean same = true;
            for (int y = 0; y < members.length; y++) {
                Member member = members[y];
                same =
                    same
                    && i > 0
                    && Olap4jUtil.equal(prevMembers[y], member);
                String value =
                    member == null
                        ? ""
                        : member.getCaption(null);
                if (isColumns) {
                    matrix.set(x, y, value, false, same);
                } else {
                    if (same) {
                        value = "";
                    }
                    //noinspection SuspiciousNameCombination
                    matrix.set(y, x, value, false, false);
                }
                prevMembers[y] = member;
                members[y] = null;
            }
        }
    }

    /**
     * Computes a description of an axis.
     *
     * @param axis Axis
     * @return Description of axis
     */
    private AxisInfo computeAxisInfo(CellSetAxis axis)
    {
        if (axis == null) {
            return new AxisInfo(0);
        }
        final AxisInfo axisInfo =
            new AxisInfo(axis.getAxisMetaData().getHierarchies().size());
        int p = -1;
        for (Position position : axis.getPositions()) {
            ++p;
            int k = -1;
            for (Member member : position.getMembers()) {
                ++k;
                final AxisOrdinalInfo axisOrdinalInfo =
                    axisInfo.ordinalInfos.get(k);
                final int topDepth =
                    member.isAll()
                        ? member.getDepth()
                        : member.getHierarchy().hasAll()
                            ? 1
                            : 0;
                if (axisOrdinalInfo.minDepth > topDepth
                    || p == 0)
                {
                    axisOrdinalInfo.minDepth = topDepth;
                }
                axisOrdinalInfo.maxDepth =
                    Math.max(
                        axisOrdinalInfo.maxDepth,
                        member.getDepth());
            }
        }
        return axisInfo;
    }

    /**
     * Returns an iterator over cells in a result.
     */
    private static Iterable<Cell> cellIter(
        final int[] pageCoords,
        final CellSet cellSet)
    {
        return new Iterable<Cell>() {
            public Iterator<Cell> iterator() {
                int[] axisDimensions =
                    new int[cellSet.getAxes().size() - pageCoords.length];
                assert pageCoords.length <= axisDimensions.length;
                for (int i = 0; i < axisDimensions.length; i++) {
                    CellSetAxis axis = cellSet.getAxes().get(i);
                    axisDimensions[i] = axis.getPositions().size();
                }
                final CoordinateIterator coordIter =
                    new CoordinateIterator(axisDimensions, true);
                return new Iterator<Cell>() {
                    public boolean hasNext() {
                        return coordIter.hasNext();
                    }

                    public Cell next() {
                        final int[] ints = coordIter.next();
                        final AbstractList<Integer> intList =
                            new AbstractList<Integer>() {
                                public Integer get(int index) {
                                    return index < ints.length
                                        ? ints[index]
                                        : pageCoords[index - ints.length];
                                }

                                public int size() {
                                    return pageCoords.length + ints.length;
                                }
                            };
                        return cellSet.getCell(intList);
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Description of a particular hierarchy mapped to an axis.
     */
    private static class AxisOrdinalInfo {
        int minDepth = 1;
        int maxDepth = 0;

        /**
         * Returns the number of matrix columns required to display this
         * hierarchy.
         */
        public int getWidth() {
            return maxDepth - minDepth + 1;
        }
    }

    /**
     * Description of an axis.
     */
    private static class AxisInfo {
        final List<AxisOrdinalInfo> ordinalInfos;

        /**
         * Creates an AxisInfo.
         *
         * @param ordinalCount Number of hierarchies on this axis
         */
        AxisInfo(int ordinalCount) {
            ordinalInfos = new ArrayList<AxisOrdinalInfo>(ordinalCount);
            for (int i = 0; i < ordinalCount; i++) {
                ordinalInfos.add(new AxisOrdinalInfo());
            }
        }

        /**
         * Returns the number of matrix columns required by this axis. The
         * sum of the width of the hierarchies on this axis.
         *
         * @return Width of axis
         */
        public int getWidth() {
            int width = 0;
            for (AxisOrdinalInfo info : ordinalInfos) {
                width += info.getWidth();
            }
            return width;
        }
    }

    /**
     * Two-dimensional collection of string values.
     */
    private class Matrix {
        private final Map<List<Integer>, MatrixCell> map =
            new HashMap<List<Integer>, MatrixCell>();
        private final int width;
        private final int height;

        /**
         * Creats a Matrix.
         *
         * @param width Width of matrix
         * @param height Height of matrix
         */
        public Matrix(int width, int height) {
            this.width = width;
            this.height = height;
        }

        /**
         * Sets the value at a particular coordinate
         *
         * @param x X coordinate
         * @param y Y coordinate
         * @param value Value
         */
        void set(int x, int y, String value) {
            set(x, y, value, false, false);
        }

        /**
         * Sets the value at a particular coordinate
         *
         * @param x X coordinate
         * @param y Y coordinate
         * @param value Value
         * @param right Whether value is right-justified
         * @param sameAsPrev Whether value is the same as the previous value.
         * If true, some formats separators between cells
         */
        void set(
            int x,
            int y,
            String value,
            boolean right,
            boolean sameAsPrev)
        {
            map.put(
                Arrays.asList(x, y),
                new MatrixCell(value, right, sameAsPrev));
            assert x >= 0 && x < width : x;
            assert y >= 0 && y < height : y;
        }

        /**
         * Returns the cell at a particular coordinate.
         *
         * @param x X coordinate
         * @param y Y coordinate
         * @return Cell
         */
        public MatrixCell get(int x, int y) {
            return map.get(Arrays.asList(x, y));
        }
    }

    /**
     * Contents of a cell in a matrix.
     */
    private static class MatrixCell {
        final String value;
        final boolean right;
        final boolean sameAsPrev;

        /**
         * Creates a matrix cell.
         *
         * @param value Value
         * @param right Whether value is right-justified
         * @param sameAsPrev Whether value is the same as the previous value.
         * If true, some formats separators between cells
         */
        MatrixCell(
            String value,
            boolean right,
            boolean sameAsPrev)
        {
            this.value = value;
            this.right = right;
            this.sameAsPrev = sameAsPrev;
        }
    }
}

// End RectangularCellSetFormatter.java
