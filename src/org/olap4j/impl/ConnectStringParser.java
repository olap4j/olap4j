/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import java.util.*;

/**
 * Parser for olap4j connect strings.
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 12, 2007
 */
// Copied from mondrian.olap.Util.ConnectStringParser
public class ConnectStringParser {
    private final String s;
    private final int n;
    private int i;
    private final StringBuilder nameBuf;
    private final StringBuilder valueBuf;

    /**
     * Converts an OLE DB connect string into a {@link java.util.Map}.
     *
     * <p> For example, <code>"Provider=MSOLAP; DataSource=LOCALHOST;"</code>
     * becomes the set of (key, value) pairs <code>{("Provider","MSOLAP"),
     * ("DataSource", "LOCALHOST")}</code>. Another example is
     * <code>Provider='sqloledb';Data Source='MySqlServer';Initial
     * Catalog='Pubs';Integrated Security='SSPI';</code>.
     *
     * <p> This method implements as much as possible of the <a
     * href="http://msdn.microsoft.com/library/en-us/oledb/htm/oledbconnectionstringsyntax.asp"
     * target="_blank">OLE DB connect string syntax
     * specification</a>.
     *
     * <p>The return value is a map which:
     * <ul>
     * <li>preserves the order in that the entries occurred;</li>
     * <li>is not case-sensitive when looking up properties</li>
     * </ul>
     *
     * @param s Connect string
     *
     * @return Map containing (name, value) pairs, stored in the order that
     * they occurred in the connect string
     */
    public static Map<String, String> parseConnectString(String s) {
        return new ConnectStringParser(s).parse();
    }

    private ConnectStringParser(String s) {
        this.s = s;
        this.i = 0;
        this.n = s.length();
        this.nameBuf = new StringBuilder(64);
        this.valueBuf = new StringBuilder(64);
    }

    private PropertyMap parse() {
        PropertyMap map = new PropertyMap();
        while (i < n) {
            parsePair(map);
        }
        return map;
    }

    /**
     * Reads "name=value;" or "name=value<EOF>".
     *
     * @param map Map to append value to
     */
    private void parsePair(PropertyMap map) {
        String name = parseName();
        if (name == null) {
            return;
        }
        String value;
        if (i >= n) {
            value = "";
        } else if (s.charAt(i) == ';') {
            i++;
            value = "";
        } else {
            value = parseValue();
        }
        map.put(name, value);
    }

    /**
     * Reads "name=". Name can contain equals sign if equals sign is
     * doubled.
     *
     * @return Next name in the connect string being parsed, or null if there
     * is no further name
     */
    private String parseName() {
        nameBuf.setLength(0);
        while (true) {
            char c = s.charAt(i);
            switch (c) {
            case '=':
                i++;
                if (i < n && (c = s.charAt(i)) == '=') {
                    // doubled equals sign; take one of them, and carry on
                    i++;
                    nameBuf.append(c);
                    break;
                }
                String name = nameBuf.toString();
                name = name.trim();
                return name;
            case ' ':
                if (nameBuf.length() == 0) {
                    // ignore preceding spaces
                    i++;
                    if (i >= n) {
                        // there is no name, e.g. trailing spaces after
                        // semicolon, 'x=1; y=2; '
                        return null;
                    }
                    break;
                } else {
                    // fall through
                }
            default:
                nameBuf.append(c);
                i++;
                if (i >= n) {
                    return nameBuf.toString().trim();
                }
            }
        }
    }

    /**
     * Reads "value;" or "value<EOF>"
     *
     * @return next value from connect string being parsed
     */
    private String parseValue() {
        char c;
        // skip over leading white space
        while ((c = s.charAt(i)) == ' ') {
            i++;
            if (i >= n) {
                return "";
            }
        }
        if (c == '"' || c == '\'') {
            String value = parseQuoted(c);
            // skip over trailing white space
            while (i < n && (c = s.charAt(i)) == ' ') {
                i++;
            }
            if (i >= n) {
                return value;
            } else if (c == ';') {
                i++;
                return value;
            } else {
                throw new RuntimeException(
                        "quoted value ended too soon, at position " + i +
                        " in '" + s + "'");
            }
        } else {
            String value;
            int semi = s.indexOf(';', i);
            if (semi >= 0) {
                value = s.substring(i, semi);
                i = semi + 1;
            } else {
                value = s.substring(i);
                i = n;
            }
            return value.trim();
        }
    }

    /**
     * Reads a string quoted by a given character. Occurrences of the
     * quoting character must be doubled. For example,
     * <code>parseQuoted('"')</code> reads <code>"a ""new"" string"</code>
     * and returns <code>a "new" string</code>.
     *
     * @param q Quoting character (usually single or double quote)
     * @return quoted string
     */
    private String parseQuoted(char q) {
        char c = s.charAt(i++);
        assert c == q;
        valueBuf.setLength(0);
        while (i < n) {
            c = s.charAt(i);
            if (c == q) {
                i++;
                if (i < n) {
                    c = s.charAt(i);
                    if (c == q) {
                        valueBuf.append(c);
                        i++;
                        continue;
                    }
                }
                return valueBuf.toString();
            } else {
                valueBuf.append(c);
                i++;
            }
        }
        throw new RuntimeException(
                "Connect string '" + s +
                "' contains unterminated quoted value '" +
                valueBuf.toString() + "'");
    }

    private static class PropertyMap extends LinkedHashMap<String, String> {
        private final Map<String, String> originalKeys =
            new HashMap<String, String>();
        private static final String PROVIDER = normalize("Provider");

        public String get(Object key) {
            return super.get(normalize((String) key));
        }

        public String remove(Object key) {
            return super.remove(normalize((String) key));
        }

        public String put(String key, String value) {
            final String normalizedKey = normalize(key);
            if (normalizedKey.equals(PROVIDER)
                && containsKey(normalizedKey))
            {
                // "Provider" is the sole property which does not override.
                // The first occurrence of "Provider" is the one which is used.
                return null;
            }
            originalKeys.put(normalizedKey, key);
            return super.put(normalizedKey, value);
        }

        public boolean containsKey(Object key) {
            return super.containsKey(normalize((String) key));
        }

        private static String normalize(String key) {
            return key.toUpperCase();
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(64);
            int i = 0;
            for (Map.Entry<String, String> entry : entrySet()) {
                if (i++ > 0) {
                    sb.append("; ");
                }
                final String key = entry.getKey();
                final String originalKey = originalKeys.get(key);
                sb.append(originalKey);
                sb.append('=');

                final String value = entry.getValue();
                if (value == null) {
                    sb.append("'null'");
                } else {
                    /*
                     * Quote a property value if is has a semi colon in it
                     * 'xxx;yyy';
                     */
                    if (value.indexOf(';') >= 0 && value.charAt(0) != '\'') {
                        sb.append("'");
                    }

                    sb.append(value);

                    if (value.indexOf(';') >= 0 && value.charAt(
                        value.length() - 1) != '\'') {
                        sb.append("'");
                    }
                }
            }
            return sb.toString();
        }
    }
}

// End ConnectStringParser.java
