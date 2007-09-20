/*
// $Id: ParseTreeNode.java 20 2007-06-10 23:09:28Z jhyde $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx;

/**
 * Region of parser source code.
 *
 * <p>The main purpose of a ParseRegion is to give detailed locations in
 * error messages and warnings from the parsing and validation process.
 *
 * <p>A region has a start and end line number and column number. A region is
 * a point if the start and end positions are the same.
 *
 * <p>The line and column number are one-based, because that is what end-users
 * understand.
 *
 * <p>A region's end-points are inclusive. For example, in the code
 *
 * <blockquote><pre>SELECT FROM [Sales]</pre></blockquote>
 *
 * the <code>SELECT</code> token has region [1:1, 1:6].
 *
 * <p>Regions are immutable.
 *
 * @version $Id: $
 * @author jhyde
 */
public class ParseRegion {
    private final int startLine;
    private final int startColumn;
    private final int endLine;
    private final int endColumn;

    private static final String NL = System.getProperty("line.separator");

    /**
     * Creates a ParseRegion.
     *
     * <p>All lines and columns are 1-based and inclusive. For example, the
     * token "select" in "select from [Sales]" has a region [1:1, 1:6].
     *
     * @param startLine Line of the beginning of the region
     * @param startColumn Column of the beginning of the region
     * @param endLine Line of the end of the region
     * @param endColumn Column of the end of the region
     */
    public ParseRegion(
        int startLine,
        int startColumn,
        int endLine,
        int endColumn)
    {
        assert endLine >= startLine;
        assert endLine > startLine || endColumn >= startColumn;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
    }

    /**
     * Creates a ParseRegion.
     *
     * All lines and columns are 1-based.
     *
     * @param line Line of the beginning and end of the region
     * @param column Column of the beginning and end of the region
     */
    public ParseRegion(
        int line,
        int column)
    {
        this(line, column, line, column);
    }

    /**
     * Return starting line number (1-based).
     *
     * @return 1-based starting line number
     */
    public int getStartLine() {
        return startLine;
    }

    /**
     * Return starting column number (1-based).
     *
     * @return 1-based starting column number
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * Return ending line number (1-based).
     *
     * @return 1-based ending line number
     */
    public int getEndLine() {
        return endLine;
    }

    /**
     * Return ending column number (1-based).
     *
     * @return 1-based starting endings column number
     */
    public int getEndColumn() {
        return endColumn;
    }

    /**
     * Returns a string representation of this ParseRegion.
     *
     * <p>Regions are of the form
     * <code>[startLine:startColumn, endLine:endColumn]</code>, or
     * <code>[startLine:startColumn]</code> for point regions.
     *
     * @return string representation of this ParseRegion
     */
    public String toString() {
        return "[" + startLine + ":" + startColumn +
            ((isPoint())
                ? ""
                : ", " + endLine + ":" + endColumn) +
            "]";
    }

    /**
     * Returns whether this region has the same start and end point.
     *
     * @return whether this region has the same start and end point
     */
    public boolean isPoint() {
        return endLine == startLine && endColumn == startColumn;
    }

    public int hashCode() {
        return startLine ^
            (startColumn << 2) ^
            (endLine << 4) ^
            (endColumn << 8);
    }

    public boolean equals(Object obj) {
        if (obj instanceof ParseRegion) {
            final ParseRegion that = (ParseRegion) obj;
            return this.startLine == that.startLine &&
                this.startColumn == that.startColumn &&
                this.endLine == that.endLine &&
                this.endColumn == that.endColumn;
        } else {
            return false;
        }
    }

    /**
     * Combines this region with a list of parse tree nodes to create a
     * region which spans from the first point in the first to the last point
     * in the other.
     */
    public ParseRegion plusAll(Iterable<ParseRegion> regions)
    {
        return sum(
            regions,
            getStartLine(),
            getStartColumn(),
            getEndLine(),
            getEndColumn());
    }

    /**
     * Combines the parser positions of a list of nodes to create a position
     * which spans from the beginning of the first to the end of the last.
     */
    public static ParseRegion sum(
        Iterable<ParseRegion> nodes)
    {
        return sum(nodes, Integer.MAX_VALUE, Integer.MAX_VALUE, -1, -1);
    }

    private static ParseRegion sum(
        Iterable<ParseRegion> regions,
        int startLine,
        int startColumn,
        int endLine,
        int endColumn)
    {
        int testLine;
        int testColumn;
        for (ParseRegion region : regions) {
            if (region == null) {
                continue;
            }
            testLine = region.getStartLine();
            testColumn = region.getStartColumn();
            if ((testLine < startLine)
                || ((testLine == startLine) && (testColumn < startColumn)))
            {
                startLine = testLine;
                startColumn = testColumn;
            }

            testLine = region.getEndLine();
            testColumn = region.getEndColumn();
            if ((testLine > endLine)
                || ((testLine == endLine) && (testColumn > endColumn)))
            {
                endLine = testLine;
                endColumn = testColumn;
            }
        }
        return new ParseRegion(startLine, startColumn, endLine, endColumn);
    }

    /**
     * Looks for one or two carets in a SQL string, and if present, converts
     * them into a parser position.
     *
     * <p>Examples:
     *
     * <ul>
     * <li>findPos("xxx^yyy") yields {"xxxyyy", position 3, line 1 column 4}
     * <li>findPos("xxxyyy") yields {"xxxyyy", null}
     * <li>findPos("xxx^yy^y") yields {"xxxyyy", position 3, line 4 column 4
     * through line 1 column 6}
     * </ul>
     */
    public static RegionAndSource findPos(String sql)
    {
        int firstCaret = sql.indexOf('^');
        if (firstCaret < 0) {
            return new RegionAndSource(sql, null);
        }
        int secondCaret = sql.indexOf('^', firstCaret + 1);
        if (secondCaret < 0) {
            String sqlSansCaret =
                sql.substring(0, firstCaret)
                + sql.substring(firstCaret + 1);
            int [] start = indexToLineCol(sql, firstCaret);
            return new RegionAndSource(
                sqlSansCaret,
                new ParseRegion(start[0], start[1]));
        } else {
            String sqlSansCaret =
                sql.substring(0, firstCaret)
                + sql.substring(firstCaret + 1, secondCaret)
                + sql.substring(secondCaret + 1);
            int [] start = indexToLineCol(sql, firstCaret);

            // subtract 1 because first caret pushed the string out
            --secondCaret;

            // subtract 1 because the col position needs to be inclusive
            --secondCaret;
            int [] end = indexToLineCol(sql, secondCaret);
            return new RegionAndSource(
                sqlSansCaret,
                new ParseRegion(start[0], start[1], end[0], end[1]));
        }
    }

    /**
     * Returns the (1-based) line and column corresponding to a particular
     * (0-based) offset in a string.
     *
     * <p>Converse of {@link #lineColToIndex(String, int, int)}.
     */
    private static int [] indexToLineCol(String sql, int i) {
        int line = 0;
        int j = 0;
        while (true) {
            String s;
            int rn = sql.indexOf("\r\n", j);
            int r = sql.indexOf("\r", j);
            int n = sql.indexOf("\n", j);
            int prevj = j;
            if ((r < 0) && (n < 0)) {
                assert rn < 0;
                s = null;
                j = -1;
            } else if ((rn >= 0) && (rn < n) && (rn <= r)) {
                s = "\r\n";
                j = rn;
            } else if ((r >= 0) && (r < n)) {
                s = "\r";
                j = r;
            } else {
                s = "\n";
                j = n;
            }
            if ((j < 0) || (j > i)) {
                return new int[] { line + 1, i - prevj + 1 };
            }
            j += s.length();
            ++line;
        }
    }

    /**
     * Finds the position (0-based) in a string which corresponds to a given
     * line and column (1-based).
     *
     * <p>Converse of {@link #indexToLineCol(String, int)}.
     */
    public static int lineColToIndex(String sql, int line, int column)
    {
        --line;
        --column;
        int i = 0;
        while (line-- > 0) {
            i = sql.indexOf(NL, i)
                + NL.length();
        }
        return i + column;
    }

    /**
     * Generates a string of the source code annotated with caret symbols ("^")
     * at the beginning and end of the region.
     *
     * <p>For example, for the region <code>(1, 9, 1, 12)</code> and source
     * <code>"values (foo)"</code>,
     * yields the string <code>"values (^foo^)"</code>.
     */
    public String annotate(String source) {
        return addCarets(source, startLine, startColumn, endLine, endColumn);
    }

    /**
     * Converts a string to a string with one or two carets in it. For example,
     * <code>addCarets("values (foo)", 1, 9, 1, 11)</code> yields "values
     * (^foo^)".
     */
    private static String addCarets(
        String sql,
        int line,
        int col,
        int endLine,
        int endCol)
    {
        String sqlWithCarets;
        int cut = lineColToIndex(sql, line, col);
        sqlWithCarets = sql.substring(0, cut) + "^"
            + sql.substring(cut);
        if ((col != endCol) || (line != endLine)) {
            cut = lineColToIndex(sqlWithCarets, endLine, endCol + 1);
            ++cut; // for caret
            if (cut < sqlWithCarets.length()) {
                sqlWithCarets =
                    sqlWithCarets.substring(0, cut)
                    + "^" + sqlWithCarets.substring(cut);
            } else {
                sqlWithCarets += "^";
            }
        }
        return sqlWithCarets;
    }

    /**
     * Combination of a region within an MDX statement with the source text
     * of the whole MDX statement.
     *
     * <p>Useful for reporting errors. For example, the error in the statement
     *
     * <blockquote>
     * <pre>
     * SELECT {<b><i>[Measures].[Units In Stock]</i></b>} ON COLUMNS
     * FROM [Sales]
     * </pre>
     * </blockquote>
     *
     * has source
     * "SELECT {[Measures].[Units In Stock]} ON COLUMNS\nFROM [Sales]" and
     * region [1:9, 1:34].
     */
    public static class RegionAndSource {
        public final String source;
        public final ParseRegion region;

        /**
         * Creates a RegionAndSource.
         *
         * @param source Source MDX code
         * @param region Coordinates of region within MDX code
         */
        public RegionAndSource(String source, ParseRegion region) {
            this.source = source;
            this.region = region;
        }
    }
}

// End ParseRegion.java
