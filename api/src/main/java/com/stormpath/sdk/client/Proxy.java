/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.client;

/**
 * HTTP proxy server communication settings, used if the Stormpath SDK Client must communicate through an HTTP Proxy.
 *
 * @since 0.8
 */
public class Proxy {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final boolean authenticationRequired;

    /**
     * Creates a instance reflecting an HTTP proxy server that <em>does not</em> require authentication.
     *
     * @param host the proxy server host.
     * @param port the proxy server host port.
     */
    public Proxy(String host, int port) {
        this(host, port, null, null, false);
    }

    /**
     * Creates an instance reflecting an HTTP proxy server that requires username/password authentication.
     *
     * @param host     the proxy server host.
     * @param port     the proxy server host port.
     * @param username the username to use when authenticating with the proxy server.
     * @param password the password to use when authenticating with the proxy server.
     */
    public Proxy(String host, int port, String username, String password) {
        this(host, port, username, password, true);
    }

    private Proxy(String host, int port, String username, String password, boolean authenticationRequired) {
        if (host == null) throw new IllegalArgumentException("host argument cannot be null");
        if (port < 0 || port > 65535) throw new IllegalArgumentException("port must be be between 0 and 65535");
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.authenticationRequired = authenticationRequired;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("host=").append(host).append(", port=").append(port);
        if (username != null) {
            sb.append(", username=").append(username);
        }
        if (password != null) {
            sb.append(", password=<hidden>");
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (host != null ? host.hashCode() : 0);
        result = prime * result + (password != null ? password.hashCode() : 0);
        result = prime * result + port;
        result = prime * result + (username != null ? username.hashCode() : 0);
        result = prime * result + (authenticationRequired ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Proxy) {
            Proxy p = (Proxy) obj;
            return (host != null ? host.equals(p.getHost()) : p.getHost() == null) &&
                    (port == p.getPort()) &&
                    (username != null ? username.equals(p.getUsername()) : p.getUsername() == null) &&
                    (password != null ? password.equals(p.getPassword()) : p.getPassword() == null) &&
                    (authenticationRequired == p.authenticationRequired);
        }
        return false;
    }
}
