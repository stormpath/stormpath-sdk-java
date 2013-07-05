package com.stormpath.sdk.application;

import com.stormpath.sdk.query.Criteria;

/**
 * @since 0.8
 */
public interface ApplicationCriteria extends Criteria<ApplicationCriteria>, ApplicationOptions<ApplicationCriteria> {

    ApplicationCriteria orderByName();

    ApplicationCriteria orderByDescription();

    ApplicationCriteria orderByStatus();
}
