/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2008 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.messages;

import java.security.InvalidParameterException;
import java.util.Locale;

import junit.framework.TestCase;

import org.olap4j.messages.Olap4jMessenger;

/**
 * <p>This is a simple test that makes sure that the messages facilities
 * work as expected.
 *
 * @author Luc Boudreau
 */
public class MessengerTest extends TestCase {

    public void testMessageFallback() throws Exception {
        String message = "My test message should be the same.";
        assertEquals(message, Olap4jMessenger.getInstance().createException(message).getMessage());
        assertEquals(message, Olap4jMessenger.getInstance().getMessage(message));
    }
    
    public void testLocales() throws Exception {
        Locale deutch = new Locale("de");
        Locale french = new Locale("fr");
        assertEquals("FrenchTestValue", MockMessenger1.getInstance().getMessage("test.value.1", french));
        assertEquals("DeutchTestValue", MockMessenger1.getInstance().getMessage("test.value.1", deutch));
    }
    
    public void testSubstitutions() throws Exception {
        String param1 = "param_1";
        String param2 = "param_2";
        String message_o = "Hello, this is my {0} nifty {1} message.";
        String message_m = "Hello, this is my param_1 nifty param_2 message.";
        assertEquals(message_o, MockMessenger1.getInstance().getMessage("nifty_message"));
        assertEquals(message_m, MockMessenger1.getInstance().getMessage("nifty_message", null, param1, param2));
        assertEquals(message_o, MockMessenger1.getInstance().createException("nifty_message").getMessage());
        assertEquals(message_m, MockMessenger1.getInstance().createException("nifty_message", null, param1, param2).getMessage());
    }
    
    public void testCauses() throws Exception {
        InvalidParameterException cause = new InvalidParameterException("Test value");
        assertEquals(cause, Olap4jMessenger.getInstance().createException("Test", cause).getCause());
        assertEquals("Test value", Olap4jMessenger.getInstance().createException("Test", cause).getCause().getMessage());
    }
    
    public void testMultipleMessengersConcurrency() throws Exception {
        Locale deutch = new Locale("de");
        Locale french = new Locale("fr");
        assertEquals("FrenchTestValue", MockMessenger1.getInstance().getMessage("test.value.1", french));
        assertEquals("FrenchTestValue_2", MockMessenger2.getInstance().getMessage("test.value.1", french));
        assertEquals("DeutchTestValue", MockMessenger1.getInstance().getMessage("test.value.1", deutch));
        assertEquals("DeutchTestValue_2", MockMessenger2.getInstance().getMessage("test.value.1", deutch));
    }
    
    private static class MockMessenger1 extends AbstractOlap4jMessenger {

        private static MockMessenger1 instance = new MockMessenger1();
        
        public static MockMessenger1 getInstance() {
            return instance;
        }
        
        public String getBundleName() {
            return "org.olap4j.messages.mock1.test_messages";
        }

        public String getDriverName() {
            return "MockMessenger1";
        }
        
    }
    
    private static class MockMessenger2 extends AbstractOlap4jMessenger {

        private static MockMessenger2 instance = new MockMessenger2();
        
        public static MockMessenger2 getInstance() {
            return instance;
        }
        
        public String getBundleName() {
            return "org.olap4j.messages.mock2.test_messages";
        }

        public String getDriverName() {
            return "MockMessenger2";
        }
        
    }
    
}

// End MessengerTest.java
