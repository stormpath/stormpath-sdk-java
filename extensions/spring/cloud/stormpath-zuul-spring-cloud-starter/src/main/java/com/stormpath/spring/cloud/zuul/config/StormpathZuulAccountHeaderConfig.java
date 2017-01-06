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
package com.stormpath.spring.cloud.zuul.config;

import com.stormpath.sdk.convert.Conversion;
import com.stormpath.sdk.convert.ResourceConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @since 1.3.0
 */
@ConfigurationProperties("stormpath.zuul.account.header")
public class StormpathZuulAccountHeaderConfig {

    @SuppressWarnings("WeakerAccess")
    public static final String DEFAULT_NAME = "X-Forwarded-User";

    private String name;

    private Conversion value; //config for how the value should be rendered, not an actual value

    private JwtConfig jwt;

    public StormpathZuulAccountHeaderConfig() {
        this.name = DEFAULT_NAME;
        this.value = ResourceConverter.DEFAULT_CONFIG;
        this.jwt = new JwtConfig();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Conversion getValue() {
        return value;
    }

    public void setValue(Conversion value) {
        this.value = value;
    }

    public JwtConfig getJwt() {
        return jwt;
    }

    public void setJwt(JwtConfig jwt) {
        this.jwt = jwt;
    }
}
