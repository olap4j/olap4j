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
package org.olap4j.impl;

/**
 * Holder for constants which indicate whether particular issues have been
 * fixed. Reference one of those constants in your code, and it is clear which
 * code can be enabled when the bug is fixed. Generally a constant is removed
 * when its bug is fixed.
 *
 * <p>Developers, please use the naming format
 * <code>Bug&lt;Product&gt;&lt;Number&gt;Fixed</code> for constants, and include
 * a hyperlink to the bug database record in the comments. Product will usually
 * be "Olap4j", but sometimes we are blocked by bugs in other components, such
 * as the JDK or Mondrian.
 *
 * <h3>Cleanup items</h3>
 *
 * The following is a list of cleanup items. They are not bugs per se:
 * functionality is not wrong, just the organization of the code. If they were
 * bugs, they would be in jira. It makes sense to have the list here, so that
 * referenced class, method and variable names show up as uses in code searches.
 *
 * <dl>
 *
 * <dt>Split out Test Compatability Kit (TCK)</dt>
 * <dd>Factor parts of olap4j test suite that depend on an olap4j implementation
 * (such as mondrian) into a jar that can be invoked by that implementation.
 * Then each implementation is responsible for running the TCK. It can also
 * manage which version of the TCK it implements, so that it does not have to
 * be absolutely up to date. Some parts of the olap4j suite do not stretch the
 * capabilities of the OLAP engine or driver, and will remain part of
 * the olap4j suite, using mondrian as reference implementation.</dd>
 *
 * </dl>
 *
 * @author jhyde
 * @since Nov 9, 2010
 */
public abstract class Bug {
    /**
     * Whether
     * <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=3106220&group_id=168953&atid=848534">bug 3106220,
     * "Complex selection context broken"</a>
     * is fixed.
     */
    public static final boolean BugOlap4j3106220Fixed = false;

    /**
     * Whether
     * <a href="http://sourceforge.net/tracker/index.php?func=detail&aid=3126853&group_id=168953&atid=848534">bug 3126853,
     * "ConnectionTest.testCellSetBug hangs"</a>
     * is fixed.
     */
    public static final boolean BugOlap4j3126853Fixed = false;

}

// End Bug.java
