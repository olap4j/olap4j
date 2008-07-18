package org.olap4j;

import junit.framework.TestCase;

import org.olap4j.driver.xmla.cache.XmlaOlap4jSHAEncoder;

/**
 * <p>This is a simple test that makes sure that the SHA encoder works
 * as expected.
 * 
 * @author Luc Boudreau
 */
public class XmlaSHAEncoderTest extends TestCase {
    
    private static final String message_1 = "This is my nifty message number 1";
    private static final String message_2 = "This is my nifty message number 2";
    private static final String message_1_encoded = "0821347e66167004f9aba546ae9e61ec5b471e59";
    private static final String message_2_encoded = "95dfe200baddc69e2f53e78feeac445a0bdbb4e7";
    
    
    public void testSimpleEncoding() throws Exception {
        String encoded = XmlaOlap4jSHAEncoder.SHA1(message_1);
        assertEquals(message_1_encoded, encoded);
    }
    
    
    public void testDoubleEncoding() throws Exception
    {
        String encoded = XmlaOlap4jSHAEncoder.SHA1(message_1);
        assertEquals(message_1_encoded, encoded);
        
        String encoded2 = XmlaOlap4jSHAEncoder.SHA1(message_2);
        assertEquals(message_2_encoded, encoded2);
        
        assertFalse(encoded.equals(encoded2));
        
        String encoded3 = XmlaOlap4jSHAEncoder.SHA1(message_1);
        assertEquals(message_1_encoded, encoded3);
        
        String encoded4 = XmlaOlap4jSHAEncoder.SHA1(message_1);
        assertEquals(message_1_encoded, encoded4);
        
        assertEquals(encoded3, encoded4);
    }

}
