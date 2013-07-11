package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.lang.Assert;

/**
 * @since 0.8
 */
public class SanitizedQuery {

    private final String hrefWithoutQuery;

    private final QueryString query;

    public SanitizedQuery(String hrefWithoutQuery, QueryString query) {
        Assert.notNull(hrefWithoutQuery, "href argument cannot be null.");
        Assert.notNull(query, "query argument cannot be null.");
        this.hrefWithoutQuery = hrefWithoutQuery;
        this.query = query;
    }

    public String getHrefWithoutQuery() {
        return hrefWithoutQuery;
    }

    public QueryString getQuery() {
        return query;
    }
}

