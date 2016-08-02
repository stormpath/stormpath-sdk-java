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

import com.stormpath.sdk.http.QueryString;
import com.stormpath.sdk.impl.http.support.DefaultCanonicalUri;
import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.impl.query.Expansion;
import com.stormpath.sdk.impl.query.LikeExpression;
import com.stormpath.sdk.impl.query.Order;
import com.stormpath.sdk.impl.query.SimpleExpression;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.query.Criterion;

import java.util.List;
import java.util.Map;

/**
 * @since 0.8
 */
public class QueryStringFactory {

    public QueryString createQueryString(String href, DefaultCriteria criteria) {

        QueryString query = createQueryString(criteria);

        CanonicalUri uri = DefaultCanonicalUri.create(href, null);
        if (uri.hasQuery()) {
            //Query params embedded directly in the href, if any, take precedence. Overwrite any from the criteria:
            query.putAll(uri.getQuery());
        }

        return query;
    }

    @SuppressWarnings("unchecked")
    public QueryString createQueryString(DefaultCriteria criteria) {
        QueryString qs = new QueryString();

        if (criteria == null || criteria.isEmpty()) {
            return qs;
        }

        List<Criterion> criterionList = criteria.getCriterionEntries();
        List<Order> orderList = criteria.getOrderEntries();
        List<Expansion> expansionList = criteria.getExpansions();

        if (!Collections.isEmpty(criterionList)) {
            addCriterionEntries(qs, criterionList);
        }

        if (!orderList.isEmpty()) {
            String orderBy = Strings.collectionToCommaDelimitedString(orderList);
            qs.put("orderBy", orderBy);
        }

        Integer offset = criteria.getOffset();
        if (offset != null) {
            String value = String.valueOf(offset);
            qs.put("offset", value);
        }

        Integer limit = criteria.getLimit();
        if (limit != null) {
            String value = String.valueOf(limit);
            qs.put("limit", value);
        }

        applyExpansions(qs, expansionList);

        return qs;
    }

    public QueryString createQueryString(String href, DefaultOptions defaultOptions) {

        QueryString query = createQueryString(defaultOptions);

        CanonicalUri uri = DefaultCanonicalUri.create(href, null);
        if (uri.hasQuery()) {
            //Query params embedded directly in the href, if any, take precedence. Overwrite any from the criteria:
            query.putAll(uri.getQuery());
        }

        return query;
    }

    public QueryString createQueryString(DefaultOptions options) {
        QueryString qs = new QueryString();
        List<Expansion> expansions = options.getExpansions();
        applyExpansions(qs, expansions);
        return qs;
    }

    private void applyExpansions(QueryString qs, List<Expansion> expansions) {
        if (!Collections.isEmpty(expansions)) {
            String expand = Strings.collectionToCommaDelimitedString(expansions);
            qs.put("expand", expand);
        }
    }

    public QueryString createQueryString(Map<String, ?> params) {
        if (params instanceof QueryString) {
            return (QueryString) params;
        }
        if (params == null) {
            return new QueryString();
        }
        QueryString qs = new QueryString();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String sValue = String.valueOf(value);
            qs.put(key, sValue);
        }
        return qs;
    }

    private void addCriterionEntries(QueryString qs, List<Criterion> entries) {

        for (Criterion c : entries) {
            //yuck - it'd be nice to use the visitor pattern here instead:
            if (c instanceof LikeExpression) {
                LikeExpression le = (LikeExpression) c;
                String queryParamName = le.getPropertyName();
                Object value = le.getValue();
                String sValue = String.valueOf(value);
                String queryParamValue = le.getMatchLocation().toMatchString(sValue);
                qs.put(queryParamName, queryParamValue);
            } else if (c instanceof SimpleExpression) {
                SimpleExpression se = (SimpleExpression) c;
                String queryParamName = se.getPropertyName();
                Object value = se.getValue();
                String queryParamValue = String.valueOf(value);
                qs.put(queryParamName, queryParamValue);
            } else {
                throw new IllegalArgumentException("Unexpected Criterion type: " + c);
            }
        }
    }
}
