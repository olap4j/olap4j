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
package org.olap4j.type;

/**
 * The type of a symbolic expression.
 *
 * <p>Symbols are identifiers which occur in particular function calls,
 * generally to indicate an option for how the function should be executed.
 * They are similar to an enumerated type in other
 * languages.
 *
 * <p>For example, the optional 3rd argument to the <code>Order</code> function
 * can be one of the symbols <code>ASC</code>, <code>DESC</code>,
 * <code>BASC</code>, <code>BDESC</code>. The signature of the
 * <code>Order</code> function is therefore
 *
 * <blockquote>
 * <code>Order(&lt;Set&gt;, &lt;Scalar expression&gt; [, &lt;Symbol&gt;])</code>
 * </blockquote>
 *
 * and
 *
 * <blockquote>
 * <code>Order([Store].Members, [Measures].[Unit Sales], BDESC)</code>
 * </blockquote>
 *
 * would be a valid call to the function.
 *
 * @author jhyde
 * @since Feb 17, 2005
 */
public class SymbolType extends ScalarType {

    /**
     * Creates a symbol type.
     */
    public SymbolType() {
    }

}

// End SymbolType.java
