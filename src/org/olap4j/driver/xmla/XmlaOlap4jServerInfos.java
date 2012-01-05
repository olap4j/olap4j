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
package org.olap4j.driver.xmla;

import java.net.URL;

/**
 * Common set of functions for an XMLA URL Provider.
 * @version $Id$
 */
public interface XmlaOlap4jServerInfos {
    /**
     * Returns the URL to use.
     * @return the url.
     */
    URL getUrl();
    /**
     * Returns the username to use with the URL.
     * @return the username.
     */
    String getUsername();
    /**
     * Returns the password to use with the URL.
     * @return the password.
     */
    String getPassword();
    /**
     * Returns a unique sesison ID to use.
     * @return the session id.
     */
    String getSessionId();
    /**
     * Stores the session id on the server.
     * @param sessionId The session id to use.
     */
    void setSessionId(String sessionId);
}
// End XmlaOlap4jServerInfos.java
