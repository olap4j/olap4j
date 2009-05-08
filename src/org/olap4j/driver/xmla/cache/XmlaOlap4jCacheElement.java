/*
// $Id$
// This software is subject to the terms of the Eclipse Public License v1.0
// Agreement, available at the following URL:
// http://www.eclipse.org/legal/epl-v10.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.cache;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Internal POJO class definition for the XmlaOlap4jMemoryCache.
 * @author Luc Boudreau
 * @version $Id$
 */
class XmlaOlap4jCacheElement {

    /**
     * The time in miliseconds when the entry was created.
     */
    private AtomicLong timestamp = new AtomicLong(Calendar.getInstance().getTimeInMillis());


    /**
     * This holds the number of times the entry was used.
     */
    private AtomicLong hitMeter = new AtomicLong(new Long("1"));


    /**
     * The cached SOAP response.
     */
    private byte[] response = null;


    /**
     * Updates it's internal time stamp.
     */
    public void refreshTimestamp() {
        this.timestamp.compareAndSet(this.timestamp.longValue(), Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Updates it's internal time stamp.
     */
    public void incrementHitCount() {
        this.hitMeter.incrementAndGet();
    }

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public AtomicLong getTimestamp() {
        return timestamp;
    }

    public AtomicLong getHitCount() {
        return hitMeter;
    }
}

// End XmlaOlap4jCacheElement.java
