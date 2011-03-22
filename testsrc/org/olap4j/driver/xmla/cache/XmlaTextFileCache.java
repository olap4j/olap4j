package org.olap4j.driver.xmla.cache;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.olap4j.impl.Base64;

/**
 * This mock server cache is only used to save and load
 * runs of the XMLA driver as an external file.
 * @author LBoudreau
 */
public class XmlaTextFileCache implements XmlaOlap4jCache {

    private static boolean initDone = false;
    private final static String CACHE_IDENT= "Chunky Bacon!";
    private final static ConcurrentHashMap<String, byte[]> cache =
        new ConcurrentHashMap<String, byte[]>();

    public static enum Properties {
        /**
         * File to save the requests to, if RECORD is true.
         * java.util.File(path) will be used.
         */
        OUTPUT,
        /**
         * File to read the requests from, if PLAY is true.
         * java.util.File(path) will be used.
         */
        INPUT,
        /**
         * Whether to record the requests/responses to a file.
         */
        RECORD,
        /**
         * Whether to playback responses from a file.
         */
        PLAY,
    }

    private static File output = null;
    private static File input = null;
    private static boolean record = false;
    private static boolean play = false;;

    public void flushCache() {
        // no op
    }

    public byte[] get(String id, URL url, byte[] request)
            throws XmlaOlap4jInvalidStateException
    {
        synchronized (cache) {
            if (play && input != null) {
                return cache.get(new String(request));
            } else {
                return null;
            }
        }
    }

    public void put(String id, URL url, byte[] request, byte[] response)
            throws XmlaOlap4jInvalidStateException
    {
        synchronized (cache) {
            if (record && output != null) {
                String base64Request = Base64.encodeBytes(request);
                String base64Response = Base64.encodeBytes(response);
                try {
                    FileWriter writer = new FileWriter(output, true);
                    try 
                    {
                        writer.write(base64Request);
                        writer.write("\r\n||\r\n");
                        writer.write(base64Response);
                        writer.write("\r\n||\r\n");
                        writer.flush();
                    } finally {
                        writer.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public String setParameters(Map<String, String> config,
            Map<String, String> props)
    {
        if (!initDone) {
            synchronized (cache) {
                cache.clear();
                try {
                    if (props.containsKey(Properties.RECORD.name())
                            && props.containsKey(Properties.OUTPUT.name()))
                    {
                        record = true;
                        output = new File(props.get(Properties.OUTPUT.name()));
                        if (!output.exists()
                                && !output.createNewFile()) {
                            throw new RuntimeException(
                                    "Failed to create output file.");
                        }
                    }
                    if (props.containsKey(Properties.PLAY.name())
                            && props.containsKey(Properties.INPUT.name()))
                    {
                        play = true;
                        input = new File(props.get(Properties.INPUT.name()));
                        if (input.exists()) {
                            FileInputStream fstream =
                                new FileInputStream(input);
                            DataInputStream in =
                                new DataInputStream(fstream);
                            BufferedReader br =
                                new BufferedReader(new InputStreamReader(in));
                            String strLine;
                            StringBuilder sb = new StringBuilder();
                            while ((strLine = br.readLine()) != null) {
                                sb.append(strLine);
                            }
                            StringTokenizer st =
                                new StringTokenizer(
                                    sb.toString(),
                                    "\r\n|||\r\n");
                            while (st.hasMoreTokens()) {
                                byte[] request =
                                    Base64.decode(st.nextToken());
                                byte[] response =
                                    Base64.decode(st.nextToken());
                                cache.put(new String(request), response);
                            }
                            in.close();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(
                        "Failed to configure the mock server cache.",
                        e);
                }
            }
        }
        return CACHE_IDENT;
    }

}
