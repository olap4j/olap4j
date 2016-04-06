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
package org.olap4j.driver.xmla;

import org.olap4j.OlapException;
import org.olap4j.impl.ArrayMap;
import org.olap4j.mdx.ParseTreeNode;
import org.olap4j.metadata.*;

import java.util.*;

/**
 * Implementation of {@link org.olap4j.metadata.Member}
 * for positions on an axis in a cell set
 * from an XML/A provider.
 *
 * <p>This class is necessary because a member can have different properties
 * when it is retrieved as part of a cell set than if it is retrieved by
 * querying schema metadata (e.g. using
 * {@link Cube#lookupMember(java.util.List)}.
 * XmlaOlap4jPositionMember wraps the schema member (which might potentially
 * be cached between queries - even though today it is not) and adds extra
 * properties. All other methods are delegated to the underlying member.</p>
 *
 * @author jhyde
 * @since Dec 7, 2007
 */
class XmlaOlap4jPositionMember
    implements XmlaOlap4jMemberBase
{
    private final XmlaOlap4jMemberBase member;
    private final Map<Property, Object> propertyValues;

    /**
     * Creates a XmlaOlap4jPositionMember.
     *
     * @param member Underlying member
     * @param propertyValues Property values
     */
    XmlaOlap4jPositionMember(
        XmlaOlap4jMemberBase member,
        Map<Property, Object> propertyValues)
    {
        assert member != null;
        assert propertyValues != null;
        this.member = member;
        this.propertyValues = new ArrayMap<Property, Object>(propertyValues);
    }

    public boolean equals(Object obj) {
        if (obj instanceof XmlaOlap4jPositionMember) {
            XmlaOlap4jPositionMember that =
                (XmlaOlap4jPositionMember) obj;
            return this.member.equals(that.member);
        } else if (obj instanceof XmlaOlap4jMember) {
            XmlaOlap4jMember that = (XmlaOlap4jMember) obj;
            return this.member.equals(that);
        } else {
            return super.equals(obj);
        }
    }

    public int hashCode() {
        return member.hashCode();
    }

    public XmlaOlap4jCube getCube() {
        return member.getCube();
    }

    public Object getAnnotations() {
        return member.getAnnotations();
    }

    public XmlaOlap4jConnection getConnection() {
        return member.getConnection();
    }

    public XmlaOlap4jCatalog getCatalog() {
        return member.getCatalog();
    }

    public Map<Property, Object> getPropertyValueMap() {
        return new ChainedMap<Property, Object>(
            propertyValues,
            member.getPropertyValueMap());
    }

    public NamedList<? extends Member> getChildMembers() throws OlapException {
        return member.getChildMembers();
    }

    public int getChildMemberCount() throws OlapException {
        return member.getChildMemberCount();
    }

    public Member getParentMember() {
        return member.getParentMember();
    }

    public Level getLevel() {
        return member.getLevel();
    }

    public Hierarchy getHierarchy() {
        return member.getHierarchy();
    }

    public Dimension getDimension() {
        return member.getDimension();
    }

    public Type getMemberType() {
        return member.getMemberType();
    }

    public boolean isAll() {
        return member.isAll();
    }

    public boolean isChildOrEqualTo(Member member) {
        return this.member.isChildOrEqualTo(member);
    }

    public boolean isCalculated() {
        return member.isCalculated();
    }

    public int getSolveOrder() {
        return member.getSolveOrder();
    }

    public ParseTreeNode getExpression() {
        return member.getExpression();
    }

    public List<Member> getAncestorMembers() {
        return member.getAncestorMembers();
    }

    public boolean isCalculatedInQuery() {
        return member.isCalculatedInQuery();
    }

    public Object getPropertyValue(Property property) throws OlapException {
        if (propertyValues.containsKey(property)) {
            return propertyValues.get(property);
        }
        return member.getPropertyValue(property);
    }

    public String getPropertyFormattedValue(Property property)
        throws OlapException
    {
        // REVIEW: Formatted value is not available for properties which
        // come back as part of axis tuple. Unformatted property is best we
        // can do.
        if (propertyValues.containsKey(property)) {
            return String.valueOf(propertyValues.get(property));
        }
        return member.getPropertyFormattedValue(property);
    }

    public void setProperty(
        Property property,
        Object value)
        throws OlapException
    {
        throw new UnsupportedOperationException();
    }

    public NamedList<Property> getProperties() {
        return member.getProperties();
    }

    public int getOrdinal() {
        return member.getOrdinal();
    }

    public boolean isHidden() {
        return member.isHidden();
    }

    public int getDepth() {
        try {
            final Object value =
                XmlaOlap4jMember.getPropertyValue(
                    Property.StandardMemberProperty.DEPTH,
                    member,
                    getPropertyValueMap());
            return XmlaOlap4jMember.toInteger(value);
        } catch (OlapException e) {
            // should not happen; only CHILDREN_CARDINALITY can potentially
            // give an error
            throw new RuntimeException(e);
        }
    }

    public Member getDataMember() {
        return member.getDataMember();
    }

    public String getName() {
        return member.getName();
    }

    public String getUniqueName() {
        return member.getUniqueName();
    }

    public String getCaption() {
        return member.getCaption();
    }

    public String getDescription() {
        return member.getDescription();
    }

    public boolean isVisible() {
        return member.isVisible();
    }

    /**
     * Read-only map that contains the union of two maps.
     */
    private static class ChainedMap<K, V> implements Map<K, V> {
        private final Map<? extends K, ? extends V> map;
        private final Map<? extends K, ? extends V> next;

        /**
         * Creates a ChainedMap.
         *
         * @param map First map in the chain
         * @param next Next map in the chain
         */
        ChainedMap(
            Map<? extends K, ? extends V> map,
            Map<? extends K, ? extends V> next)
        {
            this.map = map;
            this.next = next;
        }

        public int size() {
            int n = next.size();
            for (K k : map.keySet()) {
                //noinspection SuspiciousMethodCalls
                if (!next.containsKey(k)) {
                    ++n;
                }
            }
            return n;
        }

        public boolean isEmpty() {
            return map.isEmpty()
                && next.isEmpty();
        }

        public boolean containsKey(Object key) {
            return map.containsKey(key)
                || next.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return map.containsValue(value)
                || next.containsValue(value);
        }

        public V get(Object key) {
            //noinspection SuspiciousMethodCalls
            if (map.containsKey(key)) {
                return map.get(key);
            } else {
                return next.get(key);
            }
        }

        public V put(K key, V value) {
            throw new UnsupportedOperationException("read only");
        }

        public V remove(Object key) {
            throw new UnsupportedOperationException("read only");
        }

        public void putAll(Map<? extends K, ? extends V> t) {
            throw new UnsupportedOperationException("read only");
        }

        public void clear() {
            throw new UnsupportedOperationException("read only");
        }

        public Set<K> keySet() {
            throw new UnsupportedOperationException("need to implement");
        }

        public Collection<V> values() {
            throw new UnsupportedOperationException("need to implement");
        }

        public Set<Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException("need to implement");
        }
    }
}

// End XmlaOlap4jPositionMember.java
