package org.olap4j.driver.xmla.cache;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * This mock server cache is only used to save and load
 * runs of the XMLA driver as a database table.
 * @see Properties
 * @author LBoudreau
 */
public class XmlaDatabaseCache implements XmlaOlap4jCache {

    private static Connection connection = null;
    private final static String CACHE_IDENT = "Panda steak!";
    private Map<String, String> props;

    public static enum Properties {

        /**
         * Jdbc driver class to use. Defaults to
         * <code>org.hsqldb.jdbcDriver</code>
         */

        JDBC_DRIVER("org.hsqldb.jdbcDriver"),
        /**
         * Jdbc url to use. Defaults to
         * <code>jdbc:hsqldb:file:xmla-cache/xmla-cache-hsqldb</code>
         */
        JDBC_URL("jdbc:hsqldb:file:xmla-cache/xmla-cache-hsqldb"),

        /**
         * Jdbc username to use. Defaults to
         * <code>sa</code>
         */
        JDBC_USER("sa"),

        /**
         * Jdbc password to use. Defaults to
         * an empty string
         */
        JDBC_PASS(""),

        /**
         * Query to execute to insert elements. Defaults to
         * <code>insert into "cache" ("request", "response") values(?,?);</code>
         */
        QUERY_INSERT(
            "insert into \"cache\"(\"request\", \"response\") values(?,?);"),

        /**
         * Query to execute to select elements. Defaults to
         * <code>select "request", "response" from "cache"
         * where "request" = ?;</code>
         */
        QUERY_SELECT(
            "select \"request\", \"response\" from \"cache\" where"
            + "\"request\" = ?;"),

        /**
         * Query to initialize the cache. Can be a batch of SQL. Defaults to
         * <code>create table "cache" ("request"
         * varchar, "response" varchar);</code>
         */
        QUERY_INIT_CACHE(
            "drop table \"cache\" if exists; create table \"cache\" (\"request\" varchar, \"response\" varchar);"),

        /**
         * Should the cache insert requests/responses.
         * defaults to false.
         */
        RECORD("false"),

        /**
         * Should the cache return cached responses.
         * defaults to false.
         */
        PLAY("false"),

        /**
         * Should the cache execute Properties.QUERY_INIT_CACHE.
         * defaults to false.
         */
        INIT("false");

        private final String defaultValue;

        private Properties(String defaultValue) {
            this.defaultValue = defaultValue;
        }
        String getValueOrDefault(Map<String, String> props) {
            if (props.containsKey(this.name())) {
                return props.get(name());
            } else {
                return this.defaultValue;
            }
        }
    }

    public void flushCache() {
        // no op
    }

    public byte[] get(String id, URL url, byte[] request)
            throws XmlaOlap4jInvalidStateException
    {
        if (!Boolean.valueOf(Properties.PLAY.getValueOrDefault(props))) {
            return null;
        }
        try {
            final PreparedStatement stm =
                connection.prepareStatement(
                    Properties.QUERY_SELECT.getValueOrDefault(props));
            try {
                stm.setString(1, new String(request));
                stm.execute();
                ResultSet rs = stm.getResultSet();
                if (rs.next()) {
                    return rs.getString(2).getBytes();
                } else {
                    return null;
                }
            } finally {
                stm.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void put(String id, URL url, byte[] request, byte[] response)
            throws XmlaOlap4jInvalidStateException
    {
        if (!Boolean.valueOf(Properties.RECORD.getValueOrDefault(props))) {
            return;
        }
        try {
            final PreparedStatement stm =
                connection.prepareStatement(
                    Properties.QUERY_INSERT.getValueOrDefault(props));
            try {
                stm.setString(1, new String(request));
                stm.setString(2, new String(response));
                stm.execute();
            } finally {
                stm.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String setParameters(
            Map<String, String> config,
            Map<String, String> props)
    {
        this.props = props;
        if (connection == null) {
            try {
                Class.forName(Properties.JDBC_DRIVER.getValueOrDefault(props));
                connection =
                    DriverManager.getConnection(
                            Properties.JDBC_URL.getValueOrDefault(props),
                            Properties.JDBC_USER.getValueOrDefault(props),
                            Properties.JDBC_PASS.getValueOrDefault(props));
                if (Boolean.valueOf(
                        Properties.INIT.getValueOrDefault(props)))
                {
                    final Statement stm = connection.createStatement();
                    try {
                        stm.addBatch(
                            Properties.QUERY_INIT_CACHE
                                .getValueOrDefault(props));
                        stm.executeBatch();
                    } catch (SQLException e) {
                        // no op
                    } finally {
                        stm.close();
                    }
                    flushCache();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return CACHE_IDENT;
    }
}

// End XmlaDatabaseCache.java
