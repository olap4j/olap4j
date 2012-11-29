/*
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
package org.olap4j.driver.xmla.cache;

import junit.framework.TestCase;

/**
 * <p>Test for {@link org.olap4j.driver.xmla.cache.XmlaOlap4jShaEncoder}.
 *
 * @author Luc Boudreau
 */
public class XmlaShaEncoderTest extends TestCase {

    private static final String message_1 =
        "This is my nifty message number 1";
    private static final String message_2 =
        "This is my nifty message number 2";
    private static final String message_1_encoded =
        "0821347e66167004f9aba546ae9e61ec5b471e59";
    private static final String message_2_encoded =
        "95dfe200baddc69e2f53e78feeac445a0bdbb4e7";

    public void testSimpleEncoding() throws Exception {
        String encoded = XmlaOlap4jShaEncoder.encodeSha1(message_1);
        assertEquals(message_1_encoded, encoded);
    }

    public void testDoubleEncoding() throws Exception {
        String encoded = XmlaOlap4jShaEncoder.encodeSha1(message_1);
        assertEquals(message_1_encoded, encoded);

        String encoded2 = XmlaOlap4jShaEncoder.encodeSha1(message_2);
        assertEquals(message_2_encoded, encoded2);

        assertFalse(encoded.equals(encoded2));

        String encoded3 = XmlaOlap4jShaEncoder.encodeSha1(message_1);
        assertEquals(message_1_encoded, encoded3);

        String encoded4 = XmlaOlap4jShaEncoder.encodeSha1(message_1);
        assertEquals(message_1_encoded, encoded4);

        assertEquals(encoded3, encoded4);
    }

}

// End XmlaShaEncoderTest.java
