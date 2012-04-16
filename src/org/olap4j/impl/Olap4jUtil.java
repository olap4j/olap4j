/*
// $Id$
//
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

    private static final Pattern CELL_VALUE_REGEX1 =
        Pattern.compile("\\s*([a-zA-Z][\\w\\.]*)\\s*=\\s*'([^']*)'");

    private static final Pattern CELL_VALUE_REGEX2 =
        Pattern.compile("\\s*([a-zA-Z][\\w\\.]*)\\s*=\\s*([^\\s]*)");

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
     * Returns a hashmap with given contents.
     *
     * <p>Use this method in initializers. Type parameters are inferred from
     * context, and the contents are initialized declaratively. For example,
     *
     * <blockquote><code>Map&lt;String, Integer&gt; population =<br/>
     * &nbsp;&nbsp;Olap4jUtil.mapOf(<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;"UK", 65000000,<br/>
     * &nbsp;&nbsp;&nbsp;&nbsp;"USA", 300000000);</code></blockquote>
     *
     * @see org.olap4j.impl.UnmodifiableArrayMap#of(Object, Object, Object...)
     * @see org.olap4j.impl.ArrayMap#of(Object, Object, Object...)
     *
     * @param key First key
     * @param value First value
     * @param keyValues Second and sequent key/value pairs
     * @param <K> Key type
     * @param <V> Value type
     * @return Map with given contents
     */
    public static <K, V> Map<K, V> mapOf(K key, V value, Object... keyValues)
    {
        final Map<K, V> map = new LinkedHashMap<K, V>(1 + keyValues.length);
        map.put(key, value);
        for (int i = 0; i < keyValues.length;) {
            //noinspection unchecked
            map.put((K) keyValues[i++], (V) keyValues[i++]);
        }
        return map;
    }

    /**
     * Converts a Properties object to a Map with String keys and values.
     *
     * @param properties Properties
     * @return Map backed by the given Properties object
     */
    public static Map<String, String> toMap(final Properties properties) {
        return new AbstractMap<String, String>() {
            public Set<Entry<String, String>> entrySet() {
                return cast(properties.entrySet());
            }
        };
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

    /**
     * Parses a unique name.
     *
     * <p>For example, <tt>uniqueNameToStringArray("[foo].[bar]")</tt> returns
     * {@code ["foo", "bar"]}.
     *
     * @see org.olap4j.mdx.IdentifierNode#parseIdentifier(String)
     *
     * @param uniqueName Unique name
     * @return Parsed unique name
     */
    public static List<String> parseUniqueName(String uniqueName) {
        List<String> trail = new ArrayList<String>();
        Pattern regex = Pattern.compile("([^\\[\\]\\.]*)");
        Matcher matcher = regex.matcher(uniqueName);
        while (matcher.find()) {
            String match = matcher.group();
            if (!match.equals("")) {
                trail.add(match);
            }
        }
        return trail;
    }

    /**
     * Converts the contents of an array of strings to
     * a proper String representation.
     *
     * @param array Array of strings
     * @return string representation of the array
     */
    public static String stringArrayToString(String[] array) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < (array.length - 1)) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @SuppressWarnings({"unchecked"})
    public static <T> NamedList<T> emptyNamedList() {
        return (NamedList<T>) EMPTY_NAMED_LIST;
    }

    /**
     * Returns an unmodifiable view of the specified list.  This method allows
     * modules to provide users with "read-only" access to internal
     * lists.  Query operations on the returned list "read through" to the
     * specified list, and attempts to modify the returned list, whether
     * direct or via its iterator, result in an
     * <tt>UnsupportedOperationException</tt>.<p>
     *
     * The returned list will be serializable if the specified list
     * is serializable. Similarly, the returned list will implement
     * {@link RandomAccess} if the specified list does.
     *
     * <p>The equivalent of
     * {@link java.util.Collections#unmodifiableList(java.util.List)}.
     *
     * @param  list the list for which an unmodifiable view is to be returned.
     * @return an unmodifiable view of the specified list.
     */
    public static <T> NamedList<T> unmodifiableNamedList(
        final NamedList<T> list)
    {
        return list instanceof RandomAccess
            ? new UnmodifiableNamedRandomAccessList<T>(list)
            : new UnmodifiableNamedList<T>(list);
    }

    /**
     * Equivalent to {@link java.util.EnumSet#of(Enum, Enum[])} on JDK 1.5 or
     * later. Otherwise, returns an ordinary set.
     *
     * @param first an element that the set is to contain initially
     * @param rest the remaining elements the set is to contain initially
     * @throws NullPointerException if any of the specified elements are null,
     *     or if <tt>rest</tt> is null
     * @return an enum set initially containing the specified elements
     */
    public static <E extends Enum<E>> Set<E> enumSetOf(E first, E... rest) {
        return compatible.enumSetOf(first, rest);
    }

    /**
     * Equivalent to {@link java.util.EnumSet#noneOf(Class)} on JDK 1.5 or
     * later. Otherwise, returns an ordinary set.
     *
     * @param elementType the class object of the element type for this enum
     *     set
     * @return an empty enum set
     */
    public static <E extends Enum<E>> Set<E> enumSetNoneOf(
        Class<E> elementType)
    {
        return compatible.enumSetNoneOf(elementType);
    }

    /**
     * Equivalent to {@link java.util.EnumSet#allOf(Class)} on JDK 1.5 or later.
     * Otherwise, returns an ordinary set.

     * @param elementType the class object of the element type for this enum
     *     set
     * @return an enum set containing all elements of the given enum class
     */
    public static <E extends Enum<E>> Set<E> enumSetAllOf(
        Class<E> elementType)
    {
        return compatible.enumSetAllOf(elementType);
    }

    /**
     * Parses a formatted cell values.
     *
     * <p>There is a customary way of including formatting infornation in cell
     * values (started with Microsoft OLAP Services, and continued by JPivot and
     * now Pentaho Analyzer). This method parses out the formatted value that
     * should be displayed on the screen and also any properties present.
     *
     * <p>Examples:<ul>
     * <li>"$123" no formatting information</li>
     * <li>"|$123|style=red|" print in red style</li>
     * <li>"|$123|style=red|arrow=up|" print in red style with an up arrow</li>
     * </ul>
     *
     * <h4>Properties</h4>
     *
     * <table border="1">
     * <tr>
     *     <th>Name</th>
     *     <th>Value</th>
     *     <th>Description</th>
     * </tr>
     * <tr>
     *     <td>style</td>
     *     <td>red|green|yellow</td>
     *     <td>renders the Member in that color</td>
     * </tr>
     * <tr>
     *     <td>link</td>
     *     <td>a url</td>
     *     <td>creates a hyperlink on the member</td>
     * </tr>
     * <tr>
     *     <td>arrow</td>
     *     <td>up|down|blank</td>
     *     <td>paints an arrow image</td>
     * </tr>
     * <tr>
     *     <td>image</td>
     *     <td>a uri. If the uri starts with "/" the context name will be
     *         prepended</td>
     *     <td>paints image</td>
     * </tr>
     * </table>
     *
     * @param formattedValue Formatted cell value
     * @param map Map into which to place (property, value) pairs
     * @return Formatted cell value with properties removed
     */
    public static String parseFormattedCellValue(
        String formattedValue,
        Map<String, String> map)
    {
        if (formattedValue.startsWith("|")) {
            String[] strs = formattedValue.substring(1).split("\\|");
            formattedValue = strs[0]; // original value
            for (int i = 1; i < strs.length; i++) {
                Matcher m = CELL_VALUE_REGEX1.matcher(strs[i]);
                if (m.matches()) {
                    String propName = m.group(1); // property name
                    String propValue = m.group(2); // property value
                    map.put(propName, propValue);
                    continue;
                }

                m = CELL_VALUE_REGEX2.matcher(strs[i]);
                if (m.matches()) {
                    String propName = m.group(1); // property name
                    String propValue = m.group(2); // property value
                    map.put(propName, propValue);
                    continue;
                }

                // it is not a key=value pair
                // we add the String to the formatted value
                formattedValue += strs[i];
            }
        }
        return formattedValue;
    }

    private enum DummyEnum {
    }

    /**
     * Implementation of {@link NamedList} that is immutable and empty.
     */
    private static class EmptyNamedList<T> extends AbstractNamedList<T> {
        public String getName(Object element) {
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

    private static class UnmodifiableNamedList<T> implements NamedList<T> {
        private final NamedList<T> list;

        UnmodifiableNamedList(NamedList<T> list) {
            this.list = list;
        }

        public T get(String s) {
            return list.get(s);
        }

        public int indexOfName(String s) {
            return list.indexOfName(s);
        }

        public String getName(Object element) {
            return list.getName(element);
        }

        public Map<String, T> asMap() {
            return Collections.unmodifiableMap(list.asMap());
        }

        public int size() {
            return list.size();
        }

        public boolean isEmpty() {
            return list.isEmpty();
        }

        public boolean contains(Object o) {
            return list.contains(o);
        }

        public Iterator<T> iterator() {
            return Collections.unmodifiableList(list).iterator();
        }

        public Object[] toArray() {
            return list.toArray();
        }

        public <T2> T2[] toArray(T2[] a) {
            //noinspection SuspiciousToArrayCall
            return list.toArray(a);
        }

        public boolean add(T t) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        public boolean addAll(int index, Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public T get(int index) {
            return list.get(index);
        }

        public T set(int index, T element) {
            throw new UnsupportedOperationException();
        }

        public void add(int index, T element) {
            throw new UnsupportedOperationException();
        }

        public T remove(int index) {
            throw new UnsupportedOperationException();
        }

        public int indexOf(Object o) {
            return list.indexOf(0);
        }

        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        public ListIterator<T> listIterator() {
            return Collections.unmodifiableList(list).listIterator();
        }

        public ListIterator<T> listIterator(int index) {
            return Collections.unmodifiableList(list).listIterator(index);
        }

        public List<T> subList(int fromIndex, int toIndex) {
            // TODO: NamedList.subList should return NamedList.
            return Collections.unmodifiableList(
                list.subList(fromIndex, toIndex));
        }
    }

    private static class UnmodifiableNamedRandomAccessList<T>
        extends UnmodifiableNamedList<T>
        implements RandomAccess
    {
        UnmodifiableNamedRandomAccessList(NamedList<T> list) {
            super(list);
        }
    }
}

// End Olap4jUtil.java
