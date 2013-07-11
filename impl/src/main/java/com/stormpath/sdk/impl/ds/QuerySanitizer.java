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
