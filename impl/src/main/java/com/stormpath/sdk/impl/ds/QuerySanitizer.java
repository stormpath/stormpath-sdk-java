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
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

import java.util.Map;

/**
 * @since 0.8
 */
public class QuerySanitizer {

    public static SanitizedQuery sanitize(String href, Map<String,Object> queryParams) {
        Assert.notNull(href, "href argument cannot be null.");

        QueryString query = new QueryString(queryParams); //create a copy so we don't manipulate the argument

        int questionMarkIndex = href.lastIndexOf('?');
        if (questionMarkIndex >= 0) {
            String queryString = href.substring(questionMarkIndex + 1);
            href = href.substring(0, questionMarkIndex);

            if (Strings.hasLength(queryString)) {
                //the query values from the href portion are explicit and therefore take precedence over
                //any values in the queryParams argument:
                QueryString queryStringFromHref = QueryString.create(queryString);
                query.putAll(queryStringFromHref);
            }
        }

        return new SanitizedQuery(href, query);
    }


}
