/*
// $Id:$
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
package org.olap4j.query;

import org.olap4j.metadata.Dimension;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of a selection.
 * @author LBoudreau
 * @version $Id:$
 */
abstract class AbstractSelection extends QueryNodeImpl implements Selection {

    Operator operator;
    Dimension dimension;
    List<Selection> selectionContext;

    public AbstractSelection(
        Dimension dimension,
        Operator operator)
    {
        this.dimension = dimension;
        this.operator = operator;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        assert operator != null;
        this.operator = operator;
        notifyChange(this,-1);
    }

    void tearDown() {
    }

    public List<Selection> getSelectionContext() {
        return selectionContext;
    }

    public void addContext(Selection selection) {
        if (selectionContext == null) {
            selectionContext = new ArrayList<Selection>();
        }
        selectionContext.add(selection);
    }

    public void removeContext(Selection selection) {
        selectionContext.remove(selection);
    }

    public String getUniqueName() {
        return getRootElement().getUniqueName();
    }
}

// End AbstractSelection.java
