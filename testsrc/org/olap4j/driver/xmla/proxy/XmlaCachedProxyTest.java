/*
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2008-2009 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.proxy;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.olap4j.OlapException;
import org.olap4j.driver.xmla.XmlaOlap4jDriver;
import org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache;

/**
 * <p>Tests both the CachedProxy implementation (HttpProxy) and the
 * IXmlaOlap4jCache implementation (XmlaOlap4jNamedMemoryCache).
 *
 * @author Luc Boudreau
 * @version $Id$
 */
public class XmlaCachedProxyTest extends TestCase {
    final XmlaOlap4jDriver dummyDriver = new XmlaOlap4jDriver() {};

    /**
     * <p>Tests if a simple and valid configuration can be used.
     * @throws Exception If the test fails.
     */
    public void testCacheConfig() throws Exception
    {
        XmlaOlap4jCachedProxy proxy = new XmlaOlap4jHttpProxy(dummyDriver);
        Map<String, String> driverParameters = new HashMap<String, String>();
        Map<String, String> cacheProperties = new HashMap<String, String>();

        driverParameters.put(
            XmlaOlap4jDriver.Property.Server.name(),
            "http://example.com");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Catalog.name(),
            "CatalogName");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Cache.name(),
            "org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Name.name(),
            "testCacheConfig");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Mode.name(),
            "LFU");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
            "30");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Size.name(),
            "50");

        proxy.setCache(driverParameters, cacheProperties);
    }


    /**
     * <p>Makes sure that the cache mode value is validated.
     * @throws Exception If the test fails.
     */
    public void testCacheModeError() throws Exception {
        XmlaOlap4jCachedProxy proxy = new XmlaOlap4jHttpProxy(dummyDriver);
        Map<String, String> driverParameters = new HashMap<String, String>();
        Map<String, String> cacheProperties = new HashMap<String, String>();

        driverParameters.put(
            XmlaOlap4jDriver.Property.Server.name(),
            "http://example.com");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Catalog.name(),
            "CatalogName");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Cache.name(),
            "org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Name.name(),
            "testCacheModeError");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Mode.name(),
            "ERRONOUS VALUE MWAHAHAHAHA");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
            "30");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Size.name(),
            "50");

        try {
            proxy.setCache(driverParameters, cacheProperties);
        } catch (OlapException e) {
            return;
        }

        fail("The cache mode is not validated properly");
    }



    /**
     * <p>Makes sure that the cache timeout value is validated.
     * @throws Exception If the test fails.
     */
    public void testCacheTimeoutError() throws Exception
    {
        XmlaOlap4jCachedProxy proxy = new XmlaOlap4jHttpProxy(dummyDriver);
        Map<String, String> driverParameters = new HashMap<String, String>();
        Map<String, String> cacheProperties = new HashMap<String, String>();

        driverParameters.put(
            XmlaOlap4jDriver.Property.Server.name(),
            "http://example.com");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Catalog.name(),
            "CatalogName");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Cache.name(),
            "org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Name.name(),
            "testCacheTimeoutError");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Mode.name(),
            "LFU");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
            "EEE");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Size.name(),
            "50");

        try {
            proxy.setCache(driverParameters, cacheProperties);
        } catch (OlapException t) {
            try {
                cacheProperties.put(
                    XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
                    "-30");
                proxy.setCache(driverParameters, cacheProperties);
            } catch (OlapException t2) {
                return;
            }
        }

        fail("The cache timeout is not validated properly");
    }



    /**
     * <p>Makes sure that the cache size value is validated.
     * @throws Exception If the test fails.
     */
    public void testCacheSizeError() throws Exception
    {
        XmlaOlap4jCachedProxy proxy = new XmlaOlap4jHttpProxy(dummyDriver);
        Map<String, String> driverParameters = new HashMap<String, String>();
        Map<String, String> cacheProperties = new HashMap<String, String>();

        driverParameters.put(
            XmlaOlap4jDriver.Property.Server.name(),
            "http://example.com");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Catalog.name(),
            "CatalogName");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Cache.name(),
            "org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Name.name(),
            "testCacheSizeError");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Mode.name(),
            "LFU");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
            "600");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Size.name(),
            "EEE");

        try {
            proxy.setCache(driverParameters, cacheProperties);
        } catch (OlapException t) {
            try {
                cacheProperties.put(
                        XmlaOlap4jNamedMemoryCache.Property.Size.name(),
                        "-30");
                proxy.setCache(driverParameters, cacheProperties);
            } catch (OlapException t2) {
                return;
            }
        }

        fail("The cache size is not validated properly");
    }


    /**
     * <p>Makes sure that the cache class name value is validated.
     * @throws Exception If the test fails.
     */
    public void testCacheNameError() throws Exception
    {
        XmlaOlap4jCachedProxy proxy = new XmlaOlap4jHttpProxy(dummyDriver);
        Map<String, String> driverParameters = new HashMap<String, String>();
        Map<String, String> cacheProperties = new HashMap<String, String>();

        driverParameters.put(
            XmlaOlap4jDriver.Property.Server.name(),
            "http://example.com");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Catalog.name(),
            "CatalogName");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Cache.name(),
            "Class which doesn't exist");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Name.name(),
            "testCacheNameError");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Mode.name(),
            "LFU");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
            "600");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Size.name(),
            "50");

        try {
            proxy.setCache(driverParameters, cacheProperties);
        } catch (OlapException e) {
            return;
        }

        fail("The cache class name is not validated properly");
    }




    /**
     * <p>Makes sure that a cache name is properly shared in a static
     * way and that the parameters are not overwritten by subsequent
     * connection creations.
     *
     * @throws Exception If the test fails.
     */
    public void testCacheSharing() throws Exception
    {
        XmlaOlap4jCachedProxy proxy = new XmlaOlap4jHttpProxy(dummyDriver);
        Map<String, String> driverParameters = new HashMap<String, String>();
        Map<String, String> cacheProperties = new HashMap<String, String>();

        driverParameters.put(
            XmlaOlap4jDriver.Property.Server.name(),
            "http://example.com");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Catalog.name(),
            "CatalogName");
        driverParameters.put(
            XmlaOlap4jDriver.Property.Cache.name(),
            "org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Name.name(),
            "testCacheSharing");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Mode.name(),
            "LFU");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
            "600");
        cacheProperties.put(
            XmlaOlap4jNamedMemoryCache.Property.Size.name(),
            "50");

        try {
            proxy.setCache(driverParameters, cacheProperties);
        } catch (Throwable e) {
            fail("The cache class name is not validated properly");
        }

        driverParameters.put(
                XmlaOlap4jDriver.Property.Server.name(),
                "http://example2.com");
        driverParameters.put(
                XmlaOlap4jDriver.Property.Catalog.name(),
                "CatalogName2");
        driverParameters.put(
                XmlaOlap4jDriver.Property.Cache.name(),
                "org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache");
        cacheProperties.put(
                XmlaOlap4jNamedMemoryCache.Property.Name.name(),
                "testCacheSharing");
        cacheProperties.put(
                XmlaOlap4jNamedMemoryCache.Property.Mode.name(),
                "Erronous value which won't trigger an exception since a shared cache should be used.");
        cacheProperties.put(
                XmlaOlap4jNamedMemoryCache.Property.Timeout.name(),
                "Erronous value which won't trigger an exception since a shared cache should be used.");
        cacheProperties.put(
                XmlaOlap4jNamedMemoryCache.Property.Size.name(),
                "Erronous value which won't trigger an exception since a shared cache should be used.");

        try {
            // Create a new object and try with a faulty cache parameters,
            // but use a name which already exists in the cache directory.
            // This endures that 1 - the caches are shared in a static manner
            // and that 2 - the cache is reused and it's
            // parameters are not overwritten.
            proxy = new XmlaOlap4jHttpProxy(dummyDriver);
            proxy.setCache(driverParameters, cacheProperties);
        } catch (Throwable e) {
            fail(
                "The cache is not properly shared since an error should not "
                + "have been thrown.");
        }
    }


}

// End XmlaCachedProxyTest.java
