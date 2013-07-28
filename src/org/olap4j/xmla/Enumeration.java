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

import org.olap4j.impl.UnmodifiableArrayMap;
import org.olap4j.metadata.XmlaConstant;
import org.olap4j.metadata.XmlaConstants;

import java.util.List;
import java.util.Map;

/**
 * Contains inner classes which define enumerations used in XML for Analysis.
 */
public class Enumeration {
    public final String name;
    public final String description;
    public final XmlaType type;
    private final XmlaConstant.Dictionary<?> dictionary;

    public static final Enumeration TREE_OP =
        new Enumeration(
            "TREE_OP",
            "Bitmap which controls which relatives of a member are "
            + "returned",
            XmlaType.Integer,
            org.olap4j.metadata.Member.TreeOp.getDictionary());

    public static final Enumeration VISUAL_MODE =
        new Enumeration(
            "VisualMode",
            "This property determines the default behavior for visual "
            + "totals.",
            XmlaType.Integer,
            org.olap4j.metadata.XmlaConstants.VisualMode.getDictionary());

    public static final Enumeration METHODS =
        new Enumeration(
            "Methods",
            "Set of methods for which a property is applicable",
            XmlaType.Enumeration,
            XmlaConstants.Method.getDictionary());

    public static final Enumeration ACCESS =
        new Enumeration(
            "Access",
            "The read/write behavior of a property",
            XmlaType.Enumeration,
            XmlaConstants.Access.getDictionary());

    public static final Enumeration AUTHENTICATION_MODE =
        new Enumeration(
            "AuthenticationMode",
            "Specification of what type of security mode the data source "
            + "uses.",
            XmlaType.EnumString,
            XmlaConstants.AuthenticationMode.getDictionary());

    public static final Enumeration PROVIDER_TYPE =
        new Enumeration(
            "ProviderType",
            "The types of data supported by the provider.",
            XmlaType.Array,
            XmlaConstants.ProviderType.getDictionary());

    public Enumeration(
        String name,
        String description,
        XmlaType type,
        XmlaConstant.Dictionary<?> dictionary)
    {
        this.name = name;
        this.description = description;
        this.type = type;
        this.dictionary = dictionary;
    }

    public String getName() {
        return name;
    }

    public List<? extends Enum> getValues() {
        return dictionary.getValues();
    }
}

// End Enumeration.java
