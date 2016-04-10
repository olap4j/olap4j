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
package org.olap4j;

import org.olap4j.test.TestContext;

import junit.framework.TestSuite;

import java.util.Properties;

/**
 * Suite containing all of the the olap4j TCK.
 */
public class Olap4jTckSuite  {
    private Olap4jTckSuite() {}

    /** Entry point for Junit 3.8, and Surefire plugin. */
    public static TestSuite suite() {
        final Properties properties = TestContext.getStaticTestProperties();
        return TestContext.createTckSuite(properties, "default");
    }
}

// End Olap4jTckSuite.java
