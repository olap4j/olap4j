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

import java.util.List;

/**
 * XML for Analysis entity representing a Keyword.
 *
 * <p>Corresponds to the XML/A {@code DISCOVER_KEYWORDS} schema rowset.</p>
 */
public class XmlaKeyword extends Entity {
    public static final XmlaKeyword INSTANCE =
        new XmlaKeyword();

    public RowsetDefinition def() {
        return RowsetDefinition.DISCOVER_KEYWORDS;
    }

    List<Column> columns() {
        return list(Keyword);
    }

    List<Column> sortColumns() {
        return list(); // not sorted
    }

    public final Column Keyword =
        new Column(
            "Keyword",
            XmlaType.StringSometimesArray.scalar(),
            Column.RESTRICTION,
            Column.REQUIRED,
            "A list of all the keywords reserved by a provider.\n"
            + "Example: AND");
}

// End XmlaKeyword.java
