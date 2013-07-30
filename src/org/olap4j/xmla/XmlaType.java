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

/**
* Type of a {@link Column} in an XML/A row set.
*/
public enum XmlaType {
    String("xsd:string"),
    StringArray("xsd:string"),
    Array("xsd:string"),
    Enumeration("xsd:string"),
    EnumerationArray("xsd:string"),
    EnumString("xsd:string"),
    Boolean("xsd:boolean"),
    StringSometimesArray("xsd:string"),
    Integer("xsd:int"),
    UnsignedInteger("xsd:unsignedInt"),
    DateTime("xsd:dateTime"),
    Rowset(null),
    Short("xsd:short"),
    UUID("uuid"),
    UnsignedShort("xsd:unsignedShort"),
    Long("xsd:long"),
    UnsignedLong("xsd:unsignedLong");

    public final String columnType;

    XmlaType(String columnType) {
        this.columnType = columnType;
    }

    boolean isEnum() {
        return this == Enumeration
           || this == EnumerationArray
           || this == EnumString;
    }

    public String getName() {
        return this == String ? "string" : name();
    }

    ColumnType scalar() {
        return new ColumnType(this, null);
    }

    public ColumnType of(Enumeration enumeration) {
        return new ColumnType(this, enumeration);
    }

    static class ColumnType {
        final XmlaType xmlaType;
        Enumeration enumeratedType;

        public ColumnType(XmlaType xmlaType, Enumeration access) {
            this.xmlaType = xmlaType;
        }
    }
}


// End XmlaType.java
