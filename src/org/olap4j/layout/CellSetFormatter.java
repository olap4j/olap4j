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
package org.olap4j.layout;

import org.olap4j.CellSet;

import java.io.PrintWriter;

/**
 * Converts a {@link CellSet} into text.
 *
 * <p><b>This interface is experimental. It is not part of the olap4j
 * specification and is subject to change without notice.</b></p>
 *
 * @author jhyde
 * @version $Id$
 * @since Apr 15, 2009
 */
public interface CellSetFormatter {
    /**
     * Formats a CellSet as text to a PrintWriter.
     *
     * @param cellSet Cell set
     * @param pw Print writer
     */
    void format(
        CellSet cellSet,
        PrintWriter pw);
}

// End CellSetFormatter.java
