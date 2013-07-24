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
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;

/**
 * @since 0.8
 */
public class DefaultCacheKey {

    private final String url;

    public DefaultCacheKey(String href, QueryString queryString) {
        Assert.notNull(href, "href argument cannot be null.");

        String url = href;
        QueryString qs = queryString;

        int questionMarkIndex = href.lastIndexOf('?');
        if (questionMarkIndex >= 0) {
            url = href.substring(0, questionMarkIndex);
            String after = href.substring(questionMarkIndex + 1);

            if (Strings.hasLength(after)) {
                qs = new QueryString(queryString); //create a copy so we don't manipulate the argument
                //the query values from the href portion are explicit and therefore take precedence over
                //any query string values passed in as a separate argument:
                QueryString queryStringFromHref = QueryString.create(after);
                qs.putAll(queryStringFromHref);
            }
        }

        if (!Collections.isEmpty(qs)) {
            url += "?" + qs.toString();
        }

        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof DefaultCacheKey) {
            DefaultCacheKey other = (DefaultCacheKey) o;
            return url.equals(other.url);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
