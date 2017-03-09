package com.stormpath.sdk.client;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyOptions;
import com.stormpath.sdk.api.ApiKeyStatus;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.tenant.Tenant;

/**
 * Created by lhazlewood on 3/9/17.
 */
public class DefaultPairedApiKey implements PairedApiKey {

    private final ApiKey primary;
    private ApiKey secondary;

    public DefaultPairedApiKey(ApiKey primary, ApiKey secondary) {
        Assert.notNull(primary, "primary ApiKey cannot be null.");
        Assert.notNull(secondary, "secondary ApiKey cannot be null.");
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public ApiKey getSecondaryApiKey() {
        return secondary;
    }

    @Override
    public void setSecondaryApiKey(ApiKey secondary) {
        this.secondary = secondary;
    }

    @Override
    public void save() {
        this.primary.save();
    }

    @Override
    public void delete() {
        this.primary.delete();
    }

    @Override
    public String getHref() {
        return this.primary.getHref();
    }

    @Override
    public String getId() {
        return this.primary.getId();
    }

    @Override
    public String getSecret() {
        return this.primary.getSecret();
    }

    @Override
    public ApiKeyStatus getStatus() {
        return this.primary.getStatus();
    }

    @Override
    public void setStatus(ApiKeyStatus status) {
        this.primary.setStatus(status);
    }

    @Override
    public Account getAccount() {
        return this.primary.getAccount();
    }

    @Override
    public Tenant getTenant() {
        return this.primary.getTenant();
    }

    @Override
    public void save(ApiKeyOptions options) {
        this.primary.save(options);
    }
}
