/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.cache;

import java.net.*;
import java.util.*;
import java.util.Map.*;
import java.util.concurrent.*;

import org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache.MODE;
import org.olap4j.driver.xmla.cache.XmlaOlap4jNamedMemoryCache.Property;

/**
 * <p>Thread-safe cache object which supports concurrent access.
 * <p>It keeps it's cache element objects in memory in an internal hash
 * table. Instantiate it and use. As simple as that.
 * @author Luc Boudreau
 * @version $Id$
 */
class XmlaOlap4jConcurrentMemoryCache {

    /**
     * This defines the default cache timeout of 1 minute. The value
     * has to be in seconds.
     */
    private final static int DEFAULT_CACHE_TIMEOUT = 60;




    /**
     * This defines the default cache size of 10.
     */
    private final static int DEFAULT_CACHE_SIZE = 10;





    /**
     * This defines the current eviction mode. Defaults to LFU.
     */
    private final static MODE DEFAULT_EVICTION_MODE = MODE.LFU;


    /**
     * <p>Thread safe hashmap which will be used as a cache.
     *
     * <p>The cache is a map structured as follows :
     *
     * <ul>
     *  <li>key -> String : SHA-1 encoding of the full URL
     */
    private Map<String, XmlaOlap4jCacheElement> cacheEntries =
        new ConcurrentHashMap<String, XmlaOlap4jCacheElement>();


    /**
     * Holds on to the current cache size
     */
    private int cacheSize = DEFAULT_CACHE_SIZE;


    /**
     * Holds on to the current eviction mode
     */
    private MODE evictionMode = DEFAULT_EVICTION_MODE;


    /**
     * Holds on to the current cache timeout
     */
    private int cacheTimeout = DEFAULT_CACHE_TIMEOUT;




    public XmlaOlap4jConcurrentMemoryCache(Map<String,String> props) throws IllegalArgumentException {
        for (Entry<String,String> entry : props.entrySet()) {
            if (Property.Size.name()
                    .equalsIgnoreCase(entry.getKey().toString())) {
                this.setCacheSize(
                        Integer.parseInt(entry.getValue().toString()));
            } else if (Property.Timeout.name()
                    .equalsIgnoreCase(entry.getKey().toString())) {
                this.setCacheTimeout(
                        Integer.parseInt(entry.getValue().toString()));
            } else if (Property.Mode.name()
                    .equalsIgnoreCase(entry.getKey().toString())) {
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
        if (size > 0) {
            this.cacheSize  = size;
        } else {
          throw new IllegalArgumentException(
            "The XMLAOLap4jMemoryCache size cannot be less or equal to 0.");
        }
    }



    /**
     * Sets the number of cached entries.
     * @param size The number of cached entries.
     */
    private void setCacheMode(String mode) {
        if (MODE.valueOf(mode) != null) {
            this.evictionMode = MODE.valueOf(mode);
        } else {
            throw new IllegalArgumentException(
                "The XMLAOLap4jMemoryCache mode has to be one of XmlaOlap4jMemoryCache.MODE");
        }
    }



    /**
     * Sets the cache expiration timeout.
     * @param seconds The number of seconds to hold the entries in cache.
     */
    private void setCacheTimeout(int seconds) {
        if (seconds > 0) {
            this.cacheTimeout = seconds;
        } else {
          throw new IllegalArgumentException(
            "The XMLAOLap4jMemoryCache timeout cannot be less or equal to 0.");
        }
    }


    public byte[] get(final URL url, final byte[] request) {
        // Take the cache for ourself
        synchronized (this.cacheEntries) {
            // Clean expired values
            cleanExpired(false);

            // Extract the data from the cache
            XmlaOlap4jCacheElement entry = this.cacheEntries.get(
                XmlaOlap4jSHAEncoder.SHA1(
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


    public void put(final URL url, final byte[] request,
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
                    XmlaOlap4jSHAEncoder.SHA1(
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
     * @param makeRoom Whether to make room for later appending by
     * evicting an entry based on the selected eviction mode.
     */
    private void cleanExpired(boolean makeRoom) {
        String toBeEvicted = null;

        if (evictionMode == MODE.FIFO || evictionMode == MODE.LIFO) {
            toBeEvicted = timeBasedEviction(makeRoom);
        }
        if (evictionMode == MODE.LFU || evictionMode == MODE.MFU) {
            toBeEvicted = hitBasedEviction(makeRoom);
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
     * @param makeRoom Whether to make room for later appending by
     * evicting an entry. if false is specified, there might not
     * be an evicted entry if the cache is not full.
     * @return The key of the entry to remove, null otherwise.
     */
    private String timeBasedEviction(boolean makeRoom)
    {
        // This is a flag to find the oldest entry.
        long currentEvictedTimestamp = evictionMode == MODE.LIFO
            ? Long.MAX_VALUE
            : Long.MIN_VALUE;

        String toBeEvicted = null;

        // Iterate over entries
        for (Entry<String, XmlaOlap4jCacheElement> entry
                : this.cacheEntries.entrySet())
        {
            // Check if not expired
            if (Calendar.getInstance().getTimeInMillis() >
                (entry.getValue().getTimestamp().longValue() + (cacheTimeout * 1000)))
            {
                // Evicts it.
                this.cacheEntries.remove(entry.getKey());
                continue;
            }

            // Checks if this is the oldest entry
            if (makeRoom &&
                (evictionMode == MODE.LIFO
                  && entry.getValue().getTimestamp().longValue() < currentEvictedTimestamp)
                || (evictionMode == MODE.FIFO
                  && entry.getValue().getTimestamp().longValue() > currentEvictedTimestamp))
            {
                currentEvictedTimestamp = entry.getValue().getTimestamp().longValue();
                toBeEvicted = entry.getKey();
            }
        }
        return toBeEvicted;
    }


    /**
     * Scans for the key of the cache entry to be evicted based
     * on the selected hit based eviction mode.
     * @param makeRoom Whether to make room for later appending by evicting an entry.
     * if false is specified, there might not be an evicted entry if the cache
     * is not full.
     * @return The key of the entry to remove, null otherwise.
     */
    private String hitBasedEviction(boolean makeRoom)
    {
        // Flag to find the oldest entry.
        long currentEvictedHits = (evictionMode == MODE.LFU)
            ? Long.MAX_VALUE
            : Long.MIN_VALUE;

        String toBeEvicted = null;

        // Iterates over entries
        for (Entry<String, XmlaOlap4jCacheElement> entry
            : this.cacheEntries.entrySet())
        {
            // Checks if not expired
            if (Calendar.getInstance().getTimeInMillis() >
                (entry.getValue().getTimestamp().longValue() + (cacheTimeout * 1000)))
            {
                // Evicts it
                this.cacheEntries.remove(entry.getKey());
                continue;
            }

            // Checks if this is the oldest entry
            if (makeRoom &&
              (evictionMode == MODE.LFU
                && entry.getValue().getHitCount().longValue() < currentEvictedHits)
              || (evictionMode == MODE.MFU
                && entry.getValue().getHitCount().longValue() > currentEvictedHits))
            {
                currentEvictedHits = entry.getValue().getHitCount().longValue();
                toBeEvicted = entry.getKey();
            }
        }
        return toBeEvicted;
    }
}

// End XmlaOlap4jConcurrentMemoryCache.java
