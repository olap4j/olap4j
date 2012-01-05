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
import org.olap4j.driver.xmla.cache.XmlaOlap4jCache;

import java.util.Map;

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
        Map<String, String> properties) throws OlapException;

}

// End XmlaOlap4jCachedProxy.java
