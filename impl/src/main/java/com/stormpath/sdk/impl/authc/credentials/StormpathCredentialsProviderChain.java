/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import com.stormpath.sdk.authc.StormpathCredentials;
import com.stormpath.sdk.authc.StormpathCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class StormpathCredentialsProviderChain implements StormpathCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(StormpathCredentialsProviderChain.class);

    private List<StormpathCredentialsProvider> stormpathCredentialsProviders;

    public StormpathCredentialsProviderChain() {
        this.stormpathCredentialsProviders = new ArrayList<>();
    }

    public void addStormpathCredentialProviders(StormpathCredentialsProvider... stormpathCredentialsProviders) {
        for (StormpathCredentialsProvider stormpathCredentialsProvider : stormpathCredentialsProviders) {
            this.stormpathCredentialsProviders.add(stormpathCredentialsProvider);
        }
    }

    @Override
    public StormpathCredentials getStormpathCredentials() {

        StormpathCredentials stormpathCredentials = null;

        for (StormpathCredentialsProvider stormpathCredentialsProvider : stormpathCredentialsProviders) {
            try {
                stormpathCredentials = stormpathCredentialsProvider.getStormpathCredentials();
            } catch (Throwable throwable) {
                log.debug("Unable to load credentials from " + stormpathCredentialsProvider.toString() +
                        ": " + throwable.getMessage());
            }
        }

        if (stormpathCredentials == null) {
            throw new IllegalStateException("Unable to load credentials from any provider in the chain.");
        }

        return stormpathCredentials;
    }
}
