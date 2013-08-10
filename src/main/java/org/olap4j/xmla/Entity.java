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

import org.olap4j.impl.Olap4jUtil;
import org.olap4j.impl.UnmodifiableArrayList;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Entity in the XML for Analysis meta-model.
 *
 * <p>An entity generally has an associated schema rowset, and a method by
 * which to query it. For example, the {@link XmlaCube} entity corresponds
 * to the {@code MDSCHEMA_CUBES} schema rowset and the olap4j metadata class
 * {@link org.olap4j.metadata.Cube}.</p>
 *
 * <p>Each entity has a collection of {@code public final} fields of type
 * {@link Column}. These fields define the attributes of the entity available
 * as restrictions, sort fields or result fields in the entity's corresponding
 * schema rowset.</p>
 *
 * <p>If you need to use a column in client code, it is convenient to use the
 * name of the {@code Column} object. For example, you would write
 *
 * <pre>
 * <code>{@link XmlaHierarchy}.INSTANCE.{@link XmlaHierarchy#Structure Structure}.name</code>
 * </pre>
 *
 * <p>rather than</p>
 *
 * <pre>{@code "STRUCTURE"}</pre>
*/
public abstract class Entity {
    protected static List<Column> list(Column... columns) {
        switch (columns.length) {
        case 0:
            return Collections.emptyList();
        case 1:
            return Collections.singletonList(columns[0]);
        default:
            return Collections.unmodifiableList(
                Arrays.asList(columns));
        }
    }

    // These methods create a new list each time; they should be called
    // only when constructing static objects, not at runtime. RowsetDefinition
    // contains cached versions.

    /** Returns a list of result columns of this entity's schema rowset.
     *
     * @see RowsetDefinition#columns */
    abstract List<Column> columns();

    /** Returns a list of columns that sort this entity's schema rowset; empty
     * list if the schema rowset is not sorted.
     *
     * @see RowsetDefinition#sortColumns */
    abstract List<Column> sortColumns();

    /** Returns a list of columns that may be used to restrict this entity's
     * schema rowset.
     *
     * @see RowsetDefinition#restrictionColumns */
    List<Column> restrictionColumns() {
        final ArrayList<Column> list = new ArrayList<Column>();
        for (Column column : columns()) {
            if (column.restriction != Column.Restriction.NO) {
                list.add(column);
            }
        }
        return UnmodifiableArrayList.copyOf(list);
    }

    /** Returns the definition of the rowset that returns instances of this
     * {@code Entity}. */
    public abstract RowsetDefinition def();

    /** Checks whether this entity is valid.
     *
     * @param fail Whether to throw an {@link AssertionError} if not valid
     * @return Whether valid.
     */
    protected boolean isValid(boolean fail) {
        try {
            final Set<Column> columns = new HashSet<Column>();
            for (Field field : getClass().getFields()) {
                if (field.getType().isAssignableFrom(Column.class)) {
                    Column column = (Column) field.get(this);
                    columns.add(column);
                    final String up =
                        isOleDbForOlap()
                            ? Olap4jUtil.camelToUpper(field.getName())
                            : field.getName();
                    if (!column.name.equals(up)) {
                        assert !fail : field;
                        return false;
                    }
                }
            }
            // Every column in columns or restrictionColumns is also declared
            // as a field.
            final Set<Column> columns2 = new HashSet<Column>();
            columns2.addAll(restrictionColumns());
            columns2.addAll(columns());
            if (!columns.equals(columns2)) {
                assert !fail : def() + "\n" + columns + "\n" + columns2 + "\n";
                return false;
            }
            // Every sort column is also in columns.
            final Set<Column> columns3 = new HashSet<Column>();
            columns3.addAll(sortColumns());
            columns3.removeAll(columns);
            if (!columns3.isEmpty()) {
                assert !fail : columns3;
                return false;
            }
            if (!unique(columns())) {
                assert !fail : "columns must be unique";
                return false;
            }
            if (!unique(sortColumns())) {
                assert !fail : "sort columns must be unique";
                return false;
            }
            if (!unique(restrictionColumns())) {
                assert !fail : "restriction columns must be unique";
                return false;
            }
            return true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /** Core OLE DB rowsets have camel-case
     * column names, whereas OLE DB for OLAP have upper-case. */
    private boolean isOleDbForOlap() {
        final Class<? extends Entity> clazz = getClass();
        return clazz != XmlaDatasource.class
               && clazz != XmlaSchemaRowset.class
               && clazz != XmlaEnumerator.class
               && clazz != XmlaDatabaseProperty.class
               && clazz != XmlaKeyword.class
               && clazz != XmlaLiteral.class;
    }

    /** Returns whether a list has unique values. */
    static <E> boolean unique(List<E> list) {
        return list.size() < 2 || new HashSet<E>(list).size() == list.size();
    }
}

// End Entity.java
