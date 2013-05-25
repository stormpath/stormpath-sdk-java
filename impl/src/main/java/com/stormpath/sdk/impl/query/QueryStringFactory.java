package com.stormpath.sdk.impl.query;

import com.stormpath.sdk.impl.http.QueryString;
import com.stormpath.sdk.impl.util.StringUtils;

import java.util.List;

/**
 * @since 2013-03-14
 */
public class QueryStringFactory {

    public QueryString createQueryString(Criteria criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return null;
        }
        List<Criterion> criterionList = criteria.getCriterionEntries();
        List<Order> orderList = criteria.getOrderEntries();
        List<Expansion> expansionList = criteria.getExpansionEntries();

        QueryString qs = new QueryString();

        for(Criterion c : criterionList) {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        if (!orderList.isEmpty()) {
            String orderBy = StringUtils.collectionToCommaDelimitedString(orderList);
            qs.put("orderBy", orderBy);
        }

        if (!expansionList.isEmpty()) {
            //TODO: ensure CollectionExpansion does toString correctly.
            String expand = StringUtils.collectionToCommaDelimitedString(orderList);
            qs.put("expand", expand);
        }

        return qs;
    }
}
