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
package org.olap4j.driver.xmla.cache;

import org.olap4j.OlapException;

/**
 * <p>Internal exception which gets thrown when operations to the cache
 * are performed but it hasn't been initialized.
 *
 * @author Luc Boudreau
 */
public class XmlaOlap4jInvalidStateException extends OlapException {
    private static final long serialVersionUID = 7265273715459263740L;
}

// End XmlaOlap4jInvalidStateException.java
