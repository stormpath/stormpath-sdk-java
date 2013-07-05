package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationOptions;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.impl.query.DefaultCriteria;

/**
 * @since 0.8
 */
public class DefaultApplicationCriteria extends DefaultCriteria<ApplicationCriteria, ApplicationOptions> implements ApplicationCriteria {

    public DefaultApplicationCriteria() {
        super(new DefaultApplicationOptions());
    }

    @Override
    public ApplicationCriteria orderByName() {
        return orderBy(Applications.NAME);
    }

    @Override
    public ApplicationCriteria orderByDescription() {
        return orderBy(Applications.DESCRIPTION);
    }

    @Override
    public ApplicationCriteria orderByStatus() {
        return orderBy(Applications.STATUS);
    }

    public ApplicationCriteria expandAccounts() {
        getOptions().expandAccounts();
        return this;
    }

    public ApplicationCriteria expandAccounts(int limit) {
        getOptions().expandAccounts(limit);
        return this;
    }

    public ApplicationCriteria expandAccounts(int limit, int offset) {
        getOptions().expandAccounts(limit, offset);
        return this;
    }

    public ApplicationCriteria expandGroups() {
        getOptions().expandGroups();
        return this;
    }

    public ApplicationCriteria expandGroups(int limit) {
        getOptions().expandGroups(limit);
        return this;
    }

    public ApplicationCriteria expandGroups(int limit, int offset) {
        getOptions().expandGroups(limit, offset);
        return this;
    }

    public ApplicationCriteria expandTenant() {
        getOptions().expandTenant();
        return this;
    }
}
