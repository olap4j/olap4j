/*
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.messages;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import org.olap4j.Cell;
import org.olap4j.OlapException;

/**
 * <p>Manages all messages to be sent to the end-users of this API and 
 * driver implementation. It is thread safe and supports localization
 * of messages.
 * 
 * <p>This is an abstract class and thus cannot be used as-is. It has to
 * be subclassed by implementing drivers. Subclasses of this abstract 
 * messenger are required to override getBundleName()
 * and getDriverName(). See their respective javadoc for a description.
 * 
 * <p>For an implementation example, refer to 
 * {@link org.olap4j.driver.xmla.messages.XmlaOlap4jMessenger}.
 * 
 * <p>It also serves as a OlapException localized helper by
 * replacing exception messages by performing a lookup in messages bundles and
 * replacing if a corresponding bundeled value is found. 
 * 
 * <p>Example : You call this class with «createException("test.message")».
 * If your messages bundles include a 'test.message' property, the message will
 * be substituted. If it doesn't include such a key, the key will be used as
 * the exception message, thus allowing easy fallback.
 * 
 * @author Luc Boudreau
 * @version $Id: $
 */
public abstract class AbstractOlap4jMessenger {
    
    
    /**
     * Internal hash that maintains all instanciated resource bundles for
     * quick use.
     */
    private static Map<String,Map<String,ResourceBundle>> resourceBundles = 
        new ConcurrentHashMap<String,Map<String, ResourceBundle>>(1);
    
    /**
     * Initializes the ressourceBundles static hash and makes sure that
     * everything is ready for use.
     */
    private void init() {
        assert (getBundleName() != null);
        assert (getDriverName() != null);
        synchronized (resourceBundles) {
            if (!resourceBundles.containsKey(getDriverName())){
                resourceBundles.put(
                    getDriverName(),
                    new ConcurrentHashMap<String, ResourceBundle>(1));
            }
        }
    }
    
    
    /**
     * Performs a lookup on a message key and returns the proper
     * message string object. Uses the default JVM locale.
     * @param key The message key to lookup.
     * @return The message string.
     */
    public String getMessage(String key) {
        return getMessage(
                key, 
                null);
    }
    
    
    /**
     * Performs a lookup on a message key and returns the proper
     * message string object.
     * @param key The message key to lookup.
     * @param locale The locale to use.
     * @param arguments Strings to substitute in the correct order.
     * @return The message string if found, the key otherwise.
     */
    public String getMessage(String key, 
            Locale locale, Object... arguments) 
    {
        // Initializes the facility
        init();
        
        // Tries to fetch the correct message
        String message;
        try {
            message = getBundle(locale).getString(key);
        } catch (MissingResourceException e) {
            message = key;
        }
        
        // 
        if ( message != null &&
                arguments != null &&
                arguments.length > 0 )
        {
            for (int c = 0; c < arguments.length; c++) {
                message = message.replaceAll(
                    "\\{" + c + "\\}", 
                    (arguments[c]==null)?"null":arguments[c].toString());
            }
        }
        return (message==null)?key:message;
    }
    
    /**
     * Returns a ResourceBundle object. Will instanciate a new one
     * if necessary or use a cached one if available.
     * @param locale The locale to use. Null means use of the JVM default.
     * @return The ReousourceBundle object with the correct locale.
     */
    private ResourceBundle getBundle(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        synchronized (resourceBundles) {
            if (!resourceBundles.get(getDriverName()).containsKey(locale.getDisplayName())) {
                resourceBundles.get(getDriverName())
                  .put(
                    locale.getDisplayName(), 
                    ResourceBundle.getBundle(
                        getBundleName(), //$NON-NLS-1$
                        locale)); 
            }
        }
        return resourceBundles.get(getDriverName()).get(locale.getDisplayName());
    }
    
    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @return The built OlapException object.
     */
    public OlapException createException(String msg) {
        return new OlapException(getMessage(msg));
    }
    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @param arguments Strings to substitute in the correct order.
     * @return The built OlapException object.
     */
    public OlapException createException(String msg, Object... arguments) {
        return new OlapException(getMessage(msg, null, arguments));
    }

    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @param cause A subclass of Throwable which triggered the exception.
     * @return The built OlapException object.
     */
    public OlapException createException(String msg, Throwable cause) {
        return new OlapException(getMessage(msg), cause);
    }
    
    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @param cause A subclass of Throwable which triggered the exception.
     * @param arguments Strings to substitute in the correct order.
     * @return The built OlapException object.
     */
    public OlapException createException(
            String msg, 
            Throwable cause, 
            Object... arguments) 
    {
        return new OlapException(getMessage(msg, null, arguments), cause);
    }

    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @param context The Olap4j Context from which originated the exception.
     * @return The built OlapException object.
     */
    public OlapException createException(Cell context, String msg) {
        OlapException exception = new OlapException(getMessage(msg));
        exception.setContext(context);
        return exception;
    }
    
    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @param context The Olap4j Context from which originated the exception.
     * @param arguments Strings to substitute in the correct order.
     * @return The built OlapException object.
     */
    public OlapException createException(
            Cell context, 
            String msg, 
            Object... arguments) 
    {
        OlapException exception = new OlapException(
                                    getMessage(msg,null,arguments));
        exception.setContext(context);
        return exception;
    }

    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @param cause A subclass of Throwable which triggered the exception.
     * @param context The Olap4j Context from which originated the exception.
     * @return The built OlapException object.
     */
    public OlapException createException(
        Cell context, String msg, Throwable cause)
    {
        OlapException exception = new OlapException(getMessage(msg), cause);
        exception.setContext(context);
        return exception;
    }
    
    
    /**
     * Creates an OlapException object and returns it.
     * @param msg A string which describes the nature of the exception.
     * @param cause A subclass of Throwable which triggered the exception.
     * @param context The Olap4j Context from which originated the exception.
     * @param arguments Strings to substitute in the correct order.
     * @return The built OlapException object.
     */
    public OlapException createException(
        Cell context, String msg, Throwable cause, Object... arguments)
    {
        OlapException exception = new OlapException(
                getMessage(msg,null,arguments), cause);
        exception.setContext(context);
        return exception;
    }
    
    
    /**
     * <p>Must return a unique driver implementation identification string to help
     * the messages facilities to find the correct messages bundles when a driver
     * queries for a message.
     * <p>as a rule of thumb, we return the driver class name along with full 
     * package path.
     * @return A unique id for the messages bundle.
     */
    public abstract String getDriverName();
    
    
    /**
     * <p>This needs to be implemented by subclasses so that the messages facility
     * can know where to find the driver's messages.
     * 
     * <p>All messages bundles need to conform to the Java Property Ressource
     * mechanism.
     * 
     * @return The bundle location, including full package names. 
     * Ex : "org.olap4j.driver.xmla.messages.messages" would point to
     * "org.olap4j.driver.xmla.messages.messages.properties"
     */
    public abstract String getBundleName();
}
//End AbstractOlap4jMessenger.java