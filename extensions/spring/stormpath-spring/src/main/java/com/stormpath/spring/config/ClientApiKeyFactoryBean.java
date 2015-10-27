/*
 * Copyright 2015 Stormpath, Inc.
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
package com.stormpath.spring.config;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * A Spring {@link org.springframework.beans.factory.FactoryBean FactoryBean} that produces a singleton {@link ApiKey}
 * that will be used to authentication communication requests sent by a {@link com.stormpath.sdk.client.Client Client}
 * to the Stormpath REST API.  <b>This is primarily for
 * Spring XML configuration.  If using Spring Java Config, consider using {@link ApiKeys#builder() ApiKeys.builder()}
 * instead.</b>
 *
 * <p>The resulting {@code ApiKey} instance is expected to be used when constructing the Client instance.  For
 * example, in conjunction with the {@link com.stormpath.spring.config.ClientFactoryBean ClientFactoryBean}.</p>
 *
 * @see com.stormpath.spring.config.ClientFactoryBean ClientFactoryBean
 * @since 1.0.RC4
 */
public class ClientApiKeyFactoryBean extends AbstractFactoryBean<ApiKey> {

    private Resource file;

    private ApiKeyBuilder builder = ApiKeys.builder();

    /**
     * Sets the apiKey ID that will be used when authenticating the client requests to the Stormpath REST API.  If
     * unspecified, property value lookup heuristics will be used to find the apiKeyId value.
     *
     * @param id the apiKey ID that will be used when authenticating the client requests to the Stormpath REST API.
     */
    public void setId(String id) {
        builder.setId(id);
    }

    /**
     * Sets the ApiKey secret that will be used when authenticating client requests to the Stormpath REST API.  If
     * unspecified, property value lookup heuristics will be used to find the apiKeyId value.
     *
     * <p>For security reasons, it is <em>strongly</em> recommended that you do not configure raw secret values in
     * Spring configuration that can be seen by other developers and checked in to version control.  Spring property
     * placeholders and/or environment variables are recommended instead.</p>
     *
     * @param secret the ApiKey secret that will be used when authenticating client requests to the Stormpath REST API.
     */
    public void setSecret(String secret) {
        builder.setSecret(secret);
    }

    /**
     * Sets the {@code apiKey.properties} file that will be used to look up the ApiKey id and secret. {@link
     * #setFileIdPropertyName(String) fileIdPropertyName} is the name of the property used to acquire the id value and
     * defaults to {@code apiKey.id}.  {@link #setFileSecretPropertyName(String) fileSecretPropertyName} is the name of
     * the property used to acquire the secret value and defaults to {@code apiKey.secret}.
     *
     * @param file the {@code apiKey.properties} file that will be used to look up the ApiKey id and secret.
     */
    public void setFile(Resource file) {
        this.file = file;
    }

    /**
     * Sets the name of the property in any properties file discovered that represents the ApiKey ID value, defaults to
     * {@code apiKey.id}.
     *
     * @param fileIdPropertyName the name of the property in any properties file discovered that represents the ApiKey
     *                           ID value, defaults to {@code apiKey.id}.
     */
    public void setFileIdPropertyName(String fileIdPropertyName) {
        builder.setIdPropertyName(fileIdPropertyName);
    }

    /**
     * Sets the name of the property in any properties file discovered that represents the ApiKey secret value, defaults
     * to {@code apiKey.secret}.
     *
     * @param fileSecretPropertyName the name of the property in any properties file discovered that represents the
     *                               ApiKey secret value, defaults to {@code apiKey.secret}.
     */
    public void setFileSecretPropertyName(String fileSecretPropertyName) {
        builder.setSecretPropertyName(fileSecretPropertyName);
    }

    @Override
    public Class<?> getObjectType() {
        return ApiKey.class;
    }

    @Override
    protected ApiKey createInstance() throws Exception {

        if (file != null && file.exists()) {
            InputStream is = file.getInputStream();
            builder.setInputStream(is);
        }

        return builder.build();
    }
}
