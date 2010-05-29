/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2009-2010 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.layout;

import org.olap4j.*;
import org.olap4j.metadata.Member;

import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

/**
 * Formatter that can convert a {@link CellSet} into Mondrian's traditional
 * layout.
 *
 * <p><b>This class is experimental. It is not part of the olap4j
 * specification and is subject to change without notice.</b></p>
 *
 * @author jhyde
 * @version $Id:$
 * @since Apr 15, 2009
 */
public class TraditionalCellSetFormatter implements CellSetFormatter {
    public void format(
        CellSet cellSet,
        PrintWriter pw)
    {
        print(cellSet, pw);
    }

    /**
     * Prints a cell set.
     *
     * @param cellSet Cell set
     * @param pw Writer
     */
    private static void print(CellSet cellSet, PrintWriter pw) {
        pw.println("Axis #0:");
        printAxis(pw, cellSet.getFilterAxis());
        final List<CellSetAxis> axes = cellSet.getAxes();
        final int axisCount = axes.size();
        for (int i = 0; i < axisCount; i++) {
            CellSetAxis axis = axes.get(i);
            pw.println("Axis #" + (i + 1) + ":");
            printAxis(pw, axis);
        }
        // Usually there are 3 axes: {filter, columns, rows}. Position is a
        // {column, row} pair. We call printRows with axis=2. When it
        // recurses to axis=-1, it prints.
        List<Integer> pos = new ArrayList<Integer>(axisCount);
        for (int i = 0; i < axisCount; i++) {
            pos.add(-1);
        }
        printRows(cellSet, pw, axisCount - 1, pos);
    }

    /**
     * Prints the rows of cell set.
     *
     * @param cellSet Cell set
     * @param pw Writer
     * @param axis Axis ordinal
     * @param pos Partial coordinate
     */
    private static void printRows(
        CellSet cellSet, PrintWriter pw, int axis, List<Integer> pos)
    {
        CellSetAxis _axis = axis < 0
            ? cellSet.getFilterAxis()
            : cellSet.getAxes().get(axis);
        List<Position> positions = _axis.getPositions();
        final int positionCount = positions.size();
        for (int i = 0; i < positionCount; i++) {
            if (axis < 0) {
                if (i > 0) {
                    pw.print(", ");
                }
                printCell(cellSet, pw, pos);
            } else {
                pos.set(axis, i);
                if (axis == 0) {
                    int row =
                        axis + 1 < pos.size()
                            ? pos.get(axis + 1)
                            : 0;
                    pw.print("Row #" + row + ": ");
                }
                printRows(cellSet, pw, axis - 1, pos);
                if (axis == 0) {
                    pw.println();
                }
            }
        }
    }

    /**
     * Prints an axis and its members.
     *
     * @param pw Print writer
     * @param axis Axis
     */
    private static void printAxis(PrintWriter pw, CellSetAxis axis) {
        List<Position> positions = axis.getPositions();
        for (Position position : positions) {
            boolean firstTime = true;
            pw.print("{");
            for (Member member : position.getMembers()) {
                if (! firstTime) {
                    pw.print(", ");
                }
                pw.print(member.getUniqueName());
                firstTime = false;
            }
            pw.println("}");
        }
    }

    /**
     * Prints the formatted value of a Cell at a given position.
     *
     * @param cellSet Cell set
     * @param pw Print writer
     * @param pos Cell coordinates
     */
    private static void printCell(
        CellSet cellSet, PrintWriter pw, List<Integer> pos)
    {
        Cell cell = cellSet.getCell(pos);
        pw.print(cell.getFormattedValue());
    }
}

// End TraditionalCellSetFormatter.java
