package com.stormpath.spring.config;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeyBuilder;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.lang.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StormpathTwoAppTenantConfiguration extends AbstractStormpathConfiguration {
    @Value("#{environment.STORMPATH_API_KEY_ID_TWO_APP}")
    protected String apiKeyId;

    @Value("#{environment.STORMPATH_API_KEY_SECRET_TWO_APP}")
    protected String apiKeySecret;


    @Bean
    public ApiKey stormpathClientApiKey() {
        if (Strings.hasText(apiKeyId) && Strings.hasText(apiKeySecret)) {
            ApiKeyBuilder builder = ApiKeys.builder();
            builder.setId(apiKeyId);
            builder.setSecret(apiKeySecret);
            return builder.build();
        }
        return super.stormpathClientApiKey();
    }

    @Bean
    public Application stormpathApplication() {
        return super.stormpathApplication();
    }

    @Bean
    public com.stormpath.sdk.cache.CacheManager stormpathCacheManager() {
        return super.stormpathCacheManager();
    }

    @Bean
    public Client stormpathClient() {
        return super.stormpathClient();
    }
}
