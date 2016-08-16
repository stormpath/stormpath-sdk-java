/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.sdk.impl.http.support;

import com.stormpath.sdk.impl.http.CanonicalUri;
import com.stormpath.sdk.http.QueryString;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;

import java.util.Map;

public class DefaultCanonicalUri implements CanonicalUri {

    private final String absolutePath;
    private final QueryString query;

    public DefaultCanonicalUri(String absolutePath, QueryString query) {
        Assert.hasText(absolutePath, "absolutePath argument cannot be null or empty.");
        this.absolutePath = absolutePath;
        this.query = query;
    }

    @Override
    public String getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public boolean hasQuery() {
        return query != null;
    }

    @Override
    public QueryString getQuery() {
        return query;
    }

    public static CanonicalUri create(String href, Map<String,?> queryParams) {

        Assert.hasText(href, "href argument cannot be null or empty.");

        QueryString query = null;

        if (!Collections.isEmpty(queryParams)) {

            query = new QueryString(queryParams); //create a copy so we don't manipulate the argument

            int questionMarkIndex = href.lastIndexOf('?');

            if (questionMarkIndex >= 0) {

                String queryString = href.substring(questionMarkIndex + 1);
                href = href.substring(0, questionMarkIndex);

                if (Strings.hasLength(queryString)) {

                    //the query values from the href portion are explicit and therefore take precedence over
                    //any values in the queryParams argument:
                    QueryString queryStringFromHref = QueryString.create(queryString);

                    if (queryStringFromHref != null) {
                        query.putAll(queryStringFromHref);
                    }
                }
            }

        }

        return new DefaultCanonicalUri(href, query);
    }
}
