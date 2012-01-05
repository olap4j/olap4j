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
package org.olap4j.driver.xmla.proxy;

import org.olap4j.driver.xmla.XmlaOlap4jServerInfos;

import java.util.concurrent.Future;

/**
 * Defines a common set of methods for proxy objects.
 * @version $Id$
 */
public interface XmlaOlap4jProxy {
    /**
     * Sends a request to a URL and returns the response.
     *
     * @param serverInfos Server infos.
     * @param request Request string
     * @return Response The byte array that contains the whole response
     * from the server.
     * @throws XmlaOlap4jProxyException If anything occurs during the
     * request execution.
     */
    byte[] get(
        XmlaOlap4jServerInfos serverInfos,
        String request)
            throws XmlaOlap4jProxyException;

    /**
     * Submits a request for background execution.
     *
     * @param serverInfos Server infos.
     * @param request Request
     * @return Future object representing the submitted job
     */
    Future<byte[]> submit(
        XmlaOlap4jServerInfos serverInfos,
        String request);

    /**
     * Returns the name of the character set use for encoding the XML
     * string.
     */
    String getEncodingCharsetName();
}

// End XmlaOlap4jProxy.java
