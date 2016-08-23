/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public abstract class ClientCredentialsProviderChain implements ClientCredentialsProvider {

    private static final Logger log = LoggerFactory.getLogger(ClientCredentialsProviderChain.class);

    private List<ClientCredentialsProvider> clientCredentialsProviders;

    public ClientCredentialsProviderChain() {
        this.clientCredentialsProviders = new ArrayList<>();
    }

    public void addClientCredentialsProviders(ClientCredentialsProvider... clientCredentialsProviders) {
        for (ClientCredentialsProvider clientCredentialsProvider : clientCredentialsProviders) {
            this.clientCredentialsProviders.add(clientCredentialsProvider);
        }
    }

    @Override
    public ClientCredentials getClientCredentials() {

        for (ClientCredentialsProvider clientCredentialsProvider : clientCredentialsProviders) {
            try {
                return clientCredentialsProvider.getClientCredentials();
            } catch (Throwable throwable) {
                log.debug("Unable to load credentials from " + clientCredentialsProvider.toString() +
                        ": " + throwable.getMessage());
            }
        }

        throw new IllegalStateException("Unable to load credentials from any provider in the chain.");

    }
}
