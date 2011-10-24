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

import org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache.Mode;
import org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache.Property;
import org.olap4j.impl.Olap4jUtil;

import java.net.URL;
import java.util.Calendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe cache object which supports concurrent access.
 *
 * <p>It keeps its cache element objects in memory in an internal hash
 * table. Instantiate it and use. As simple as that.
 *
 * @author Luc Boudreau
 * @version $Id$
 */
class XmlaOlap4jConcurrentMemoryCache {

    /**
     * Default cache timeout (1 minute). The value is in seconds.
     */
    private final static int DEFAULT_CACHE_TIMEOUT = 60;

    /**
     * Default cache size (10).
     */
    private final static int DEFAULT_CACHE_SIZE = 10;

    /**
     * Default eviction mode (LFU).
     */
    private final static Mode DEFAULT_EVICTION_MODE = Mode.LFU;


    /**
     * Thread-safe hashmap which will be used as a cache.
     *
     * <p>The cache is a map structured as follows:
     *
     * <ul>
     * <li>key -> String : SHA-1 encoding of the full URL</li>
     * </ul>
     */
    private Map<String, XmlaOlap4jCacheElement> cacheEntries =
        new ConcurrentHashMap<String, XmlaOlap4jCacheElement>();


    /**
     * Cache size.
     */
    private int cacheSize = DEFAULT_CACHE_SIZE;

    /**
     * Eviction mode.
     */
    private Mode evictionMode = DEFAULT_EVICTION_MODE;

    /**
     * Cache timeout, in seconds.
     */
    private int cacheTimeout = DEFAULT_CACHE_TIMEOUT;

    /**
     * Creates an XmlaOlap4jConcurrentMemoryCache.
     *
     * @param props Properties
     * @throws IllegalArgumentException
     */
    public XmlaOlap4jConcurrentMemoryCache(
        Map<String, String> props)
        throws IllegalArgumentException
    {
        for (Entry<String, String> entry : props.entrySet()) {
            if (Property.SIZE.name().equalsIgnoreCase(
                    entry.getKey().toString()))
            {
                this.setCacheSize(
                    Integer.parseInt(entry.getValue().toString()));
            } else if (Property.TIMEOUT.name().equalsIgnoreCase(
                    entry.getKey().toString()))
            {
                this.setCacheTimeout(
                    Integer.parseInt(entry.getValue().toString()));
            } else if (Property.MODE.name().equalsIgnoreCase(
                    entry.getKey().toString()))
            {
                this.setCacheMode(
                    entry.getValue().toString());
            }
        }
    }

    /**
     * Sets the number of cached entries.
     * @param size The number of cached entries.
     */
    private void setCacheSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException(
                "Cache size must be positive, but was " + size);
        }
        this.cacheSize  = size;
    }

    /**
     * Sets the eviction mode.
     *
     * @param mode Eviction mode
     */
    private void setCacheMode(String mode) {
        if (Mode.valueOf(mode) == null) {
            throw new IllegalArgumentException(
                "The XmlaOlap4jMemoryCache mode has to be one of "
                + Mode.class.getName());
        }
        this.evictionMode = Mode.valueOf(mode);
    }

    /**
     * Sets the cache expiration timeout.
     *
     * @param seconds The number of seconds to hold the entries in cache.
     */
    private void setCacheTimeout(int seconds) {
        if (seconds <= 0) {
            throw new IllegalArgumentException(
                "Cache timeout must be positive, but was " + seconds);
        }
        this.cacheTimeout = seconds;
    }

    byte[] get(
        final URL url,
        final byte[] request)
    {
        // Take the cache for ourself
        synchronized (this.cacheEntries) {
            // Clean expired values
            cleanExpired(false);

            // Extract the data from the cache
            XmlaOlap4jCacheElement entry = this.cacheEntries.get(
                XmlaOlap4jShaEncoder.encodeSha1(
                    url.toExternalForm() + new String(request)));

            // Increment its counter
            if (entry != null) {
                entry.incrementHitCount();
                entry.refreshTimestamp();
            }

            // Return a copy to prevent corruption
            return entry != null
                ? new String(entry.getResponse()).getBytes()
                : null;
        }
    }

    void put(
        final URL url,
        final byte[] request,
        final byte[] response)
    {
        // Take the cache for ourself
        synchronized (this.cacheEntries) {
            // Make some cleanup
            cleanExpired(true);

            if (this.cacheEntries.size() < cacheSize) {
                // Create the entry
                XmlaOlap4jCacheElement entry = new XmlaOlap4jCacheElement();
                entry.setResponse(response);

                this.cacheEntries.put(
                    XmlaOlap4jShaEncoder.encodeSha1(
                        String.valueOf(url.toExternalForm())
                        + new String(request)),
                        entry);
            } else {
                throw new RuntimeException("Concurrency error detected.");
            }
        }
    }


    /**
     * Cleans expired cache entries.
     *
     * @param makeRoom Whether to make room for later appending by
     * evicting an entry based on the selected eviction mode.
     */
    private void cleanExpired(boolean makeRoom) {
        final String toBeEvicted;
        switch (evictionMode) {
        case FIFO:
        case LIFO:
            toBeEvicted = timeBasedEviction(makeRoom);
            break;
        case LFU:
        case MFU:
            toBeEvicted = hitBasedEviction(makeRoom);
            break;
        default:
            throw Olap4jUtil.unexpected(evictionMode);
        }

        // Make some space if required
        if (makeRoom && this.cacheEntries.size() >= cacheSize
            && toBeEvicted != null)
        {
            this.cacheEntries.remove(toBeEvicted);
        }
    }

    /**
     * Scans for the key of the cache entry to be evicted based
     * on the selected time based eviction mode.
     *
     * @param makeRoom Whether to make room for later appending by
     * evicting an entry. if false is specified, there might not
     * be an evicted entry if the cache is not full.
     * @return The key of the entry to remove, null otherwise.
     */
    private String timeBasedEviction(boolean makeRoom)
    {
        // This is a flag to find the oldest entry.
        long currentEvictedTimestamp = evictionMode == Mode.LIFO
            ? Long.MAX_VALUE
            : Long.MIN_VALUE;

        String toBeEvicted = null;

        // Iterate over entries
        for (Entry<String, XmlaOlap4jCacheElement> entry
                : this.cacheEntries.entrySet())
        {
            // Check if not expired
            if (Calendar.getInstance().getTimeInMillis() >
                (entry.getValue().getTimestamp().longValue()
                    + (cacheTimeout * 1000)))
            {
                // Evicts it.
                this.cacheEntries.remove(entry.getKey());
                continue;
            }

            // Checks if this is the oldest entry.
            if ((makeRoom
                 && (evictionMode == XmlaOlap4jNamedMemoryCache.Mode.LIFO
                    && entry.getValue().getTimestamp().longValue()
                    < currentEvictedTimestamp))
                || (makeRoom
                    && (evictionMode == XmlaOlap4jNamedMemoryCache.Mode.FIFO
                    && entry.getValue().getTimestamp().longValue()
                    > currentEvictedTimestamp)))
            {
                currentEvictedTimestamp =
                    entry.getValue().getTimestamp().longValue();
                toBeEvicted = entry.getKey();
            }
        }
        return toBeEvicted;
    }


    /**
     * Scans for the key of the cache entry to be evicted based
     * on the selected hit based eviction mode.
     *
     * @param makeRoom Whether to make room for later appending by evicting an
     * entry. If false, there might not be an evicted entry if the cache is not
     * full
     *
     * @return The key of the entry to remove, null otherwise.
     */
    private String hitBasedEviction(boolean makeRoom)
    {
        // Flag to find the oldest entry.
        long currentEvictedHits = (evictionMode == Mode.LFU)
            ? Long.MAX_VALUE
            : Long.MIN_VALUE;

        String toBeEvicted = null;

        // Iterates over entries
        for (Entry<String, XmlaOlap4jCacheElement> entry
            : this.cacheEntries.entrySet())
        {
            // Checks if not expired
            if (Calendar.getInstance().getTimeInMillis() >
                (entry.getValue().getTimestamp().longValue()
                    + (cacheTimeout * 1000)))
            {
                // Evicts it
                this.cacheEntries.remove(entry.getKey());
                continue;
            }

            // Checks if this is the oldest entry.
            if ((makeRoom
                 && (evictionMode == Mode.LFU
                    && entry.getValue().getHitCount().longValue()
                    < currentEvictedHits))
                || (makeRoom
                    && (evictionMode == XmlaOlap4jNamedMemoryCache.Mode.MFU
                    && entry.getValue().getHitCount().longValue()
                    > currentEvictedHits)))
            {
                currentEvictedHits = entry.getValue().getHitCount().longValue();
                toBeEvicted = entry.getKey();
            }
        }
        return toBeEvicted;
    }
}

// End XmlaOlap4jConcurrentMemoryCache.java



