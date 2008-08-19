/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.messages;



/**
 * <p>This is the main messenger facility for the Olap4j API. All messages and
 * exceptions are thrown through this facility and localized here.
 * 
 * @author Luc boudreau
 * @version $Id: $
 */
public class Olap4jMessenger extends AbstractOlap4jMessenger {

    /**
     * Holds on at the static level to the singleton instance of this 
     * API resource bundle.
     */
    private static Olap4jMessenger instance = new Olap4jMessenger();
    
    /**
     * Returns an instance of the XMLA driver resource bundle. 
     * @return
     */
    public static Olap4jMessenger getInstance() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.olap4j.messages.AbstractOlap4jMessenger#getBundleName()
     */
    public String getBundleName() {
        return "org.olap4j.messages.messages"; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see org.olap4j.messages.AbstractOlap4jMessenger#getDriverName()
     */
    public String getDriverName() {
        return "org.olap4j.API"; //$NON-NLS-1$
    }

}
