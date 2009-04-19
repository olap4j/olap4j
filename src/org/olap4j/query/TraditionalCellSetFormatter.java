/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2009-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.query;

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
 * @version $Id$
 * @since Apr 15, 2009
 */
public class TraditionalCellSetFormatter implements CellSetFormatter {
    public void format(
        CellSet cellSet,
        PrintWriter pw)
    {
        print(cellSet, pw);
    }

    private static void print(CellSet cellSet, PrintWriter pw) {
        pw.println("Axis #0:");
        printAxis(pw, cellSet.getFilterAxis());
        final List<CellSetAxis> axes = cellSet.getAxes();
        int i = 0;
        for (CellSetAxis axis : axes) {
            pw.println("Axis #" + (++i) + ":");
            printAxis(pw, axis);
        }
        // Usually there are 3 axes: {filter, columns, rows}. Position is a
        // {column, row} pair. We call printRows with axis=2. When it
        // recurses to axis=-1, it prints.
        List<Integer> pos = new ArrayList<Integer>(axes.size());
        for (CellSetAxis axis : axes) {
            pos.add(-1);
        }
        printRows(cellSet, pw, axes.size() - 1, pos);
    }

    private static void printRows(
        CellSet cellSet, PrintWriter pw, int axis, List<Integer> pos)
    {
        CellSetAxis _axis = axis < 0 ?
            cellSet.getFilterAxis() :
            cellSet.getAxes().get(axis);
        List<Position> positions = _axis.getPositions();
        int i = 0;
        for (Position position : positions) {
            if (axis < 0) {
                if (i > 0) {
                    pw.print(", ");
                }
                printCell(cellSet, pw, pos);
            } else {
                pos.set(axis, i);
                if (axis == 0) {
                    int row = axis + 1 < pos.size() ? pos.get(axis + 1) : 0;
                    pw.print("Row #" + row + ": ");
                }
                printRows(cellSet, pw, axis - 1, pos);
                if (axis == 0) {
                    pw.println();
                }
            }
            i++;
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
