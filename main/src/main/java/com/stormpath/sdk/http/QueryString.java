/*
 * Copyright 2012 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.sdk.http;

import com.stormpath.sdk.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

/**
 * @since 0.1
 */
public class QueryString extends TreeMap<String,String> {

    public QueryString(){}

    public String toString() {
        if (isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String,String> entry : entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (sb.length() > 0) {
                sb.append('&');
            }

            if (!isEncoded(key)) {
                key = encode(key);
            }

            if (!isEncoded(value)) {
                value = encode(value);
            }

            if (value == null) {
                value = "";
            }

            sb.append(key).append("=").append(value);
        }

        return sb.toString();
    }

    private static boolean isEncoded(String s) {
        return s != null && s.contains("%");
    }

    private static String encode(String s) {
        if (s == null || s.equals("")) {
            return s;
        }
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Unable to UTF-8 encode query string component [" + s + "]", e);
        }
    }

    public static QueryString create(String query) {
        if (!StringUtils.hasLength(query)) {
            return null;
        }
        boolean alreadyEncoded = query.contains("%");

        QueryString queryString = new QueryString();

        String[] tokens = StringUtils.tokenizeToStringArray(query, "&", alreadyEncoded, false);
        if (tokens != null) {
            for( String token : tokens) {
                applyKeyValuePair(queryString, token);
            }
        } else {
            applyKeyValuePair(queryString, query);
        }

        return queryString;
    }

    private static void applyKeyValuePair(QueryString qs, String kv) {

        String[] pair = StringUtils.split(kv, "=");

        if (pair != null) {
            String key = pair[0];
            String value = pair[1] != null ? pair[1] : "";
            qs.put(key, value);
        } else {
            //no equals sign, it's just a key:
            qs.put(kv, null);
        }
    }


}
