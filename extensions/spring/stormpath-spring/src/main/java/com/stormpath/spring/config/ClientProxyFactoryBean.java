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

import com.stormpath.sdk.client.Proxy;
import com.stormpath.sdk.lang.Assert;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.util.StringUtils;

/**
 * A Spring {@link org.springframework.beans.factory.FactoryBean FactoryBean} that produces a singleton {@link Proxy}
 * instance representing HTTP Proxy settings.
 *
 * <p>The resulting {@code Proxy} instance is expected to be used when constructing the Client instance.  For example,
 * in conjunction with the {@link com.stormpath.spring.config.ClientFactoryBean ClientFactoryBean}.</p>
 *
 * @see com.stormpath.spring.config.ClientFactoryBean#setProxy(com.stormpath.sdk.client.Proxy)
 * @since 1.0.RC4
 */
public class ClientProxyFactoryBean extends AbstractFactoryBean<Proxy> {

    private String host;

    private int port = 80;

    private String username;

    private String password;

    /**
     * Sets the proxy server host name or IP address.
     *
     * @param host the proxy server host name or IP address.
     * @see
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the proxy server host port.  If unspecified, defaults to {@code 80}.
     *
     * @param port the proxy server host port.  If unspecified, defaults to {@code 80}.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the username to use to authenticate connections to the proxy server if proxy server authentication is
     * required.  If proxy server authentication is not required, do not set this property.
     *
     * @param username the username to use to authenticate connections to the proxy server if proxy server
     *                 authentication is required.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password to use to authenticate connections to the proxy server if proxy server authentication is
     * required. If proxy server authentication is not required, do not set this property.
     *
     * @param password the password to use to authenticate connections to the proxy server if proxy server
     *                 authentication is required.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Class<?> getObjectType() {
        return Proxy.class;
    }

    @Override
    protected Proxy createInstance() throws Exception {

        Assert.hasText(host, "Proxy host must be specified.");
        Assert.isTrue(port > 0 && port < 65535, "Proxy port must be greater than zero and less than 65535");

        Proxy proxy;

        if (StringUtils.hasText(username) || StringUtils.hasText(password)) {
            proxy = new Proxy(host, port, username, password);
        } else {
            proxy = new Proxy(host, port);
        }

        return proxy;
    }
}
