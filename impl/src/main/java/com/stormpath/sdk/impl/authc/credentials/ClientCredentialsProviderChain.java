/*
 * Copyright 2016 Stormpath, Inc.
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

    protected List<ClientCredentialsProvider> getClientCredentialsProviders(){
        return clientCredentialsProviders;
    }
}
