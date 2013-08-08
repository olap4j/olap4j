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
package org.olap4j.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Mapping between {@link Locale} and Locale identifier (LCID).
 *
 * @author jhyde
 */
public class LcidLocale {
    final Map<Short, Info> lcidLocaleMap = new HashMap<Short, Info>();
    final Map<String, Info> localeToLcidMap = new HashMap<String, Info>();

    /**
     * The singleton instance. Initialized lazily, to avoid the space overhead
     * of the full map. (Most people only use LCID 1033 = en_US.)
     */
    private static LcidLocale INSTANCE;

    private LcidLocale() {
        URL resource = getClass().getResource("nls.properties");
        InputStream inputStream = null;
        try {
            inputStream = resource.openStream();
            Properties properties = new Properties();
            properties.load(inputStream);
            @SuppressWarnings("unchecked")
            final Map<String, String> map = (Map) properties;
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                if (!key.startsWith("locale.") || !key.endsWith(".lcid")) {
                    continue;
                }
                final String root =
                    key.substring(0, key.length() - ".lcid".length());
                Info info =
                    new Info(
                        Short.parseShort(entry.getValue().substring(2), 16),
                        root.substring("locale.".length()).replace('-', '_'),
                        map.get(root + ".locale"),
                        map.get(root + ".language"),
                        map.get(root + ".languageLocal"),
                        Short.parseShort(map.get(root + ".codepageAnsi")),
                        Short.parseShort(map.get(root + ".codepageOem")),
                        map.get(root + ".regionAbbrev"),
                        map.get(root + ".languageAbbrev"));
                lcidLocaleMap.put(info.lcid, info);
                localeToLcidMap.put(info.localeCode, info);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                "Exception while initializing LcidLocale", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    /**
     * Returns the singleton instance, creating if necessary.
     */
    static LcidLocale instance() {
        if (INSTANCE == null) {
            INSTANCE = new LcidLocale();
        }
        return INSTANCE;
    }

    /**
     * Converts a locale id to a locale.
     *
     * @param lcid LCID
     * @return Locale, never null
     * @throws RuntimeException if LCID is not valid
     */
    private Locale toLocale(short lcid) {
        final Info s = lcidLocaleMap.get(lcid);
        if (s == null) {
            throw new RuntimeException("Unknown LCID " + lcid);
        }
        return s.getLocale();
    }

    /**
     * Converts a locale identifier (LCID) as used by Windows into a Java
     * locale.
     *
     * <p>For example, {@code lcidToLocale(1033)} returns "en_US", because
     * 1033 (hex 0409) is US english.</p>
     *
     * @param lcid Locale identifier
     * @return Locale
     * @throws RuntimeException if locale id is unkown
     */
    public static Locale lcidToLocale(short lcid) {
        // Most common case first, to avoid instantiating the full map.
        if (lcid == 0x0409) {
            return Locale.US;
        }
        return instance().toLocale(lcid);
    }

    /**
     * Converts a locale name to a locale identifier (LCID).
     *
     * <p>For example, {@code localeToLcid(Locale.US)} returns 1033,
     * because 1033 (hex 0409) is US english.</p>
     *
     * @param locale Locale
     * @return Locale identifier
     * @throws RuntimeException if locale has no known LCID
     */
    public static short localeToLcid(Locale locale) {
        // Most common case first, to avoid instantiating the full map.
        if (locale.equals(Locale.US)) {
            return 0x0409;
        }
        return instance().toLcid(locale.toString());
    }

    private short toLcid(String localeName) {
        final String localeName0 = localeName;
        for (;;) {
            final Info info = localeToLcidMap.get(localeName);
            if (info != null) {
                return info.lcid;
            }
            final int underscore = localeName.lastIndexOf('_');
            if (underscore < 0) {
                throw new RuntimeException("Unknown locale " + localeName0);
            }
            localeName = localeName.substring(0, underscore);
        }
    }

    /**
     * Parses a locale string.
     *
     * <p>The inverse operation of {@link java.util.Locale#toString()}.
     *
     * @param localeString Locale string, e.g. "en" or "en_US"
     * @return Java locale object
     */
    public static Locale parseLocale(String localeString) {
        String[] strings = localeString.split("_");
        switch (strings.length) {
        case 1:
            return new Locale(strings[0]);
        case 2:
            return new Locale(strings[0], strings[1]);
        case 3:
            return new Locale(strings[0], strings[1], strings[2]);
        default:
            throw new RuntimeException(
                "bad locale string '" + localeString + "'");
        }
    }

    public static class Info {
        public final short lcid; // e.g. 0x080C
        public final String localeCode; // e.g. "fr_BE"
        public final String locale; // e.g. "French (Belgium)"
        public final String language; // e.g. "French"
        public final String languageLocal; // e.g. "français (Belgique)"
        public final short codepageAnsi; // e.g. 1252
        public final short codepageOem; // e.g. 850
        public final String regionAbbrev; // e.g. "BEL"
        public final String languageAbbrev; // e.g. "FRB"

        private Info(
            short lcid,
            String localeCode,
            String locale,
            String language,
            String languageLocal,
            short codepageAnsi,
            short codepageOem,
            String regionAbbrev,
            String languageAbbrev)
        {
            this.lcid = lcid;
            this.localeCode = localeCode;
            this.locale = locale;
            this.language = language;
            this.languageLocal = languageLocal;
            this.codepageAnsi = codepageAnsi;
            this.codepageOem = codepageOem;
            this.regionAbbrev = regionAbbrev;
            this.languageAbbrev = languageAbbrev;
        }

        public Locale getLocale() {
            return parseLocale(localeCode);
        }
    }
}

// End LcidLocale.java
