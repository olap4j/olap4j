/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.proxy;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;

/**
 * Defines a common set of methods for proxy objects.
 * @version $Id$
 */
public interface XmlaOlap4jProxy {
    /**
     * Sends a request to a URL and returns the response.
     *
     * @param url Target URL
     * @param request Request string
     * @return Response The byte array that contains the whole response
     * from the server.
     * @throws IOException This exception declaration will be removed soon.
     * Don't catch this. Catch XmlaOlap4jProxyException instead.
     * @throws XmlaOlap4jProxyException If anything occurs during the
     * request execution.
     */
     /*
      * FIXME We will need to remove the IOException declaration because
      * this type of error is linked to the proxy type. A wrapper
      * class was created, but some proxies out there (MondrianInprocProxy...)
      * still uses this.
      */
    byte[] get(
        URL url,
        String request)
        throws XmlaOlap4jProxyException, IOException;

    /**
     * Submits a request for background execution.
     *
     * @param url URL
     * @param request Request
     * @return Future object representing the submitted job
     */
    Future<byte[]> submit(
        URL url,
        String request);

    /**
     * Returns the name of the character set use for encoding the XML
     * string.
     */
    String getEncodingCharsetName();
}

// End XmlaOlap4jProxy.java
