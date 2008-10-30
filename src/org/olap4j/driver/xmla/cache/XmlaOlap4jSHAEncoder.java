/*
// $Id: XmlaOlap4jSHAEncoder.java 92 2008-07-17 07:41:10Z lucboudreau $
// This software is subject to the terms of the Common Public License
// Agreement, available at the following URL:
// http://www.opensource.org/licenses/cpl.html.
// Copyright (C) 2007-2007 Julian Hyde
// All Rights Reserved.
// You must accept the terms of that agreement to use this software.
*/
package org.olap4j.driver.xmla.cache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * SHA encoder to create unique hash strings for cache elements.
 *
 * @author Luc Boudreau
 * @version $Id: XmlaOlap4jSHAEncoder.java 92 2008-07-17 07:41:10Z lucboudreau $
 *
 */
class XmlaOlap4jSHAEncoder {

    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e1) {
                throw new RuntimeException(e1);
            }
        }

        byte[] sha1hash = new byte[40];

        md.update(text.getBytes(), 0, text.length());

        sha1hash = md.digest();

        return convertToHex(sha1hash);
    }
}

// End XmlaOlap4jSHAEncoder.java
