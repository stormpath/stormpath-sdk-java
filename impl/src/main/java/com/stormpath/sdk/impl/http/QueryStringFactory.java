package com.stormpath.sdk.impl.http;

import com.stormpath.sdk.impl.query.DefaultCriteria;
import com.stormpath.sdk.impl.query.DefaultOptions;
import com.stormpath.sdk.impl.query.Expansion;
import com.stormpath.sdk.impl.query.Order;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.query.Criterion;
import com.stormpath.sdk.query.LikeExpression;
import com.stormpath.sdk.query.SimpleExpression;

import java.util.List;
import java.util.Map;

/**
 * @since 0.8
 */
public class QueryStringFactory {

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
