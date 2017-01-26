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
package com.stormpath.spring.cloud.zuul.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.convert.Conversion;
import com.stormpath.sdk.convert.MapToJwtConverter;
import com.stormpath.sdk.convert.ResourceConverter;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.DefaultRuntimeEnvironment;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.lang.RuntimeEnvironment;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.AccountStringResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.json.JsonFunction;
import com.stormpath.spring.boot.autoconfigure.StormpathWebMvcAutoConfiguration;
import com.stormpath.spring.cloud.zuul.config.JwkConfig;
import com.stormpath.spring.cloud.zuul.config.JwkResult;
import com.stormpath.spring.cloud.zuul.config.JwtConfig;
import com.stormpath.spring.cloud.zuul.config.StormpathZuulAccountHeaderConfig;
import com.stormpath.spring.cloud.zuul.config.ValueClaimConfig;
import com.stormpath.zuul.account.ForwardedAccountHeaderFilter;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.ZuulProxyConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.1.0
 */
@Configuration
@ConditionalOnProperty(name = {"stormpath.enabled", "stormpath.web.enabled", "stormpath.zuul.enabled"}, matchIfMissing = true)
@EnableConfigurationProperties(StormpathZuulAccountHeaderConfig.class)
@EnableZuulProxy
@AutoConfigureAfter({ZuulProxyConfiguration.class, StormpathWebMvcAutoConfiguration.class})
public class StormpathZuulAutoConfiguration {

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private AccountResolver accountResolver; //provided by StormpathWebMvcAutoConfiguration

    @Autowired(required = false)
    private ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings({"ELValidationInJSP", "SpringElInspection"})
    @Value("${stormpath.zuul.account.filter.type:pre}")
    private String forwardedAccountFilterType = "pre";

    @Value("${stormpath.zuul.account.filter.order:0}")
    private int forwardedAccountFilterOrder = 0;

    @Autowired
    private StormpathZuulAccountHeaderConfig accountHeader;

    @Autowired(required = false)
    private RuntimeEnvironment runtimeEnvironment = DefaultRuntimeEnvironment.INSTANCE;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Client client;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private Application application;

    @Bean
    @ConditionalOnMissingBean(name = "stormpathJsonFunction")
    public Function<Object, String> stormpathJsonFunction() {
        return new JsonFunction<>(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountMapFunction")
    public Function<Account, ?> stormpathForwardedAccountMapFunction() {
        final ResourceConverter<Account> converter = new ResourceConverter<>();
        Conversion c = accountHeader.getValue();
        converter.setConfig(c);
        return converter;
    }

    /**
     * @since 1.3.0
     */
    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountJwtSigningKey")
    public Key stormpathForwardedAccountJwtSigningKey() {
        return null;
    }

    /**
     * @since 1.3.0
     */
    @SuppressWarnings("Duplicates")
    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountJwtFunction")
    public Function<Map<String, ?>, String> stormpathForwardedAccountJwtFunction() {

        final JwtConfig jwt = accountHeader.getJwt();

        final JwkConfig jwk = jwt.getKey();

        SignatureAlgorithm signatureAlgorithm = null;
        Key key = null;
        String kid = null;

        if (jwk.isEnabled()) {

            Function<SignatureAlgorithm, JwkResult> defaultKeyFunction =
                new ClientApplicationJwkFactory(this.client, this.application);

            Function<JwkConfig, JwkResult> keyFunction = new ConfigJwkFactory(runtimeEnvironment, defaultKeyFunction);

            JwkResult jwkResult = keyFunction.apply(jwk);
            key = jwkResult.getKey();
            kid = jwkResult.getKeyId();
            signatureAlgorithm = jwkResult.getSignatureAlgorithm();
        }

        Map<String, Object> baseHeader = new LinkedHashMap<>();
        if (!Collections.isEmpty(jwt.getHeader())) {
            baseHeader.putAll(jwt.getHeader());
        }
        if (Strings.hasText(kid)) {
            baseHeader.put("kid", kid);
        }

        String valueClaimName = null;
        ValueClaimConfig valueClaim = jwt.getValueClaim();
        if (valueClaim != null && valueClaim.isEnabled()) {
            valueClaimName = valueClaim.getName();
        }

        return new MapToJwtConverter(
            baseHeader,
            jwt.getClaims(),
            valueClaimName,
            signatureAlgorithm,
            key,
            jwt.getExpirationSeconds(),
            jwt.getNotBeforeSeconds());
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountStringFunction")
    public Function<Account, String> stormpathForwardedAccountStringFunction() {

        final Function<Object, String> jsonFunction = stormpathJsonFunction();
        final Function<Account, ?> accountFunction = stormpathForwardedAccountMapFunction();
        final Function<Map<String, ?>, String> jwtFunction = stormpathForwardedAccountJwtFunction();
        final JwtConfig jwt = accountHeader.getJwt();

        return new Function<Account, String>() {
            @SuppressWarnings("unchecked")
            @Override
            public String apply(Account account) {
                Object value = accountFunction.apply(account);

                if (value == null || (value instanceof Map && Collections.isEmpty((Map) value))) {
                    return null;
                }

                if (value instanceof String) {
                    return (String) value;
                }

                if (jwt.isEnabled()) {
                    Assert.isInstanceOf(Map.class, value,
                        "stormpathForwardedAccountMapFunction must return a String or Map<String,?> when using JWT.");
                    @SuppressWarnings("ConstantConditions") Map<String, ?> map = (Map<String, ?>) value;
                    return jwtFunction.apply(map);
                }

                //else JSON:
                return jsonFunction.apply(value);
            }
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountHeaderValueResolver")
    public Resolver<String> stormpathForwardedAccountHeaderValueResolver() {
        AccountStringResolver resolver = new AccountStringResolver();
        resolver.setAccountResolver(accountResolver);
        resolver.setAccountStringFunction(stormpathForwardedAccountStringFunction());
        return resolver;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountHeaderFilter")
    public ZuulFilter stormpathForwardedAccountHeaderFilter() {
        ForwardedAccountHeaderFilter filter = new ForwardedAccountHeaderFilter();
        filter.setFilterType(forwardedAccountFilterType);
        filter.setFilterOrder(forwardedAccountFilterOrder);
        filter.setHeaderName(accountHeader.getName());
        filter.setAccountResolver(accountResolver);
        filter.setValueResolver(stormpathForwardedAccountHeaderValueResolver());
        return filter;
    }
}
