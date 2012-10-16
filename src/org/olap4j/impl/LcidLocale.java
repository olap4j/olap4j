/*
// $Id$
//
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
 * @version $Id$
 * @author jhyde
 */
public class LcidLocale {
    final Map<Short, Info> lcidLocaleMap = new HashMap<Short, Info>();
    final Map<String, Info> localeToLcidMap = new HashMap<String, Info>();

    private static final Object[] LOCALE_DATA_ = {
        "", (short) 0x007f, "invariant culture",
        "ar", (short) 0x0001, "Arabic",
        "bg", (short) 0x0002, "Bulgarian",
        "ca", (short) 0x0003, "Catalan",
        "zh_CHS", (short) 0x0004, "Chinese (Simplified)",
        "cs", (short) 0x0005, "Czech",
        "da", (short) 0x0006, "Danish",
        "de", (short) 0x0007, "German",
        "el", (short) 0x0008, "Greek",
        "en", (short) 0x0009, "English",
        "es", (short) 0x000a, "Spanish",
        "fi", (short) 0x000b, "Finnish",
        "fr", (short) 0x000c, "French",
        "he", (short) 0x000d, "Hebrew",
        "hu", (short) 0x000e, "Hungarian",
        "is", (short) 0x000f, "Icelandic",
        "it", (short) 0x0010, "Italian",
        "ja", (short) 0x0011, "Japanese",
        "ko", (short) 0x0012, "Korean",
        "nl", (short) 0x0013, "Dutch",
        "no", (short) 0x0014, "Norwegian",
        "pl", (short) 0x0015, "Polish",
        "pt", (short) 0x0016, "Portuguese",
        "ro", (short) 0x0018, "Romanian",
        "ru", (short) 0x0019, "Russian",
        "hr", (short) 0x001a, "Croatian",
        "sk", (short) 0x001b, "Slovak",
        "sq", (short) 0x001c, "Albanian",
        "sv", (short) 0x001d, "Swedish",
        "th", (short) 0x001e, "Thai",
        "tr", (short) 0x001f, "Turkish",
        "ur", (short) 0x0020, "Urdu",
        "id", (short) 0x0021, "Indonesian",
        "uk", (short) 0x0022, "Ukrainian",
        "be", (short) 0x0023, "Belarusian",
        "sl", (short) 0x0024, "Slovenian",
        "et", (short) 0x0025, "Estonian",
        "lv", (short) 0x0026, "Latvian",
        "lt", (short) 0x0027, "Lithuanian",
        "fa", (short) 0x0029, "Farsi",
        "vi", (short) 0x002a, "Vietnamese",
        "hy", (short) 0x002b, "Armenian",
        "az", (short) 0x002c, "Azeri",
        "eu", (short) 0x002d, "Basque",
        "mk", (short) 0x002f, "Macedonian",
        "af", (short) 0x0036, "Afrikaans",
        "ka", (short) 0x0037, "Georgian",
        "fo", (short) 0x0038, "Faroese",
        "hi", (short) 0x0039, "Hindi",
        "ms", (short) 0x003e, "Malay",
        "kk", (short) 0x003f, "Kazakh",
        "ky", (short) 0x0040, "Kyrgyz",
        "sw", (short) 0x0041, "Swahili",
        "uz", (short) 0x0043, "Uzbek",
        "tt", (short) 0x0044, "Tatar",
        "pa", (short) 0x0046, "Punjabi",
        "gu", (short) 0x0047, "Gujarati",
        "ta", (short) 0x0049, "Tamil",
        "te", (short) 0x004a, "Telugu",
        "kn", (short) 0x004b, "Kannada",
        "mr", (short) 0x004e, "Marathi",
        "sa", (short) 0x004f, "Sanskrit",
        "mn", (short) 0x0050, "Mongolian",
        "gl", (short) 0x0056, "Galician",
        "kok", (short) 0x0057, "Konkani",
        "syr", (short) 0x005a, "Syriac",
        "div", (short) 0x0065, "Dhivehi",
        "ar_SA", (short) 0x0401, "Arabic - Saudi Arabia",
        "bg_BG", (short) 0x0402, "Bulgarian - Bulgaria",
        "ca_ES", (short) 0x0403, "Catalan - Catalan",
        "zh_TW", (short) 0x0404, "Chinese - Taiwan",
        "cs_CZ", (short) 0x0405, "Czech - Czech Republic",
        "da_DK", (short) 0x0406, "Danish - Denmark",
        "de_DE", (short) 0x0407, "German - Germany",
        "el_GR", (short) 0x0408, "Greek - Greece",
        "en_US", (short) 0x0409, "English - United States",
        "es_ES", (short) 0x040a, "Spanish - Spain (Traditional Sort)",
        "fi_FI", (short) 0x040b, "Finnish - Finland",
        "fr_FR", (short) 0x040c, "French - France",
        "he_IL", (short) 0x040d, "Hebrew - Israel",
        "hu_HU", (short) 0x040e, "Hungarian - Hungary",
        "is_IS", (short) 0x040f, "Icelandic - Iceland",
        "it_IT", (short) 0x0410, "Italian - Italy",
        "ja_JP", (short) 0x0411, "Japanese - Japan",
        "ko_KR", (short) 0x0412, "Korean - Korea",
        "nl_NL", (short) 0x0413, "Dutch - The Netherlands",
        "nb_NO", (short) 0x0414, "Norwegian (Bokml) - Norway",
        "pl_PL", (short) 0x0415, "Polish - Poland",
        "pt_BR", (short) 0x0416, "Portuguese - Brazil",
        "rm",    (short) 0x0417, "Rhaeto-Romanic",
        "ro_RO", (short) 0x0418, "Romanian - Romania",
        "ru_RU", (short) 0x0419, "Russian - Russia",
        "hr_HR", (short) 0x041a, "Croatian - Croatia",
        "sk_SK", (short) 0x041b, "Slovak - Slovakia",
        "sq_AL", (short) 0x041c, "Albanian - Albania",
        "sv_SE", (short) 0x041d, "Swedish - Sweden",
        "th_TH", (short) 0x041e, "Thai - Thailand",
        "tr_TR", (short) 0x041f, "Turkish - Turkey",
        "ur_PK", (short) 0x0420, "Urdu - Pakistan",
        "id_ID", (short) 0x0421, "Indonesian - Indonesia",
        "uk_UA", (short) 0x0422, "Ukrainian - Ukraine",
        "be_BY", (short) 0x0423, "Belarusian - Belarus",
        "sl_SI", (short) 0x0424, "Slovenian - Slovenia",
        "et_EE", (short) 0x0425, "Estonian - Estonia",
        "lv_LV", (short) 0x0426, "Latvian - Latvia",
        "lt_LT", (short) 0x0427, "Lithuanian - Lithuania",
        "fa_IR", (short) 0x0429, "Farsi - Iran",
        "vi_VN", (short) 0x042a, "Vietnamese - Vietnam",
        "hy_AM", (short) 0x042b, "Armenian - Armenia",
        "az_AZ_Latn", (short) 0x042c, "Azeri (Latin) - Azerbaijan",
        "eu_ES", (short) 0x042d, "Basque - Basque",
        "mk_MK", (short) 0x042f, "Macedonian - FYROM",
        "tn", (short) 0x0432, "Tswana",
        "xh", (short) 0x0434, "Xhosa",
        "zu", (short) 0x0435, "Zulu",
        "af_ZA", (short) 0x0436, "Afrikaans - South Africa",
        "ka_GE", (short) 0x0437, "Georgian - Georgia",
        "fo_FO", (short) 0x0438, "Faroese - Faroe Islands",
        "hi_IN", (short) 0x0439, "Hindi - India",
        "mt", (short) 0x043a, "Maltese",
        "se_NO", (short) 0x043b, "Sami (Northern) - Norway",
        "gd", (short) 0x043c, "Gaelic",
        "ms_MY", (short) 0x043e, "Malay - Malaysia",
        "kk_KZ", (short) 0x043f, "Kazakh - Kazakhstan",
        "ky_KZ", (short) 0x0440, "Kyrgyz - Kazakhstan",
        "sw_KE", (short) 0x0441, "Swahili - Kenya",
        "uz_UZ_Latn", (short) 0x0443, "Uzbek (Latin) - Uzbekistan",
        "tt_RU", (short) 0x0444, "Tatar - Russia",
        "bn_IN", (short) 0x0445, "Bengali - India",
        "pa_IN", (short) 0x0446, "Punjabi - India",
        "gu_IN", (short) 0x0447, "Gujarati - India",
        "ta_IN", (short) 0x0449, "Tamil - India",
        "te_IN", (short) 0x044a, "Telugu - India",
        "kn_IN", (short) 0x044b, "Kannada - India",
        "ml_IN", (short) 0x044c, "Mayalam - India",
        "mr_IN", (short) 0x044e, "Marathi - India",
        "sa_IN", (short) 0x044f, "Sanskrit - India",
        "mn_MN", (short) 0x0450, "Mongolian - Mongolia",
        "cy_GB", (short) 0x0452, "Welsh - United Kingdom",
        "gl_ES", (short) 0x0456, "Galician - Galician",
        "kok_IN", (short) 0x0457, "Konkani - India",
        "syr_SY", (short) 0x045a, "Syriac - Syria",
        "div_MV", (short) 0x0465, "Dhivehi - Maldives",
        "quz_BO", (short) 0x046b, "Quecha - Bolivia",
        "mi_NZ", (short) 0x0481, "Maori - New Zealand",
        "ar_IQ", (short) 0x0801, "Arabic - Iraq",
        "zh_CN", (short) 0x0804, "Chinese - China",
        "de_CH", (short) 0x0807, "German - Switzerland",
        "en_GB", (short) 0x0809, "English - United Kingdom",
        "es_MX", (short) 0x080a, "Spanish - Mexico",
        "fr_BE", (short) 0x080c, "French - Belgium",
        "it_CH", (short) 0x0810, "Italian - Switzerland",
        "nl_BE", (short) 0x0813, "Dutch - Belgium",
        "nn_NO", (short) 0x0814, "Norwegian (Nynorsk) - Norway",
        "pt_PT", (short) 0x0816, "Portuguese - Portugal",
        "ro_MD", (short) 0x0818, "Romanian - Moldova",
        "ru_MD", (short) 0x0819, "Russian - Moldova",
        "sr_SP_Latn", (short) 0x081a, "Serbian (Latin) - Serbia",
        "sv_FI", (short) 0x081d, "Swedish - Finland",
        "az_AZ_Cyrl", (short) 0x082c, "Azeri (Cyrillic) - Azerbaijan",
        "se_SE", (short) 0x083b, "Sami (Northern) - Sweden",
        "ga",    (short) 0x083c, "Irish",
        "ms_BN", (short) 0x083e, "Malay - Brunei",
        "uz_UZ_Cyrl", (short) 0x0843, "Uzbek (Cyrillic) - Uzbekistan",
        "bn_BD",  (short) 0x0845, "Bengali - Bangladesh",
        "quz_EC", (short) 0x086b, "Quecha - Ecuador",
        "ar_EG", (short) 0x0c01, "Arabic - Egypt",
        "zh_HK", (short) 0x0c04, "Chinese - Hong Kong SAR",
        "de_AT", (short) 0x0c07, "German - Austria",
        "en_AU", (short) 0x0c09, "English - Australia",
        "es_ES", (short) 0x0c0a, "Spanish - Spain",
        "fr_CA", (short) 0x0c0c, "French - Canada",
        "sr_SP_Cyrl", (short) 0x0c1a, "Serbian (Cyrillic) - Serbia",
        "se_FI", (short) 0x0c3b, "Sami (Northern) - Finland",
        "quz_PE", (short) 0x0c6b, "Quecha - Peru",
        "ar_LY", (short) 0x1001, "Arabic - Libya",
        "zh_SG", (short) 0x1004, "Chinese - Singapore",
        "de_LU", (short) 0x1007, "German - Luxembourg",
        "en_CA", (short) 0x1009, "English - Canada",
        "es_GT", (short) 0x100a, "Spanish - Guatemala",
        "fr_CH", (short) 0x100c, "French - Switzerland",
        "hr_BA", (short) 0x101a, "Croatian - Bosnia and Herzegovina",
        "ar_DZ", (short) 0x1401, "Arabic - Algeria",
        "zh_MO", (short) 0x1404, "Chinese - Macau SAR",
        "de_LI", (short) 0x1407, "German - Liechtenstein",
        "en_NZ", (short) 0x1409, "English - New Zealand",
        "es_CR", (short) 0x140a, "Spanish - Costa Rica",
        "fr_LU", (short) 0x140c, "French - Luxembourg",
        "bs_Latn_BA", (short) 0x141a, "Bosnian (Latin) - Bosnia/Herzegovina",
        "ar_MA", (short) 0x1801, "Arabic - Morocco",
        "en_IE", (short) 0x1809, "English - Ireland",
        "es_PA", (short) 0x180a, "Spanish - Panama",
        "fr_MC", (short) 0x180c, "French - Monaco",
        "sr_Latn_BA", (short) 0x181a, "Serbian (Latin) - Bosnia/Herzegovina",
        "ar_TN", (short) 0x1c01, "Arabic - Tunisia",
        "en_ZA", (short) 0x1c09, "English - South Africa",
        "es_DO", (short) 0x1c0a, "Spanish - Dominican Republic",
        "sr_Cyrl_BA", (short) 0x1c1a, "Serbian (Cyrillic) - Bosnia/Herzegovina",
        "ar_OM", (short) 0x2001, "Arabic - Oman",
        "en_JM", (short) 0x2009, "English - Jamaica",
        "es_VE", (short) 0x200a, "Spanish - Venezuela",
        "ar_YE", (short) 0x2401, "Arabic - Yemen",
        "en_CB", (short) 0x2409, "English - Caribbean",
        "es_CO", (short) 0x240a, "Spanish - Colombia",
        "ar_SY", (short) 0x2801, "Arabic - Syria",
        "en_BZ", (short) 0x2809, "English - Belize",
        "es_PE", (short) 0x280a, "Spanish - Peru",
        "ar_JO", (short) 0x2c01, "Arabic - Jordan",
        "en_TT", (short) 0x2c09, "English - Trinidad and Tobago",
        "es_AR", (short) 0x2c0a, "Spanish - Argentina",
        "ar_LB", (short) 0x3001, "Arabic - Lebanon",
        "en_ZW", (short) 0x3009, "English - Zimbabwe",
        "es_EC", (short) 0x300a, "Spanish - Ecuador",
        "ar_KW", (short) 0x3401, "Arabic - Kuwait",
        "en_PH", (short) 0x3409, "English - Philippines",
        "es_CL", (short) 0x340a, "Spanish - Chile",
        "ar_AE", (short) 0x3801, "Arabic - United Arab Emirates",
        "es_UY", (short) 0x380a, "Spanish - Uruguay",
        "ar_BH", (short) 0x3c01, "Arabic - Bahrain",
        "es_PY", (short) 0x3c0a, "Spanish - Paraguay",
        "ar_QA", (short) 0x4001, "Arabic - Qatar",
        "es_BO", (short) 0x400a, "Spanish - Bolivia",
        "es_SV", (short) 0x440a, "Spanish - El Salvador",
        "es_HN", (short) 0x480a, "Spanish - Honduras",
        "es_NI", (short) 0x4c0a, "Spanish - Nicaragua",
        "es_PR", (short) 0x500a, "Spanish - Puerto Rico",
        "zh_CHT", (short) 0x7c04, "Chinese (Traditional)",
    };

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
                String value = entry.getValue();
                if (key.endsWith(".lcid")) {
                    final String root =
                        key.substring(0, key.length() - ".lcid".length());
                    String lcname =
                        root.substring(root.indexOf('.') + 1).replace('-', '_');
                    short lcid = Short.parseShort(value.substring(2), 16);
                    String locale = map.get(root + ".locale");
                    String language = map.get(root + ".language");
                    String languageLocal = map.get(root + ".languageLocal");
                    String codepageAnsi = map.get(root + ".codepageAnsi");
                    String codepageOem = map.get(root + ".codepageOem");
                    String regionAbbrev = map.get(root + ".regionAbbrev");
                    String languageAbbrev = map.get(root + ".languageAbbrev");
                    Info info =
                        new Info(
                            lcid,
                            lcname,
                            locale,
                            language,
                            languageLocal,
                            Short.parseShort(codepageAnsi),
                            Short.parseShort(codepageOem),
                            regionAbbrev,
                            languageAbbrev);
                    lcidLocaleMap.put(lcid, info);
                    localeToLcidMap.put(lcname, info);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        return parseLocale(s.locale);
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
        public final short lcid;
        public final String locale;
        public final String localeFull;
        public final String language;
        public final String languageLocal;
        public final short codepageAnsi;
        public final short codepageOem;
        public final String regionAbbrev;
        public final String languageAbbrev;

        public Info(
            short lcid,
            String locale,
            String localeFull,
            String language,
            String languageLocal,
            short codepageAnsi,
            short codepageOem,
            String regionAbbrev,
            String languageAbbrev)
        {
            this.lcid = lcid;
            this.locale = locale;
            this.localeFull = localeFull;
            this.language = language;
            this.languageLocal = languageLocal;
            this.codepageAnsi = codepageAnsi;
            this.codepageOem = codepageOem;
            this.regionAbbrev = regionAbbrev;
            this.languageAbbrev = languageAbbrev;
        }
    }
}

// End LcidLocale.java
