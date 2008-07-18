/*
// $Id: Proxy.java 92 2008-07-17 07:41:10Z lucboudreau $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.proxy;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Future;

/**
 * Defines a common set of methods for proxy objects.
 * @version $Id: Proxy.java 92 2008-07-17 07:41:10Z lucboudreau $
 */
public interface XmlaOlap4jProxy {
    /**
     * Sends a request to a URL and returns the response.
     *
     * @param url Target URL
     * @param request Request string
     * @return Response
     * @throws IOException
     */
    byte[] get(URL url, String request) throws IOException;

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