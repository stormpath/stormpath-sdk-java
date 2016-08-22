/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.impl.http;

import com.stormpath.sdk.impl.util.RequestUtils;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;

import java.util.Map;
import java.util.TreeMap;

/**
 * @since 0.1
 */
public class QueryString extends TreeMap<String,String> {

    public QueryString(){}

    public QueryString(Map<String,?> source) {
        super();
        if (!Collections.isEmpty(source)) {
            for(Map.Entry<String,?> entry : source.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String sValue = value != null ? String.valueOf(value) : null;
                put(key, sValue);
            }
        }
    }

    public String toString() {
        return toString(false);
    }

    /**
     * The canonicalized query string is formed by first sorting all the query
     * string parameters, then URI encoding both the key and value and then
     * joining them, in order, separating key value pairs with an '&'.
     *
     * @param canonical whether or not the string should be canonicalized
     * @return the canonical query string
     */
    public String toString(boolean canonical) {
        if (isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String,String> entry : entrySet()) {
            String key = RequestUtils.encodeUrl(entry.getKey(), false, canonical);
            String value = RequestUtils.encodeUrl(entry.getValue(), false, canonical);

            if (sb.length() > 0) {
                sb.append('&');
            }

            sb.append(key).append("=").append(value);
        }

        return sb.toString();
    }

    public static QueryString create(String query) {
        if (!Strings.hasLength(query)) {
            return null;
        }

        QueryString queryString = new QueryString();

        // only returns null if string is null
        String[] tokens = Strings.tokenizeToStringArray(query, "&", false, false);
        for( String token : tokens) {
            applyKeyValuePair(queryString, token);
        }

        return queryString;
    }

    private static void applyKeyValuePair(QueryString qs, String kv) {

        String[] pair = Strings.split(kv, "=");

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
