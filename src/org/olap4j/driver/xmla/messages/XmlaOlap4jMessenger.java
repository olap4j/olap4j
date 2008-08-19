/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.messages;

import org.olap4j.OlapException;
import org.olap4j.messages.AbstractOlap4jMessenger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Manages all messages for the XMLA driver through Olap4j messaging 
 * facilities.
 * @author Luc Boudreau
 * @version $Id: $
 */
public class XmlaOlap4jMessenger extends AbstractOlap4jMessenger {

    /**
     * Holds on at the static level to the singleton instance of this 
     * driver's resource bundle.
     */
    private static XmlaOlap4jMessenger instance = 
        new XmlaOlap4jMessenger();
    
    /**
     * Returns an instance of the XMLA driver resource bundle. 
     * @return
     */
    public static XmlaOlap4jMessenger getInstance() {
        return instance;
    }
    
    /* (non-Javadoc)
     * @see org.olap4j.messages.Olap4jResourceBundle#getBundleName()
     */
    public String getBundleName() {
        return "org.olap4j.driver.xmla.messages.messages";
    }

    /* (non-Javadoc)
     * @see org.olap4j.messages.Olap4jResourceBundle#getDriverName()
     */
    public String getDriverName() {
        return "org.olap4j.driver.xmla.XmlaOlap4jDriver";
    }
    
    /**
     * Parses an XML fragment from a SOAP fault response and generates
     * a user friendly message in an OlapException.
     * @param soapFaultElement Pass it the SOAP-ENV:Fault xml element
     * @return returns the String message.
     */
    public OlapException createException(Element soapFaultElement)
    {
        /* Example:
         * <SOAP-ENV:Fault>
                <faultcode>SOAP-ENV:Client.00HSBC01</faultcode>
                <faultstring>XMLA connection datasource not found</faultstring>
                <faultactor>Mondrian</faultactor>
                <detail>
                    <XA:error xmlns:XA="http://mondrian.sourceforge.net">
                        <code>00HSBC01</code>
                        <desc>The Mondrian XML: Mondrian Error:Internal
                            error: no catalog named 'LOCALDB'</desc>
                    </XA:error>
                </detail>
            </SOAP-ENV:Fault> 
         */
        StringBuilder sb = new StringBuilder(
                "-- The remote SOAP endpoint returned an error message --");
        appendStructuredErrorMessageElement(
            sb,
            soapFaultElement,
            0);
        return new OlapException(sb.toString());
    }
    
    /**
     * Recursive private method which parses an XML structure and
     * creates a formatted message.
     * @param sb A StringBuilder object into which the message will be appended.
     * @param currentElement A Node object into which we'll descend.
     * @param level The current level. Send 0 for the first iteration.
     */
    private void appendStructuredErrorMessageElement(StringBuilder sb, Node currentElement, int level)
    {
        sb.append("\n");
        for(int cpt = 0; cpt < level; cpt++) {
            sb.append("\t");
        }
        sb.append("<");
        sb.append(currentElement.getNodeName());
        sb.append(">");
        
        NodeList childrenList = currentElement.getChildNodes();
        for (int childrenPosition = 0; 
            childrenPosition < childrenList.getLength();
            childrenPosition++)
        {
            if (childrenList.item(childrenPosition).getNodeType() 
                    != Node.TEXT_NODE)
            {
                appendStructuredErrorMessageElement(
                        sb, 
                        childrenList.item(childrenPosition), 
                        level+1);
            } else {
                sb.append("\n");
                for(int cpt = 0; cpt < level+1; cpt++) {
                    sb.append("\t");
                }
                sb.append(currentElement.getTextContent());
            }
        }
        
        sb.append("\n");
        for(int cpt = 0; cpt < level; cpt++) {
            sb.append("\t");
        }
        sb.append("</");
        sb.append(currentElement.getNodeName());
        sb.append(">");
    }
}
//End XmlaOlap4jMessenger.java