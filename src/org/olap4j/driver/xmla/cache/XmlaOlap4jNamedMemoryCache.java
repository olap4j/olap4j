/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2008-2011 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.cache;

import org.olap4j.impl.Olap4jUtil;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Implementation of the XMLA SOAP cache that places its cache entries
 * in memory for later use. It is thread safe and at static class level.
 *
 * <p>It supports cache sharing through the Name property.
 *
 * <p>All parameters are optional.
 *
 * <ul>
 * <li><b>NAME</b><br />A unique identifier which allows two connections
 * to share a same cache space. Setting this to an already existing cache
 * space will cause the cache manager to ignore other configuration properties,
 * such as eviction mode and so on. Not setting this property will
 * assign a random name to the cache space, thus creating a unique space.</li>
 * <li><b>SIZE</b><br />The number of entries to maintain in cache under
 * the given cache name.</li>
 * <li><b>TIMEOUT</b><br />The number of seconds to maintain entries in
 * cache before expiration.</li>
 * <li><b>MODE</b><br />Supported eviction modes are LIFO (last in first out),
 * FIFO (first in first out), LFU (least frequently used) and MFU
 * (most frequently used)</li>
 * </ul>
 *
 * @see XmlaOlap4jNamedMemoryCache.Property
 * @version $Id$
 */
public class XmlaOlap4jNamedMemoryCache implements XmlaOlap4jCache {

    /**
     * <p>Thread safe hashmap which will be used to keep track of
     * the current caches. The unique ID is the URL.
     */
    private static Map<String, XmlaOlap4jConcurrentMemoryCache> caches = null;

    /**
     * Properties which will be considered for configuration.
     *
     * <p>All parameters are optional.
     */
    public static enum Property {
        /**
         * A unique identifier which allows two connections to share a same
         * cache space. Setting this to an already existing cache
         * space will cause the cache manager to ignore other configuration
         * properties, such as eviction mode and so on. Not setting this
         * property will assign a random name to the cache space, thus creating
         * a unique space.
         */
        NAME("Name of a cache to create or to share."),

        /**
         * The number of entries to maintain in cache under
         * the given cache name.
         */
        SIZE(
            "Maximum number of SOAP requests which will be cached under the "
            + "given cache name."),

        /**
         * The number of seconds to maintain
         * entries in cache before expiration.
         */
        TIMEOUT(
            "Maximum TTL of SOAP requests which will be cached under the given "
            + "cache name."),

        /**
         * Eviction mode. Supported eviction modes are
         * LIFO (last in first out), FIFO (first in first out),
         * LFU (least frequently used) and MFU (most frequently used).
         */
        MODE("Eviction mode to set to the given cache name.");

        /**
         * Creates a property.
         *
         * @param description Description of property
         */
        Property(String description) {
            Olap4jUtil.discard(description);
        }
    }


    /**
     * Defines the supported eviction modes.
     */
    public static enum Mode {
        /** Last-in, first-out. */
        LIFO,
        /** First-in, first-out. */
        FIFO,
        /** Least-frequently used. */
        LFU,
        /** Most-frequently used. */
        MFU
    }


    /**
     * Makes sure that the cache is not accessed before it is configured.
     */
    private boolean initDone = false;


    /**
     * Default constructor which instantiates the concurrent hash map.
     */
    public XmlaOlap4jNamedMemoryCache() {
        XmlaOlap4jNamedMemoryCache.initCaches();
    }


    /**
     * Initializes the caches in a static and thread safe way.
     */
    private static synchronized void initCaches() {
        if (caches == null) {
            caches =
                new ConcurrentHashMap<
                String, XmlaOlap4jConcurrentMemoryCache>();
        }
    }

    // implement XmlaOlap4jCache
    public String setParameters(
        Map<String, String> config,
        Map<String, String> props)
    {
        String refId;

        // Make sure there's a name for the cache. Generate a
        // random one if needed.
        if (props.containsKey(
                XmlaOlap4jNamedMemoryCache.Property.NAME.name()))
        {
            refId = (String) props.get(
                XmlaOlap4jNamedMemoryCache.Property.NAME.name());
        } else {
            refId = String.valueOf(UUID.randomUUID());
            props.put(XmlaOlap4jNamedMemoryCache.Property.NAME.name(), refId);
        }


        // Wait for exclusive access to the caches
        synchronized (caches) {
            // Create a cache for this URL if it is not created yet
            if (!caches.containsKey(
                    props.get(
                        XmlaOlap4jNamedMemoryCache.Property.NAME.name())))
            {
                caches.put(
                    (String) props.get(
                        XmlaOlap4jNamedMemoryCache.Property.NAME.name()),
                    new XmlaOlap4jConcurrentMemoryCache(props));
            }
        }

        // Mark this cache as inited.
        this.initDone = true;

        // Give back the reference id.
        return refId;
    }


    // implement XmlaOlap4jCache
    public byte[] get(
        String id,
        URL url,
        byte[] request)
        throws XmlaOlap4jInvalidStateException
    {
        this.validateState();

        // Wait for exclusive access to the caches
        synchronized (caches) {
            if (caches.containsKey(id)) {
                return caches.get(id).get(url, request);
            } else {
                throw new XmlaOlap4jInvalidStateException();
            }
        }
    }


    // implement XmlaOlap4jCache
    public void put(
        String id,
        URL url,
        byte[] request,
        byte[] response)
        throws XmlaOlap4jInvalidStateException
    {
        this.validateState();

        // Wait for exclusive access to the caches
        synchronized (caches) {
            if (caches.containsKey(id)) {
                caches.get(id).put(url, request, response);
            } else {
                throw new XmlaOlap4jInvalidStateException();
            }
        }
    }

    // implement XmlaOlap4jCache
    public void flushCache() {
        // Wait for exclusive access to the caches
        synchronized (caches) {
            caches.clear();
        }
    }

    /**
     * Helper method to validate that the cache is initialized.
     *
     * @throws XmlaOlap4jInvalidStateException When the cache is not initialized.
     */
    private void validateState() throws XmlaOlap4jInvalidStateException {
        if (!this.initDone) {
            throw new XmlaOlap4jInvalidStateException();
        }
    }
}

// End XmlaOlap4jNamedMemoryCache.java


