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
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.convert.Conversion;
import com.stormpath.sdk.convert.MapToJwtConverter;
import com.stormpath.sdk.convert.ResourceConverter;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Collections;
import com.stormpath.sdk.lang.Function;
import com.stormpath.sdk.servlet.account.AccountResolver;
import com.stormpath.sdk.servlet.account.AccountStringResolver;
import com.stormpath.sdk.servlet.http.Resolver;
import com.stormpath.sdk.servlet.json.JsonFunction;
import com.stormpath.spring.boot.autoconfigure.StormpathWebMvcAutoConfiguration;
import com.stormpath.spring.cloud.zuul.config.JwkConfig;
import com.stormpath.spring.cloud.zuul.config.JwtConfig;
import com.stormpath.spring.cloud.zuul.config.StormpathZuulAccountHeaderConfig;
import com.stormpath.spring.cloud.zuul.config.ValueClaimConfig;
import com.stormpath.zuul.account.ForwardedAccountHeaderFilter;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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

    private static final Logger log = LoggerFactory.getLogger(StormpathZuulAutoConfiguration.class);

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
    protected static SignatureAlgorithm getAlgorithm(byte[] hmacSigningKeyBytes) {
        if (hmacSigningKeyBytes == null || hmacSigningKeyBytes.length == 0) {
            return null;
        }
        if (hmacSigningKeyBytes.length >= 64) {
            return SignatureAlgorithm.HS512;
        } else if (hmacSigningKeyBytes.length >= 48) {
            return SignatureAlgorithm.HS384;
        } else { //<= 32
            return SignatureAlgorithm.HS256;
        }
    }

    /**
     * @since 1.3.0
     */
    @SuppressWarnings("Duplicates")
    @Bean
    @ConditionalOnMissingBean(name = "stormpathForwardedAccountJwtFunction")
    public Function<Map<String, ?>, String> stormpathForwardedAccountJwtFunction() {

        final JwtConfig jwt = accountHeader.getJwt();

        JwkConfig jwk = jwt.getKey();

        if (jwk == null) {
            jwk = new JwkConfig();
        }

        SignatureAlgorithm signatureAlgorithm = null;
        Key key = null;
        boolean keyEnabled = jwk.isEnabled();

        Map<String, Object> baseHeader = new LinkedHashMap<>();
        if (!Collections.isEmpty(jwt.getHeader())) {
            baseHeader.putAll(jwt.getHeader());
        }

        if (keyEnabled) {

            String value = jwk.getAlg();
            if (value != null) {
                signatureAlgorithm = SignatureAlgorithm.forName(value);
            }

            String kid = jwk.getKid();

            key = stormpathForwardedAccountJwtSigningKey(); //check if explicitly provided as a bean

            if (key == null) {

                byte[] bytes = null;

                String encodedKeyBytes = jwk.getK();
                if (encodedKeyBytes != null) {

                    String encoding = jwk.getEncoding();
                    if (encoding == null) {
                        //default to the JWK specification format:
                        encoding = "base64url";
                    }

                    if (encoding.equalsIgnoreCase("base64url")) {
                        bytes = TextCodec.BASE64URL.decode(encodedKeyBytes);
                    } else if (encoding.equalsIgnoreCase("base64")) {
                        bytes = TextCodec.BASE64.decode(encodedKeyBytes);
                    } else if (encoding.equalsIgnoreCase("utf8")) {
                        bytes = encodedKeyBytes.getBytes(StandardCharsets.UTF_8);
                    } else {
                        throw new IllegalArgumentException("Unsupported encoding '" + encoding + "'.  Supported " +
                            "encodings: base64url, base64, utf8");
                    }
                }

                if (bytes != null && bytes.length > 0) {

                    if (signatureAlgorithm == null) {
                        //choose the best available alg based on available key:
                        signatureAlgorithm = getAlgorithm(bytes);
                    }

                    if (!signatureAlgorithm.isHmac()) {
                        String algName = signatureAlgorithm.name();
                        String msg = "The stormpath.zuul.account.header.jwt.key.k property may only be specified " +
                            "when the stormpath.zuul.account.header.jwt.key.alg value equals HS256, HS384, or HS512. " +
                            "The specified stormpath.zuul.account.header.jwt.key.alg value is " +  algName + ". " +
                            "When using " + algName + ", please please define a bean named " +
                            "'stormpathForwardedAccountJwtSigningKey' that returns an " +
                            signatureAlgorithm.getFamilyName() + " private key instance.";
                        throw new IllegalArgumentException(msg);
                    }

                    key = new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
                }
            }

            if (key == null) {

                //a key was not provided as a bean, nor was one configured via app config properties.
                //fall back to the Stormpath Client API Key Secret as the signing key

                //fall back to client api key secret:
                ApiKey apiKey = client.getApiKey();

                //Set a 'kid' equal to the api key href:
                String href = application.getHref();
                int i = href.indexOf("/applications/");
                href = href.substring(0, i);
                href += "/apiKeys/" + apiKey.getId();
                kid = href;

                String secret = apiKey.getSecret();
                byte[] bytes = TextCodec.BASE64.decode(secret);

                SignatureAlgorithm defaultSigAlg = getAlgorithm(bytes);
                if (signatureAlgorithm == null) {
                    signatureAlgorithm = defaultSigAlg;
                }
                if (!signatureAlgorithm.isHmac()) {
                    String msg = "Unable to use specified JWT signature algorithm '" + signatureAlgorithm + "' when " +
                        "creating X-Forwarded-User JWTs, as this algorithm is incompatible with the " +
                        "fallback/default Stormpath Client ApiKey secret signing key.  Defaulting to '" +
                        defaultSigAlg + "'.  To avoid this message, either 1) do not specify a signature algorithm to " +
                        "let the framework choose an algorithm appropriate for the default signing key, or 2) define " +
                        "a 'stormpathForwardedAccountJwtSigningKey' bean of type java.security.Key that is " +
                        "compatible with your specified signature algorithm.";
                    log.warn(msg);
                    signatureAlgorithm = defaultSigAlg;
                }

                key = new SecretKeySpec(bytes, signatureAlgorithm.getJcaName());
            }

            if (kid != null) {
                baseHeader.put("kid", kid);
            }
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
            @Override
            public String apply(Account account) {
                Object value = accountFunction.apply(account);

                if (value == null || (value instanceof Map && Collections.isEmpty((Map)value))) {
                    return null;
                }

                if (value instanceof String) {
                    return (String)value;
                }

                if (jwt.isEnabled()) {
                    Assert.isInstanceOf(Map.class, value,
                        "stormpathForwardedAccountMapFunction must return a String or Map<String,?> when using JWT.");
                    Map<String,?> map = (Map<String,?>)value;
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
