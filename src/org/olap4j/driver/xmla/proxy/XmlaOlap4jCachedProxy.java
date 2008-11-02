/*
// $Id$
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.proxy;

import java.util.Map;

import org.olap4j.OlapException;
import org.olap4j.driver.xmla.cache.XmlaOlap4jCache;

/**
 *
 * Extended Proxy interface which supports cached SOAP calls.
 *
 * @author Luc Boudreau
 * @version $Id$
 *
 */
public interface XmlaOlap4jCachedProxy extends XmlaOlap4jProxy {

    /**
     * <p>Sets the cache class to use as a SOAP message cache.
     *
     * <p>Calling this method is not mandatory. If it isn't called,
     * no cache will be used and all SOAP requests will be sent to
     * the service end-point.
     *
     * @param configParameters This contains all the parameters used
     * to configure the Olap4j driver. It contains the full class name
     * of the cache implementation to use as well as the raw Cache
     * config parameters.
     * @param properties The properties to configure the cache,
     * so all config parameters which started
     * by Cache.* are inside this convenient thigny.
     * @see XmlaOlap4jCache
     */
    void setCache(
        Map<String, String> configParameters,
        Map<String,String> properties) throws OlapException;

}

// End XmlaOlap4jCachedProxy.java
