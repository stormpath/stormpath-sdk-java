/*
 * Copyright 2012 Stormpath, Inc.
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
package com.stormpath.sdk.authc;

/**
 * @since 0.2
 */
public class UsernamePasswordRequest implements AuthenticationRequest<String, char[]> {

    private String username;
    private char[] password;
    private String host;

    public UsernamePasswordRequest(String username, String password) {
        this(username, password, null);
    }

    public UsernamePasswordRequest(String username, char[] password) {
        this(username, password, null);
    }

    public UsernamePasswordRequest(String username, String password, String host) {
        this(username, password != null ? password.toCharArray() : "".toCharArray(), host);
    }

    public UsernamePasswordRequest(String username, char[] password, String host) {
        this.username = username;
        this.password = password;
        this.host = host;
    }

    @Override
    public String getPrincipals() {
        return username;
    }

    @Override
    public char[] getCredentials() {
        return password;
    }

    @Override
    public String getHost() {
        return this.host;
    }

    /**
     * Clears out (nulls) the username, password, and host.  The password bytes are explicitly set to
     * <tt>0x00</tt> to eliminate the possibility of memory access at a later time.
     */
    @Override
    public void clear() {
        this.username = null;
        this.host = null;

        char[] password = this.password;
        this.password = null;

        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = 0x00;
            }
        }

    }
}
