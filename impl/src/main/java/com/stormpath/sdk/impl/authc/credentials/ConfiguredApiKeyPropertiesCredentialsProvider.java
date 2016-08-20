/*
 * Copyright (c) 2016 Stormpath, Inc.  All rights reserved.
 */
package com.stormpath.sdk.impl.authc.credentials;

import java.util.Properties;

public class ConfiguredApiKeyPropertiesCredentialsProvider extends AbstractApiKeyCredentialsProvider {

    public ConfiguredApiKeyPropertiesCredentialsProvider(){

    }

    @Override
    protected Properties loadProperties() {
        return null;
    }
}
