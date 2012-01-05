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

import org.olap4j.OlapException;
import org.olap4j.driver.xmla.*;
import org.olap4j.driver.xmla.cache.XmlaOlap4jCache;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * <p>Abstract implementation of Proxy which adds a SOAP
 * cache layer between the driver and its proxy implementations.
 * It can be configured via the setCache() method, as instructed in
 * {@link XmlaOlap4jCachedProxy} interface.
 *
 * <p>It also offers helper methods to keep track of
 * the HTTP cookies and sends them back
 * to the server along with queries. The useful methods are
 * saveCookies(URL) and useCookies(URL).
 *
 * @author Luc Boudreau
 * @version $Id$
 */
abstract class XmlaOlap4jAbstractHttpProxy
    implements XmlaOlap4jCachedProxy
{
    private final XmlaHelper helper = new XmlaHelper();

    /**
     * Holds on to the cache implementation.
     */
    private XmlaOlap4jCache cache = null;


    /**
     * Holds on to the connection name which is associated to this proxy.
     */
    private String cacheId;


    /**
     * Keeps a link to the cookie manager instance.
     */
    private XmlaOlap4jCookieManager cookieManager = null;

    /**
     * Creates an XmlaOlap4jAbstractHttpProxy.
     */
    protected XmlaOlap4jAbstractHttpProxy() {
    }

    /**
     * Sends a request to a URL and returns the response.
     *
     * @param url Target URL
     * @param request Request string
     * @return Response
     */
    public abstract byte[] getResponse(
        XmlaOlap4jServerInfos serverInfos,
        String request)
            throws XmlaOlap4jProxyException;


    /**
     * Submits a request for background execution.
     *
     * @param url URL
     * @param request Request
     * @return Future object representing the submitted job
     */
    public abstract Future<byte[]> getResponseViaSubmit(
        XmlaOlap4jServerInfos serverInfos,
        String request);

    /**
     * Helper method to add cookies to a given connection.
     * @param urlConn The url connection to which we want the cookies
     * applied to.
     */
    protected void useCookies(URLConnection urlConn) {
        // Initializes the cookie manager
        this.initCookieManager();
        // Saves the current cookies
        this.cookieManager.setCookies(urlConn);
    }

    /**
     * Helper method to save cookies for later use.
     * @param urlConn The url connection for which we want the cookies
     * saved for later use.
     */
    protected void saveCookies(URLConnection urlConn) {
        // Initializes the cookie manager
        this.initCookieManager();
        // Saves the current cookies
        this.cookieManager.storeCookies(urlConn);
    }

    @SuppressWarnings("unchecked")
    public void setCache(
        Map<String, String> config,
        Map<String, String> properties)
        throws OlapException
    {
        try {
            // Loads the cache class
            Class clazz = Class.forName(config.get(
                XmlaOlap4jDriver.Property.CACHE.name()));

            // Instantiates it
            this.cache = (XmlaOlap4jCache) clazz.newInstance();

            // Configures it
            this.cacheId = this.cache.setParameters(config, properties);
        } catch (ClassNotFoundException e) {
            throw helper.createException(
                "The specified cache class name could not be found : "
                + config.get(XmlaOlap4jDriver.Property.CACHE.name()), e);
        } catch (InstantiationException e) {
            throw helper.createException(
                "The specified cache class name could not be instanciated : "
                + config.get(XmlaOlap4jDriver.Property.CACHE.name()), e);
        } catch (IllegalAccessException e) {
            throw helper.createException(
                "An error was encountered while instanciating the cache : "
                + config.get(XmlaOlap4jDriver.Property.CACHE.name()), e);
        } catch (IllegalArgumentException e) {
            throw helper.createException(
                "An error was encountered while instanciating the cache : "
                + config.get(XmlaOlap4jDriver.Property.CACHE.name()), e);
        } catch (SecurityException e) {
            throw helper.createException(
                "An error was encountered while instanciating the cache : "
                + config.get(XmlaOlap4jDriver.Property.CACHE.name()), e);
        }
    }

    // implement XmlaOlap4jProxy
    public byte[] get(
        XmlaOlap4jServerInfos serverInfos,
        String request)
        throws XmlaOlap4jProxyException
    {
        byte[] response = null;
        // Tries to fetch from cache
        try {
            response =
                getFromCache(
                    serverInfos.getUrl(),
                    request.getBytes(getEncodingCharsetName()));
            // Returns the cached value if found
            if (response != null) {
                return response;
            }
        } catch (Exception e) {
            throw new XmlaOlap4jProxyException(
                "An exception was encountered while browsing the proxy cache.",
                e);
        }

        // Executes the query
        response = getResponse(serverInfos, request);

        try {
            // Adds to cache
            addToCache(
                serverInfos.getUrl(),
                request.getBytes(getEncodingCharsetName()),
                response);
            // Returns result
            return response;
        } catch (Exception e) {
            throw new XmlaOlap4jProxyException(
                "An exception was encountered while saving a response in the proxy cache.",
                e);
        }
    }


    /**
     * Tries to fetch a cached response from the cache implementation.
     *
     * @param url The url used to send the request
     *
     * @param request The SOAP request to cache
     *
     * @return either a response in a byte array or null
     * if the response is not in cache
     */
    private byte[] getFromCache(final URL url, final byte[] request)
            throws OlapException
    {
        return (this.cache != null)
            ? this.cache.get(this.cacheId, url, request)
            : null;
    }


    /**
     * Caches an entry using the current cache implementation.
     * @param url The URL from which originated the request
     * @param request The SOAP request to cache
     * @param response The SOAP response to cache
     */
    private void addToCache(URL url, byte[] request, byte[] response)
            throws OlapException
    {
        if (this.cache != null) {
            this.cache.put(this.cacheId, url, request, response);
        }
    }

    // implement XmlaOlap4jProxy
    public Future<byte[]> submit(
        final XmlaOlap4jServerInfos serverInfos,
        final String request)
    {
        // The submit operation doesn't need to be cached yet, since it will
        // call the get operation to fetch the data later on. It will get cached
        // then.
        //
        // I still overridden the submit method in case we need some caching
        // done in the end. - Luc
        return getResponseViaSubmit(serverInfos, request);
    }

    /**
     * Initializes the cookie manager. It is not initialized
     * by default because some proxy implementation might not need this
     * functionnality.
     */
    private void initCookieManager() {
        if (this.cookieManager == null) {
            this.cookieManager = new XmlaOlap4jCookieManager();
        }
    }
}

// End XmlaOlap4jAbstractHttpProxy.java



