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
package com.stormpath.sdk.impl.provider.saml;

import com.stormpath.sdk.impl.provider.DefaultCreateProviderRequest;
import com.stormpath.sdk.impl.provider.IdentityProviderType;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.provider.CreateProviderRequest;
import com.stormpath.sdk.provider.saml.CreateSamlProviderRequestBuilder;
import com.stormpath.sdk.saml.AttributeStatementMappingRules;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.RC8
 */
public class DefaultCreateSamlProviderRequestBuilder implements CreateSamlProviderRequestBuilder {

    private String ssoLoginUrl;
    private String ssoLogoutUrl;
    private String encodedX509SigningCert;
    private String requestSignatureAlgorithm;
    private AttributeStatementMappingRules attributeStatementMappingRules;

    protected String getConcreteProviderId() {
        return IdentityProviderType.SAML.getNameKey();
    }

    @Override
    public CreateSamlProviderRequestBuilder setSsoLoginUrl(String ssoLoginUrl) {
        Assert.notNull(ssoLoginUrl, "ssoLoginUrl cannot be null or empty.");
        this.ssoLoginUrl = ssoLoginUrl;
        return this;
    }

    @Override
    public CreateSamlProviderRequestBuilder setSsoLogoutUrl(String ssoLogoutUrl) {
        Assert.notNull(ssoLogoutUrl, "ssoLogoutUrl cannot be null or empty.");
        this.ssoLogoutUrl = ssoLogoutUrl;
        return this;
    }

    @Override
    public CreateSamlProviderRequestBuilder setRequestSignatureAlgorithm(String requestSignatureAlgorithm) {
        Assert.notNull(requestSignatureAlgorithm, "requestSignatureAlgorithm cannot be null or empty.");
        this.requestSignatureAlgorithm = requestSignatureAlgorithm;
        return this;
    }

    @Override
    public CreateSamlProviderRequestBuilder setEncodedX509SigningCert(String encodedX509SigningCert) {
        Assert.notNull(encodedX509SigningCert, "encodedX509SigningCert cannot be null or empty.");
        this.encodedX509SigningCert = encodedX509SigningCert;
        return this;
    }

    @Override
    public CreateSamlProviderRequestBuilder setAttributeStatementMappingRules(AttributeStatementMappingRules attributeStatementMappingRules) {
        Assert.notNull(attributeStatementMappingRules, "attributeStatementMappingRules cannot be null or empty.");
        this.attributeStatementMappingRules = attributeStatementMappingRules;
        return this;
    }

    protected CreateProviderRequest doBuild(Map <String, Object> map) {
        DefaultSamlProvider provider = new DefaultSamlProvider(null, map);

        provider.setEncodedX509SigningCert(encodedX509SigningCert);
        provider.setRequestSignatureAlgorithm(requestSignatureAlgorithm);
        provider.setSsoLoginUrl(ssoLoginUrl);
        provider.setSsoLogoutUrl(ssoLogoutUrl);
        if (attributeStatementMappingRules != null){
            provider.setAttributeStatementMappingRules(attributeStatementMappingRules);
        }

        return new DefaultCreateProviderRequest(provider);
    }

    public CreateProviderRequest build() {

        final String providerId = getConcreteProviderId();
        Assert.state(Strings.hasText(providerId), "The providerId property is missing.");

        Assert.state(Strings.hasText(encodedX509SigningCert), "The encodedX509SigningCert property is missing.");
        Assert.state(Strings.hasText(requestSignatureAlgorithm), "The requestSignatureAlgorithm property is missing.");
        Assert.state(Strings.hasText(ssoLoginUrl), "The ssoLoginUrl property is missing.");
        Assert.state(Strings.hasText(ssoLogoutUrl), "The ssoLogoutUrl property is missing.");

        Map<String, Object> properties = new LinkedHashMap<String, Object>();
        properties.put("providerId", providerId);

        return doBuild(Collections.unmodifiableMap(properties));
    }
}
