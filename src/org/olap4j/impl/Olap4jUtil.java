/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.impl;

import org.olap4j.metadata.NamedList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods common to multiple olap4j driver implementations.
 *
 * <p>This class, and this package as a whole, are part of the olap4j library
 * but the methods are not part of the public olap4j API. The classes exist
 * for the convenience of implementers of olap4j drivers, but their
 * specification and implementation may change at any time.</p>
 *
 * <p><b>Applications which use the API in this package will not be portable
 * across multiple versions of olap4j</b>. We encourage implementors of drivers
 * to use classes in this package, but not writers of applications.</p>
 *
 * @author jhyde
 * @version $Id$
 * @since Dec 12, 2007
 */
public class Olap4jUtil {
    /**
     * Whether we are running a version of Java before 1.5.
     *
     * <p>If this variable is true, we will be running retroweaver. Retroweaver
     * has some problems involving {@link java.util.EnumSet}.
     */
    public static final boolean PreJdk15 =
        System.getProperty("java.version").startsWith("1.4");

    /**
     * Whether the code base has re-engineered using retroweaver.
     * If this is the case, some functionality is not available.
     */
    public static final boolean Retrowoven =
        DummyEnum.class.getSuperclass().getName().equals(
            "com.rc.retroweaver.runtime.Enum_");

    private static final Olap4jUtilCompatible compatible;

    private static final NamedList<?> EMPTY_NAMED_LIST =
        new EmptyNamedList();

    static {
        String className;
        if (PreJdk15 || Retrowoven) {
            className = "org.olap4j.impl.Olap4jUtilCompatibleJdk14";
        } else {
            className = "org.olap4j.impl.Olap4jUtilCompatibleJdk15";
        }
        try {
            //noinspection unchecked
            Class<Olap4jUtilCompatible> clazz =
                (Class<Olap4jUtilCompatible>) Class.forName(className);
            compatible = clazz.newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not load '" + className + "'", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Could not load '" + className + "'", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Could not load '" + className + "'", e);
        }
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(boolean b) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(byte b) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(char c) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(double v) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(float v) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(int i) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(long l) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(Object o) {
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public static void discard(short i) {
    }

    /**
     * Casts a Set to a Set with a different element type.
     *
     * @param set Set
     * @return Set of desired type
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Set<T> cast(Set<?> set) {
        return (Set<T>) set;
    }

    /**
     * Casts a List to a List with a different element type.
     *
     * @param list List
     * @return List of desired type
     */
    @SuppressWarnings({"unchecked"})
    public static <T> List<T> cast(List<?> list) {
        return (List<T>) list;
    }

    /**
     * Casts a NamedList to a NamedList with a different element type.
     *
     * @param list Named list
     * @return Named list of desired type
     */
    @SuppressWarnings({"unchecked"})
    public static <T> NamedList<T> cast(NamedList<?> list) {
        return (NamedList<T>) list;
    }

    /**
     * Returns an exception indicating that we didn't expect to find this value
     * here.
     *
     * @param value Value
     * @return an AssertionError which can be thrown
     */
    public static AssertionError unexpected(Enum value) {
        return new AssertionError(
            "Was not expecting value '" + value
            + "' for enumeration '" + value.getClass().getName()
            + "' in this context");
    }

    /**
    * Returns true if two objects are equal, or are both null.
     *
     * @param t1 First value
     * @param t2 Second value
     * @return Whether values are both equal or both null
     */
    public static <T> boolean equal(T t1, T t2) {
        return t1 == null ? t2 == null : t1.equals(t2);
    }

    /**
     * Returns a string with every occurrence of a seek string replaced with
     * another.
     *
     * @param s String to act on
     * @param find String to find
     * @param replace String to replace it with
     * @return The modified string
     */
    public static String replace(
        String s,
        String find,
        String replace)
    {
        // let's be optimistic
        int found = s.indexOf(find);
        if (found == -1) {
            return s;
        }
        StringBuilder sb = new StringBuilder(s.length() + 20);
        int start = 0;
        char[] chars = s.toCharArray();
        final int step = find.length();
        if (step == 0) {
            // Special case where find is "".
            sb.append(s);
            replace(sb, 0, find, replace);
        } else {
            for (;;) {
                sb.append(chars, start, found - start);
                if (found == s.length()) {
                    break;
                }
                sb.append(replace);
                start = found + step;
                found = s.indexOf(find, start);
                if (found == -1) {
                    found = s.length();
                }
            }
        }
        return sb.toString();
    }

    /**
     * Replaces all occurrences of a string in a buffer with another.
     *
     * @param buf String buffer to act on
     * @param start Ordinal within <code>find</code> to start searching
     * @param find String to find
     * @param replace String to replace it with
     * @return The string buffer
     */
    public static StringBuilder replace(
        StringBuilder buf,
        int start,
        String find,
        String replace)
    {
        // Search and replace from the end towards the start, to avoid O(n ^ 2)
        // copying if the string occurs very commonly.
        int findLength = find.length();
        if (findLength == 0) {
            // Special case where the seek string is empty.
            for (int j = buf.length(); j >= 0; --j) {
                buf.insert(j, replace);
            }
            return buf;
        }
        int k = buf.length();
        while (k > 0) {
            int i = buf.lastIndexOf(find, k);
            if (i < start) {
                break;
            }
            buf.replace(i, i + find.length(), replace);
            // Step back far enough to ensure that the beginning of the section
            // we just replaced does not cause a match.
            k = i - findLength;
        }
        return buf;
    }

    /**
     * Converts a list of SQL-style patterns into a Java regular expression.
     *
     * <p>For example, {"Foo_", "Bar%BAZ"} becomes "Foo.|Bar.*BAZ".
     *
     * @param wildcards List of SQL-style wildcard expressions
     * @return Regular expression
     */
    public static String wildcardToRegexp(List<String> wildcards) {
        StringBuilder buf = new StringBuilder();
        for (String value : wildcards) {
            if (buf.length() > 0) {
                buf.append('|');
            }
            int i = 0;
            while (true) {
                int percent = value.indexOf('%', i);
                int underscore = value.indexOf('_', i);
                if (percent == -1 && underscore == -1) {
                    if (i < value.length()) {
                        buf.append(quotePattern(value.substring(i)));
                    }
                    break;
                }
                if (underscore >= 0 && (underscore < percent || percent < 0)) {
                    if (i < underscore) {
                        buf.append(
                            quotePattern(value.substring(i, underscore)));
                    }
                    buf.append('.');
                    i = underscore + 1;
                } else if (percent >= 0
                    && (percent < underscore || underscore < 0))
                {
                    if (i < percent) {
                    buf.append(
                        quotePattern(value.substring(i, percent)));
                    }
                    buf.append(".*");
                    i = percent + 1;
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }
        return buf.toString();
    }

    /**
     * Returns a literal pattern String for the specified String.
     *
     * <p>Specification as for {@link java.util.regex.Pattern#quote(String)}, which was
     * introduced in JDK 1.5.
     *
     * @param s The string to be literalized
     * @return A literal string replacement
     */
    public static String quotePattern(String s) {
        return compatible.quotePattern(s);
    }

    /**
     * Converts a camel-case name to an upper-case name with underscores.
     *
     * <p>For example, <code>camelToUpper("FooBar")</code> returns "FOO_BAR".
     *
     * @param s Camel-case string
     * @return  Upper-case string
     */
    public static String camelToUpper(String s) {
        StringBuilder buf = new StringBuilder(s.length() + 10);
        int prevUpper = -1;
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > prevUpper + 1) {
                    buf.append('_');
                }
                prevUpper = i;
            } else {
                c = Character.toUpperCase(c);
            }
            buf.append(c);
        }
        return buf.toString();
    }

    /**
     * Returns an exception which indicates that a particular piece of
     * functionality should work, but a developer has not implemented it yet.
     *
     * @param o Object operation was called on, perhaps indicating the
     * subtype where a virtual method needs to be implemented
     *
     * @return an exception which can be thrown
     */
    public static RuntimeException needToImplement(Object o) {
        throw new UnsupportedOperationException("need to implement " + o);
    }

    public static String[] uniqueNameToStringArray(String uniqueName) {
        List<String> trail = new ArrayList<String>();
        Pattern regex = Pattern.compile("([^\\[\\]\\.]*)");
        Matcher matcher = regex.matcher(uniqueName);
        while (matcher.find()) {
            String match = matcher.group();
            if (!match.equals("")) {
                trail.add(match);
            }
        }
        return trail.toArray(new String[trail.size()]);
    }

    @SuppressWarnings({"unchecked"})
    public static <T> NamedList<T> emptyNamedList() {
        return (NamedList<T>) EMPTY_NAMED_LIST;
    }

    private enum DummyEnum {
    }

    /**
     * Implementation of {@link NamedList} whih is immutable and empty.
     */
    private static class EmptyNamedList<T> extends AbstractNamedList<T> {
        protected String getName(Object o) {
            throw new UnsupportedOperationException();
        }

        public int size() {
            return 0;
        }

        public T get(int index) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        // Preserves singleton property
        @SuppressWarnings({"UnusedDeclaration"})
        private Object readResolve() {
            return EMPTY_NAMED_LIST;
        }
    }
}

// End Olap4jUtil.java
