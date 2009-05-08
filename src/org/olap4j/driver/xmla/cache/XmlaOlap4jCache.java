/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2008-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.cache;

import java.net.URL;
import java.util.Map;

/**
 * XMLA driver cache. Implementations will have to declare those methods.
 *
 * <p>The XMLA driver will call the cache before each SOAP request to see
 * if it wasn't sent previously and if a SOAP response doesn't already
 * exist in it.
 *
 * <p>Any implementations have to declare a constructor which takes a String
 * as a parameter. This string value is the unique name of the connection
 * which triggered the request.
 *
 * @author Luc Boudreau
 * @version $Id$
 */
public interface XmlaOlap4jCache {

    /**
     * Fetches a SOAP response from the cache. Returns null
     * if there are no cached response corresponding to the SOAP
     * message and the URL.
     *
     * @param id The connection unique name which called this cache.
     * @param url The URL where the SOAP message was sent.
     * @param request The SOAP complete message.
     *
     * @throws XmlaOlap4jInvalidStateException when
     * operations to the cache are performed but it hasn't been initialized.
     * Make sure you call the setParameters method.
     *
     * @return The SOAP response, null if there are no corresponding
     * response in the cache.
     */
    public byte[] get(
        String id,
        URL url,
        byte[] request)
        throws XmlaOlap4jInvalidStateException;

    /**
     * Adds a SOAP response to the cache. It has to be relative to the
     * URL of the SOAP service.
     *
     * @param id The connection unique name which called this cache.
     * @param url The URL of the SOAP endpoint.
     * @param request The full SOAP message from which we want to cache its
     * response.
     * @param response The response to cache.
     *
     * @throws XmlaOlap4jInvalidStateException when
     * operations to the cache are performed but it hasn't been initialized.
     * Make sure you call the setParameters method.
     */
    public void put(
        String id,
        URL url,
        byte[] request,
        byte[] response)
        throws XmlaOlap4jInvalidStateException;

    /**
     * Tells the cache to flush all cached entries.
     */
    public void flushCache();

    /**
     * Convenience method to receive custom properties.
     *
     * <p>The XMLA driver takes cache properties as
     * "<code>Cache.[property name]=[value]</code>" in its JDBC url. All those
     * properties should be striped of their "<code>Cache.</code>" prefix and
     * sent to this method as the properties parameter.
     *
     * <p>Also, the complete config map of the current connection
     * should be passed as the config parameter.
     *
     * @param config The complete configuration parameters which were used to
     * create the current connection.
     * @param props The properties received from the JDBC url.
     * @return Returns a string object which gives a reference id to the
     * caller for future use. This id has to be passed along with any future
     * get and put requests.
     */
    public String setParameters(
        Map<String, String> config,
        Map<String, String> props);
}

// End XmlaOlap4jCache.java
