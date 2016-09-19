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
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.AccountStringResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.json.JsonFunction;
import com.stormpath.sdk.servlet.json.ResourceJsonFunction;
import com.stormpath.sdk.servlet.mvc.ResourceMapFunction;
import com.stormpath.spring.boot.autoconfigure.StormpathWebMvcAutoConfiguration;
import com.stormpath.zuul.account.ForwardedAccountHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.ZuulProxyConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Set;

/**
 * @since 1.1.0
 */
@Configuration
@ConditionalOnProperty(name = {"stormpath.enabled", "stormpath.web.enabled", "stormpath.zuul.enabled"}, matchIfMissing = true)
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

    @Value("${stormpath.zuul.account.header.name:X-Forwarded-Account}")
    private String forwardedAccountHeaderName = "X-Forwarded-Account";

    @Value("#{ @environment['stormpath.zuul.account.header.includedProperties'] ?: {'groups', 'customData'} }")
    private Set<String> accountToMapConverterIncludedFields = Collections.toSet("groups", "customData");

    //complex objects other than custom data are excluded by default - nothing more to exclude by default:
    @Value("#{ @environment['stormpath.zuul.account.header.excludedProperties'] ?: {} }")
    private Set<String> accountToMapConverterExcludedFields = java.util.Collections.emptySet();

    @Bean
    @ConditionalOnMissingBean(name = "stormpathJsonFunction")
    public Function<Object, String> stormpathJsonFunction() {
        return new JsonFunction<>(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountMapFunction")
    public Function<Account, Map<String, Object>> stormpathForwardedAccountMapFunction() {
        ResourceMapFunction<Account> converter = new ResourceMapFunction<>();
        converter.setIncludedFields(accountToMapConverterIncludedFields);
        converter.setExcludedFields(accountToMapConverterExcludedFields);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountStringFunction")
    public Function<Account, String> stormpathForwardedAccountStringFunction() {
        return new ResourceJsonFunction<>(stormpathForwardedAccountMapFunction(), stormpathJsonFunction());
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
        filter.setHeaderName(forwardedAccountHeaderName);
        filter.setAccountResolver(accountResolver);
        filter.setValueResolver(stormpathForwardedAccountHeaderValueResolver());
        return filter;
    }
}
