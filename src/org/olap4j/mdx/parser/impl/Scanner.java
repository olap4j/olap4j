/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.mdx.parser.impl;

import org.olap4j.mdx.ParseRegion;
import org.olap4j.mdx.parser.MdxParseException;

import java.math.BigDecimal;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import java_cup.runtime.Symbol;

/**
 * Lexical analyzer for MDX.
 *
 * <p>NOTE: This class is not part of the public olap4j API.
 *
 * @version $Id$
 * @author jhyde
 */
class Scanner {

    /** single lookahead character */
    protected int nextChar;
    /** next lookahead character */
    private int lookaheadChars[] = new int[16];
    private int firstLookaheadChar = 0;
    private int lastLookaheadChar = 0;
    private Map<String, Integer> reservedWordIds;
    private int iMaxResword;
    private String[] reservedWords;
    protected boolean debug;

    /** lines[x] is the start of the x'th line */
    private final List<Integer> lines = new ArrayList<Integer>();

    /** number of times advance() has been called */
    private int iChar;

    /** end of previous token */
    private int iPrevChar;

    /** previous symbol returned */
    private int previousSymbol;
    private boolean inFormula;

    /**
     * Comment delimiters. Modify this list to support other comment styles.
     */
    private static final String[][] commentDelim = {
        {"//", null},
        {"--", null},
        {"/*", "*/"}
    };

    /**
     * Whether to allow nested comments.
     */
    private static final boolean allowNestedComments = true;

    /**
     * The {@link java.math.BigDecimal} value 0.
     * Note that BigDecimal.ZERO does not exist until JDK 1.5.
     */
    private static final BigDecimal BigDecimalZero = BigDecimal.valueOf(0);

    /**
     * Creates a Scanner.
     *
     * @param debug Whether to populate debug messages.
     */
    Scanner(boolean debug) {
        this.debug = debug;
    }

    /**
     * Returns the current nested comments state.
     */
    public static boolean getNestedCommentsState() {
        return allowNestedComments;
    }

    /**
     * Returns the list of comment delimiters.
     */
    public static String[][] getCommentDelimiters() {
        return commentDelim;
    }

    /**
     * Advance input by one character, setting {@link #nextChar}.
     */
    private void advance()
        throws java.io.IOException {

        if (firstLookaheadChar == lastLookaheadChar) {
            // We have nothing in the lookahead buffer.
            nextChar = getChar();
        } else {
            // We have called lookahead(); advance to the next character it got.
            nextChar = lookaheadChars[firstLookaheadChar++];
            if (firstLookaheadChar == lastLookaheadChar) {
                firstLookaheadChar = 0;
                lastLookaheadChar = 0;
            }
        }
        if (nextChar == '\012') {
            lines.add(iChar);
        }
        iChar++;
    }

    /** Peek at the character after {@link #nextChar} without advancing. */
    private int lookahead()
        throws java.io.IOException {

        return lookahead(1);
    }

    /**
     * Peeks at the character n after {@link #nextChar} without advancing.
     * lookahead(0) returns the current char (nextChar).
     * lookahead(1) returns the next char (was lookaheadChar, same as lookahead());
     */
    private int lookahead(int n)
        throws java.io.IOException {

        if (n == 0) {
            return nextChar;
        }
        else {
            // if the desired character not in lookahead buffer, read it in
            if (n > lastLookaheadChar - firstLookaheadChar) {
                int len=lastLookaheadChar - firstLookaheadChar;
                int t[];

                // make sure we do not go off the end of the buffer
                if (n + firstLookaheadChar > lookaheadChars.length) {
                    if (n > lookaheadChars.length) {
                        // the array is too small; make it bigger and shift
                        // everything to the beginning.
                        t=new int[n * 2];
                    }
                    else {
                        // the array is big enough, so just shift everything
                        // to the beginning of it.
                        t = lookaheadChars;
                    }

                    System.arraycopy(
                        lookaheadChars, firstLookaheadChar, t, 0, len);
                    lookaheadChars = t;
                    firstLookaheadChar = 0;
                    lastLookaheadChar = len;
                }

                // read ahead enough
                while (n > lastLookaheadChar - firstLookaheadChar) {
                    lookaheadChars[lastLookaheadChar++] = getChar();
                }
            }

            return lookaheadChars[n - 1 + firstLookaheadChar];
        }
    }

    /** Read a character from input, returning -1 if end of input. */
    protected int getChar()
        throws java.io.IOException {

        return System.in.read();
    }

    /** Initialize the scanner */
    public void init()
        throws java.io.IOException {

        initReswords();
        lines.clear();
        iChar = iPrevChar = 0;
        advance();
    }

    private void initResword(int id, String s) {
        reservedWordIds.put(s, id);
        if (id > iMaxResword) {
            iMaxResword = id;
        }
    }

    /**
     * Initializes the table of reserved words.
     */
    private void initReswords() {
        // This list generated by piping the 'terminal' declaration in mdx.cup
        // through:
        //   grep -list // |
        //   sed -e 's/,//' |
        //   awk '{printf "initResword(%20s,%c%s%c);",$1,34,$1,34}'
        reservedWordIds = new HashMap<String, Integer>();
        iMaxResword = 0;
//      initResword(DefaultMdxParserSym.ALL                 ,"ALL");
        initResword(DefaultMdxParserSym.AND                 ,"AND");
        initResword(DefaultMdxParserSym.AS                  ,"AS");
//      initResword(DefaultMdxParserSym.ASC                 ,"ASC");
        initResword(DefaultMdxParserSym.AXIS                ,"AXIS");
//      initResword(DefaultMdxParserSym.BACK_COLOR          ,"BACK_COLOR");
//      initResword(DefaultMdxParserSym.BASC                ,"BASC");
//      initResword(DefaultMdxParserSym.BDESC               ,"BDESC");
        initResword(DefaultMdxParserSym.CAST                ,"CAST"); // mondrian extension
        initResword(DefaultMdxParserSym.CASE                ,"CASE");
        initResword(DefaultMdxParserSym.CELL                ,"CELL");
//      initResword(DefaultMdxParserSym.CELL_ORDINAL        ,"CELL_ORDINAL");
        initResword(DefaultMdxParserSym.CHAPTERS            ,"CHAPTERS");
//      initResword(DefaultMdxParserSym.CHILDREN            ,"CHILDREN");
        initResword(DefaultMdxParserSym.COLUMNS             ,"COLUMNS");
//      initResword(DefaultMdxParserSym.DESC                ,"DESC");
        initResword(DefaultMdxParserSym.DIMENSION           ,"DIMENSION");
        initResword(DefaultMdxParserSym.ELSE                ,"ELSE");
        initResword(DefaultMdxParserSym.EMPTY               ,"EMPTY");
        initResword(DefaultMdxParserSym.END                 ,"END");
//      initResword(DefaultMdxParserSym.FIRSTCHILD          ,"FIRSTCHILD");
//      initResword(DefaultMdxParserSym.FIRSTSIBLING        ,"FIRSTSIBLING");
//      initResword(DefaultMdxParserSym.FONT_FLAGS          ,"FONT_FLAGS");
//      initResword(DefaultMdxParserSym.FONT_NAME           ,"FONT_NAME");
//      initResword(DefaultMdxParserSym.FONT_SIZE           ,"FONT_SIZE");
//      initResword(DefaultMdxParserSym.FORE_COLOR          ,"FORE_COLOR");
//      initResword(DefaultMdxParserSym.FORMATTED_VALUE     ,"FORMATTED_VALUE");
//      initResword(DefaultMdxParserSym.FORMAT_STRING       ,"FORMAT_STRING");
        initResword(DefaultMdxParserSym.FROM                ,"FROM");
        initResword(DefaultMdxParserSym.IS                  ,"IS");
        initResword(DefaultMdxParserSym.IN                  ,"IN");
//      initResword(DefaultMdxParserSym.LAG                 ,"LAG");
//      initResword(DefaultMdxParserSym.LASTCHILD           ,"LASTCHILD");
//      initResword(DefaultMdxParserSym.LASTSIBLING         ,"LASTSIBLING");
//      initResword(DefaultMdxParserSym.LEAD                ,"LEAD");
        initResword(DefaultMdxParserSym.MATCHES             ,"MATCHES");
        initResword(DefaultMdxParserSym.MEMBER              ,"MEMBER");
//      initResword(DefaultMdxParserSym.MEMBERS             ,"MEMBERS");
//      initResword(DefaultMdxParserSym.NEXTMEMBER          ,"NEXTMEMBER");
        initResword(DefaultMdxParserSym.NON                 ,"NON");
        initResword(DefaultMdxParserSym.NOT                 ,"NOT");
        initResword(DefaultMdxParserSym.NULL                ,"NULL");
        initResword(DefaultMdxParserSym.ON                  ,"ON");
        initResword(DefaultMdxParserSym.OR                  ,"OR");
        initResword(DefaultMdxParserSym.PAGES               ,"PAGES");
//      initResword(DefaultMdxParserSym.PARENT              ,"PARENT");
//      initResword(DefaultMdxParserSym.PREVMEMBER          ,"PREVMEMBER");
        initResword(DefaultMdxParserSym.PROPERTIES          ,"PROPERTIES");
//      initResword(DefaultMdxParserSym.RECURSIVE           ,"RECURSIVE");
        initResword(DefaultMdxParserSym.ROWS                ,"ROWS");
        initResword(DefaultMdxParserSym.SECTIONS            ,"SECTIONS");
        initResword(DefaultMdxParserSym.SELECT              ,"SELECT");
        initResword(DefaultMdxParserSym.SET                 ,"SET");
//      initResword(DefaultMdxParserSym.SOLVE_ORDER         ,"SOLVE_ORDER");
        initResword(DefaultMdxParserSym.THEN                ,"THEN");
//      initResword(DefaultMdxParserSym.VALUE               ,"VALUE");
        initResword(DefaultMdxParserSym.WHEN                ,"WHEN");
        initResword(DefaultMdxParserSym.WHERE               ,"WHERE");
        initResword(DefaultMdxParserSym.WITH                ,"WITH");
        initResword(DefaultMdxParserSym.XOR                 ,"XOR");

        reservedWords = new String[iMaxResword + 1];
        for (Map.Entry<String, Integer> entry : reservedWordIds.entrySet()) {
            reservedWords[entry.getValue()] = entry.getKey();
        }
    }

    /** return the name of the reserved word whose token code is "i" */
    public String lookupReserved(int i) {
        return reservedWords[i];
    }

    private Symbol makeSymbol(int id, Object o) {
        int iPrevPrevChar = iPrevChar;
        this.iPrevChar = iChar;
        this.previousSymbol = id;
        return new Symbol(id, iPrevPrevChar, iChar, o);
    }

    /**
     * Creates a token representing a numeric literal.
     *
     * @param mantissa The digits of the number
     * @param exponent The base-10 exponent of the number
     * @return number literal token
     */
    private Symbol makeNumber(BigDecimal mantissa, int exponent) {
        double d = mantissa.movePointRight(exponent).doubleValue();
        return makeSymbol(DefaultMdxParserSym.NUMBER, d);
    }

    private Symbol makeId(String s, boolean quoted, boolean ampersand) {
        final int id;
        if (quoted) {
            if (ampersand) {
                id = DefaultMdxParserSym.AMP_QUOTED_ID;
            } else {
                id = DefaultMdxParserSym.QUOTED_ID;
            }
        } else {
            if (ampersand) {
                id = DefaultMdxParserSym.AMP_UNQUOTED_ID;
            } else {
                id = DefaultMdxParserSym.ID;
            }
        }
        return makeSymbol(id, s);
    }

    /**
     * Creates a token representing a reserved word.
     *
     * @param i Token code
     * @return Token
     */
    private Symbol makeRes(int i) {
        return makeSymbol(i, reservedWords[i]);
    }

    /**
     * Creates a token.
     *
     * @param i Token code
     * @param s Text of the token
     * @return Token
     */
    private Symbol makeToken(int i, String s) {
        return makeSymbol(i, s);
    }

    /**
     * Creates a token representing a string literal.
     *
     * @param s String
     * @return String token
     */
    private Symbol makeString(String s) {
        if (inFormula) {
            inFormula = false;
            return makeSymbol(DefaultMdxParserSym.FORMULA_STRING, s);
        } else {
            return makeSymbol(DefaultMdxParserSym.STRING, s);
        }
    }

    /**
     * Discards all characters until the end of the current line.
     */
    private void skipToEOL() throws IOException {
        while (nextChar != -1 && nextChar != '\012') {
            advance();
        }
    }

    /**
     * Eats a delimited comment.
     * The type of delimiters are kept in commentDelim.  The current
     * comment type is indicated by commentType.
     * end of file terminates a comment without error.
     */
    private void skipComment(
            final String startDelim,
            final String endDelim) throws IOException {

        int depth = 1;

        // skip the starting delimiter
        for (int x = 0; x < startDelim.length(); x++) {
            advance();
        }

        for (;;) {
            if (nextChar == -1) {
                return;
            }
            else if (checkForSymbol(endDelim)) {
                // eat the end delimiter
                for (int x = 0; x < endDelim.length(); x++) {
                    advance();
                }
                if (--depth == 0) {
                    return;
                }
            }
            else if (allowNestedComments && checkForSymbol(startDelim)) {
               // eat the nested start delimiter
                for (int x = 0; x < startDelim.length(); x++) {
                    advance();
                }
                depth++;
            }
            else {
                advance();
            }
        }
    }

    /**
     * If the next tokens are comments, skip over them.
     */
    private void searchForComments() throws IOException {

        // eat all following comments
        boolean foundComment;
        do {
            foundComment = false;
            for (String[] aCommentDelim : commentDelim) {
                if (checkForSymbol(aCommentDelim[0])) {
                    if (aCommentDelim[1] == null) {
                        foundComment = true;
                        skipToEOL();
                    } else {
                        foundComment = true;
                        skipComment(aCommentDelim[0], aCommentDelim[1]);
                    }
                }
            }
        } while (foundComment);
    }

    /**
     * Checks if the next symbol is the supplied string
     */
    private boolean checkForSymbol(final String symb) throws IOException {
            for (int x = 0; x < symb.length(); x++) {
                if (symb.charAt(x) != lookahead(x)) {
                    return false;
                }
            }
            return true;
    }

    /**
     * Recognizes and returns the next complete token.
     */
    public Symbol next_token() throws IOException {

        StringBuilder id;
        boolean ampersandId = false;
        for (;;) {
            searchForComments();
            mainSwitch:
            switch (nextChar) {
            case '.':
                switch (lookahead()) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                    // We're looking at the '.' on the start of a number,
                    // e.g. .1; fall through to parse a number.
                    break;
                default:
                    advance();
                    return makeToken(DefaultMdxParserSym.DOT, ".");
                }
                // fall through

            case '0': case '1': case '2': case '3': case '4':
            case '5': case '6': case '7': case '8': case '9':

                // Parse a number.  Valid examples include 1, 1.2, 0.1, .1,
                // 1e2, 1E2, 1e-2, 1e+2.  Invalid examples include e2, 1.2.3,
                // 1e2e3, 1e2.3.
                //
                // Signs preceding numbers (e.g. -1, +1E-5) are valid, but are
                // handled by the parser.
                //
                BigDecimal n = BigDecimalZero;
                int digitCount = 0, exponent = 0;
                boolean positive = true;
                BigDecimal mantissa = BigDecimalZero;
                State state = State.leftOfPoint;

                for (;;) {
                    switch (nextChar) {
                    case '.':
                        switch (state) {
                        case leftOfPoint:
                            state = State.rightOfPoint;
                            mantissa = n;
                            n = BigDecimalZero;
                            digitCount = 0;
                            positive = true;
                            advance();
                            break;
                            // Error: we are seeing a point in the exponent
                            // (e.g. 1E2.3 or 1.2E3.4) or a second point in the
                            // mantissa (e.g. 1.2.3).  Return what we've got
                            // and let the parser raise the error.
                        case rightOfPoint:
                            mantissa =
                                mantissa.add(
                                    n.movePointRight(-digitCount));
                            return makeNumber(mantissa, exponent);
                        case inExponent:
                            if (!positive) {
                                n = n.negate();
                            }
                            exponent = n.intValue();
                            return makeNumber(mantissa, exponent);
                        }
                        break;

                    case 'E':
                    case 'e':
                        switch (state) {
                        case inExponent:
                            // Error: we are seeing an 'e' in the exponent
                            // (e.g. 1.2e3e4).  Return what we've got and let
                            // the parser raise the error.
                            if (!positive) {
                                n = n.negate();
                            }
                            exponent = n.intValue();
                            return makeNumber(mantissa, exponent);
                        case leftOfPoint:
                            mantissa = n;
                            break;
                        default:
                            mantissa =
                                mantissa.add(
                                    n.movePointRight(-digitCount));
                            break;
                        }

                        digitCount = 0;
                        n = BigDecimalZero;
                        positive = true;
                        advance();
                        state = State.inExponent;
                        break;

                    case'0': case'1': case'2': case'3': case'4':
                    case'5': case'6': case'7': case'8': case'9':
                        n = n.movePointRight(1);
                        n = n.add(BigDecimal.valueOf(nextChar - '0'));
                        digitCount++;
                        advance();
                        break;

                    case '+':
                    case '-':
                        if (state == State.inExponent && digitCount == 0) {
                            // We're looking at the sign after the 'e'.
                            positive = !positive;
                            advance();
                            break;
                        }
                        // fall through - end of number

                    default:
                        // Reached end of number.
                        switch (state) {
                        case leftOfPoint:
                            mantissa = n;
                            break;
                        case rightOfPoint:
                            mantissa =
                                mantissa.add(
                                    n.movePointRight(-digitCount));
                            break;
                        default:
                            if (!positive) {
                                n = n.negate();
                            }
                            exponent = n.intValue();
                            break;
                        }
                        return makeNumber(mantissa, exponent);
                    }
                }

            case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
            case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
            case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
            case 's': case 't': case 'u': case 'v': case 'w': case 'x':
            case 'y': case 'z':
            case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
            case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
            case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
            case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
            case 'Y': case 'Z':
                /* parse an identifier */
                id = new StringBuilder();
                for (;;) {
                    id.append((char) nextChar);
                    advance();
                    switch (nextChar) {
                    case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                    case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
                    case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
                    case 's': case 't': case 'u': case 'v': case 'w': case 'x':
                    case 'y': case 'z':
                    case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                    case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
                    case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
                    case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
                    case 'Y': case 'Z':
                    case '0': case '1': case '2': case '3': case '4':
                    case '5': case '6': case '7': case '8': case '9':
                    case '_':
                        break;
                    default:
                        String strId = id.toString();
                        Integer i = reservedWordIds.get(
                            strId.toUpperCase());
                        if (i == null) {
                            // identifier
                            return makeId(strId, false, ampersandId);
                        } else {
                            // reserved word
                            return makeRes(i);
                        }
                    }
                }

            case '&':
                advance();
                switch (nextChar) {
                case '[':
                    // fall through to parse a delimited identifier
                    ampersandId = true;
                    break;
                case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
                case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
                case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
                case 's': case 't': case 'u': case 'v': case 'w': case 'x':
                case 'y': case 'z':
                case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
                case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
                case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
                case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
                case 'Y': case 'Z':
                    // fall into logic to create identifer
                    ampersandId = true;
                    break mainSwitch;
                default:
                    // error
                    return makeToken(DefaultMdxParserSym.UNKNOWN, "&");
                }

            case '[':
                /* parse a delimited identifier */
                id = new StringBuilder();
                for (;;) {
                    advance();
                    switch (nextChar) {
                    case ']':
                        advance();
                        if (nextChar == ']') {
                            // ] escaped with ] - just take one
                            id.append(']');
                            break;
                        } else {
                            // end of identifier
                            if (ampersandId) {
                                ampersandId = false;
                                return makeId(id.toString(), true, true);
                            } else {
                                return makeId(id.toString(), true, false);
                            }
                        }
                    case -1:
                        if (ampersandId) {
                            ampersandId = false;
                            return makeId(id.toString(), true, true);
                        } else {
                            return makeId(id.toString(), true, false);
                        }
                    default:
                        id.append((char) nextChar);
                    }
                }

            case ':':
                advance();
                return makeToken(DefaultMdxParserSym.COLON, ":");
            case ',':
                advance();
                return makeToken(DefaultMdxParserSym.COMMA, ",");
            case '=':
                advance();
                return makeToken(DefaultMdxParserSym.EQ, "=");
            case '<':
                advance();
                switch (nextChar) {
                case '>':
                    advance();
                    return makeToken(DefaultMdxParserSym.NE, "<>");
                case '=':
                    advance();
                    return makeToken(DefaultMdxParserSym.LE, "<=");
                default:
                    return makeToken(DefaultMdxParserSym.LT, "<");
                }
            case '>':
                advance();
                switch (nextChar) {
                case '=':
                    advance();
                    return makeToken(DefaultMdxParserSym.GE, ">=");
                default:
                    return makeToken(DefaultMdxParserSym.GT, ">");
                }
            case '{':
                advance();
                return makeToken(DefaultMdxParserSym.LBRACE, "{");
            case '(':
                advance();
                return makeToken(DefaultMdxParserSym.LPAREN, "(");
            case '}':
                advance();
                return makeToken(DefaultMdxParserSym.RBRACE, "}");
            case ')':
                advance();
                return makeToken(DefaultMdxParserSym.RPAREN, ")");
            case '+':
                advance();
                return makeToken(DefaultMdxParserSym.PLUS, "+");
            case '-':
                advance();
                return makeToken(DefaultMdxParserSym.MINUS, "-");
            case '*':
                advance();
                return makeToken(DefaultMdxParserSym.ASTERISK, "*");
            case '/':
                advance();
                return makeToken(DefaultMdxParserSym.SOLIDUS, "/");
            case '|':
                advance();
                switch (nextChar) {
                case '|':
                    advance();
                    return makeToken(DefaultMdxParserSym.CONCAT, "||");
                default:
                    return makeToken(DefaultMdxParserSym.UNKNOWN, "|");
                }

            case '"':
                /* parse a double-quoted string */
                id = new StringBuilder();
                for (;;) {
                    advance();
                    switch (nextChar) {
                    case '"':
                        advance();
                        if (nextChar == '"') {
                            // " escaped with "
                            id.append('"');
                            break;
                        } else {
                            // end of string
                            return makeString(id.toString());
                        }
                    case -1:
                        return makeString(id.toString());
                    default:
                        id.append((char) nextChar);
                    }
                }

            case '\'':
                if (previousSymbol == DefaultMdxParserSym.AS) {
                    inFormula = true;
                }

                /* parse a single-quoted string */
                id = new StringBuilder();
                for (;;) {
                    advance();
                    switch (nextChar) {
                    case '\'':
                        advance();
                        if (nextChar == '\'') {
                            // " escaped with "
                            id.append('\'');
                            break;
                        } else {
                            // end of string
                            return makeString(id.toString());
                        }
                    case -1:
                        return makeString(id.toString());
                    default:
                        id.append((char) nextChar);
                    }
                }

            case -1:
                // we're done
                return makeToken(DefaultMdxParserSym.EOF, "EOF");

            default:
                // If it's whitespace, skip over it.
                if (nextChar <= Character.MAX_VALUE
                    && Character.isWhitespace(nextChar))
                {
                    // fall through
                } else {
                    // everything else is an error
                    throw new MdxParseException(
                        createRegion(iPrevChar, iChar),
                        "Unexpected character '" + (char) nextChar + "'");
                }

            case ' ':
            case '\t':
            case '\n':
            case '\r':
                // whitespace can be ignored
                iPrevChar = iChar;
                advance();
                break;
            }
        }
    }

    /**
     * Creates a region from a start and end point.
     * Called by {@link DefaultMdxParser#syntax_error}.
     */
    ParseRegion createRegion(final int left, final int right) {
        int target = left;
        int line = -1;
        int lineEnd = 0;
        int lineStart;
        do {
            line++;
            lineStart = lineEnd;
            lineEnd = Integer.MAX_VALUE;
            if (line < lines.size()) {
                lineEnd = lines.get(line);
            }
        } while (lineEnd < target);

        int startLine = line;
        int startColumn = target - lineStart;

        if (right == left) {
            return new ParseRegion(startLine + 1, startColumn + 1);
        }

        target = right - 1;
        if (target > left) --target; // don't know why
        line = -1;
        lineEnd = 0;
        do {
            line++;
            lineStart = lineEnd;
            lineEnd = Integer.MAX_VALUE;
            if (line < lines.size()) {
                lineEnd = lines.get(line);
            }
        } while (lineEnd < target);

        int endLine = line;
        int endColumn = target - lineStart;

        return new ParseRegion(
            startLine + 1, startColumn + 1, endLine + 1, endColumn + 1);
    }

    private enum State {
        leftOfPoint,
        rightOfPoint,
        inExponent,
    }
}

// End Scanner.java
