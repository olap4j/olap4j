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
 * <li><b>NAME</b><br>A unique identifier which allows two connections
 * to share a same cache space. Setting this to an already existing cache
 * space will cause the cache manager to ignore other configuration properties,
 * such as eviction mode and so on. Not setting this property will
 * assign a random name to the cache space, thus creating a unique space.</li>
 * <li><b>SIZE</b><br>The number of entries to maintain in cache under
 * the given cache name.</li>
 * <li><b>TIMEOUT</b><br>The number of seconds to maintain entries in
 * cache before expiration.</li>
 * <li><b>MODE</b><br>Supported eviction modes are LIFO (last in first out),
 * FIFO (first in first out), LFU (least frequently used) and MFU
 * (most frequently used)</li>
 * </ul>
 *
 * @see XmlaOlap4jNamedMemoryCache.Property
 */
public class XmlaOlap4jNamedMemoryCache implements XmlaOlap4jCache {

    /**
     * <p>Thread safe hash map which will be used to keep track of
     * the current caches. The unique ID is the URL.
     */
    private static final Map<String, XmlaOlap4jConcurrentMemoryCache> CACHES =
        new ConcurrentHashMap<String, XmlaOlap4jConcurrentMemoryCache>();

    /**
     * Properties which will be considered for configuration.
     *
     * <p>All parameters are optional.
     */
    public enum Property {
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
    public enum Mode {
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
    }

    public String setParameters(
        Map<String, String> config,
        Map<String, String> props)
    {
        String refId;

        // Make sure there's a name for the cache. Generate a
        // random one if needed.
        if (props.containsKey(Property.NAME.name())) {
            refId = props.get(Property.NAME.name());
        } else {
            refId = String.valueOf(UUID.randomUUID());
            props.put(Property.NAME.name(), refId);
        }


        // Wait for exclusive access to the caches
        synchronized (CACHES) {
            // Create a cache for this URL if it is not created yet
            if (!CACHES.containsKey(props.get(Property.NAME.name()))) {
                CACHES.put(
                    props.get(Property.NAME.name()),
                    new XmlaOlap4jConcurrentMemoryCache(props));
            }
        }

        // Mark this cache as initialized.
        this.initDone = true;

        // Give back the reference id.
        return refId;
    }


    public byte[] get(
        String id,
        URL url,
        byte[] request)
        throws XmlaOlap4jInvalidStateException
    {
        this.validateState();

        // Wait for exclusive access to the caches
        synchronized (CACHES) {
            if (CACHES.containsKey(id)) {
                return CACHES.get(id).get(url, request);
            } else {
                throw new XmlaOlap4jInvalidStateException();
            }
        }
    }


    public void put(
        String id,
        URL url,
        byte[] request,
        byte[] response)
        throws XmlaOlap4jInvalidStateException
    {
        this.validateState();

        // Wait for exclusive access to the caches
        synchronized (CACHES) {
            if (CACHES.containsKey(id)) {
                CACHES.get(id).put(url, request, response);
            } else {
                throw new XmlaOlap4jInvalidStateException();
            }
        }
    }

    public void flushCache() {
        // Wait for exclusive access to the caches
        synchronized (CACHES) {
            CACHES.clear();
        }
    }

    /**
     * Helper method to validate that the cache is initialized.
     *
     * @throws XmlaOlap4jInvalidStateException When the cache is not initialized
     */
    private void validateState() throws XmlaOlap4jInvalidStateException {
        if (!this.initDone) {
            throw new XmlaOlap4jInvalidStateException();
        }
    }
}

// End XmlaOlap4jNamedMemoryCache.java


