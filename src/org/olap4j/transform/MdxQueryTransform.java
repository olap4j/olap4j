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
package org.olap4j.transform;

import org.olap4j.mdx.SelectNode;

/**
 * MDX Query Transformation
 *
 * <p>General interface for transforming an MDX query to another one,
 * according to behavior and parameters encapsulated in implementing
 * classes
 *
 * @author etdub
 * @author jhyde
 * @version $Id$
 * @since Jul 28, 2008
 */
public interface MdxQueryTransform {
    String getName();
    String getDescription();
    SelectNode apply(SelectNode sn);
}

// End MdxQueryTransform.java
