/*
// $Id:$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2011 Julian Hyde and others.
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla;

import java.net.URL;

/**
 * Common set of functions for an XMLA URL Provider.
 * @version $Id:$
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