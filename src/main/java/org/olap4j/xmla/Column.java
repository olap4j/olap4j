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
package org.olap4j.xmla;

import java.lang.reflect.*;

/**
* Column in an XMLA row set.
*/
public class Column {

    /**
     * This is used as the true value for the restriction parameter.
     */
    public static final Restriction RESTRICTION = Restriction.OPTIONAL;
    /**
     * This is used as the false value for the restriction parameter.
     */
    public static final Restriction NOT_RESTRICTION = Restriction.NO;

    /**
     * This is used as the false value for the nullable parameter.
     */
    public static final boolean REQUIRED = false;
    /**
     * This is used as the true value for the nullable parameter.
     */
    public static final boolean OPTIONAL = true;

    /**
     * This is used as the false value for the unbounded parameter.
     */
    public static final boolean ONE_MAX = false;
    /**
     * This is used as the true value for the unbounded parameter.
     */
    public static final boolean UNBOUNDED = true;

    /** Name of this {@code Column}. */
    public final String name;

    /** Type of this {@code Column}. */
    public final XmlaType type;

    /** Enumeration that provides the values of this {@code Column}. */
    public final Enumeration enumeration;

    /** Description of this {@code Column}. */
    public final String description;

    /** @deprecated Use {@link Entity#restrictionColumns()}. */
    @Deprecated
    public final Restriction restriction;

    /** Whether this {@code Column} allows null values. */
    public final boolean nullable;

    /** Whether this {@code Column} is unbounded. */
    public final boolean unbounded;

    /**
     * Creates a column.
     *
     * @param name Name of column
     * @param type A {@link XmlaType} value
     * @param description Description of column
     * @param restriction Whether column can be used as a filter on its
     *     rowset
     * @param nullable Whether column can contain null values
     * @pre type != null
     * @pre (type == Type.Enumeration
     *  || type == Type.EnumerationArray
     *  || type == Type.EnumString)
     *  == (enumeratedType != null)
     * @pre description == null || description.indexOf('\r') == -1
     */
    Column(
        String name,
        XmlaType.ColumnType type,
        Restriction restriction,
        boolean nullable,
        String description)
    {
        this(
            name, type,
            restriction, nullable, ONE_MAX, description);
    }

    Column(
        String name,
        XmlaType.ColumnType type,
        Restriction restriction,
        boolean nullable,
        boolean unbounded,
        String description)
    {
        assert type != null;
        switch (type.xmlaType) {
        case Enumeration:
        case EnumerationArray:
        case EnumString:
            assert type.enumeratedType != null;
            break;
        case Short:
        case UnsignedShort:
        case Integer:
        case String:
            break;
        default:
            assert type.enumeratedType == null;
        }
        // Line endings must be UNIX style (LF) not Windows style (LF+CR).
        // Thus the client will receive the same XML, regardless
        // of the server O/S.
        assert description == null || description.indexOf('\r') == -1;
        this.name = name;
        this.type = type.xmlaType;
        this.enumeration = type.enumeratedType;
        this.description = description;
        this.restriction = restriction;
        this.nullable = nullable;
        this.unbounded = unbounded;
    }

    /** Call this method when declaring a column to indicate that the column
     * is a Mondrian extension to the XMLA standard. The method does nothing
     * at this time. */
    Column extension() {
        return this;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Retrieves a value of this column from a row. The base implementation
     * uses reflection to call an accessor method; a derived class may
     * provide a different implementation.
     *
     * @param row Row
     *
     * @see org.olap4j.impl.Bug Should be in xmla-server, not olap4j.
     */
    public Object get(Object row) {
        return getFromAccessor(row);
    }

    /**
     * Retrieves the value of this column "MyColumn" from a field called
     * "myColumn".
     *
     * @param row Current row
     * @return Value of given this property of the given row
     */
    protected final Object getFromField(Object row) {
        try {
            String javaFieldName =
                name.substring(0, 1).toLowerCase()
                + name.substring(1);
            Field field = row.getClass().getField(javaFieldName);
            return field.get(row);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(
                "Error while accessing rowset column " + name, e);
        } catch (SecurityException e) {
            throw new RuntimeException(
                "Error while accessing rowset column " + name, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                "Error while accessing rowset column " + name, e);
        }
    }

    /**
     * Retrieves the value of this column "MyColumn" by calling a method
     * called "getMyColumn()".
     *
     * @param row Current row
     * @return Value of given this property of the given row
     */
    protected final Object getFromAccessor(Object row) {
        try {
            String javaMethodName = "get" + name;
            Method method = row.getClass().getMethod(javaMethodName);
            return method.invoke(row);
        } catch (SecurityException e) {
            throw new RuntimeException(
                "Error while accessing rowset column " + name, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                "Error while accessing rowset column " + name, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(
                "Error while accessing rowset column " + name, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(
                "Error while accessing rowset column " + name, e);
        }
    }

    public String getColumnType() {
        if (type.isEnum()) {
            return enumeration.type.columnType;
        }
        return type.columnType;
    }

    /** Defines whether and how a column may be used to restrict the output from
     * an XMLA schema rowset.
     *
     * @see org.olap4j.xmla.Entity#restrictionColumns()
     */
    public static class Restriction {
        public static final Restriction NO = new Restriction(null, null);
        public static final Restriction OPTIONAL = new Restriction(null, null);
        public static final Restriction MANDATORY = new Restriction(null, null);

        private final Restriction parent;
        private final Enumeration enumeration;

        private Restriction(Restriction parent, Enumeration enumeration) {
            this.parent = parent;
            this.enumeration = enumeration;
        }

        Restriction of(Enumeration enumeration, Enum<?> default_) {
            return new Restriction(this, enumeration);
        }
    }
}

// End Column.java
