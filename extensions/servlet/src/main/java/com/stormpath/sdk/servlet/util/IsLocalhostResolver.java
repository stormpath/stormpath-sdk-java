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
package com.stormpath.sdk.servlet.util;

import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.servlet.http.Resolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * @since 1.0.RC3
 */
public class IsLocalhostResolver implements Resolver<Boolean> {

    private static final List<String> REMOTE_ADDR_HEADERS = Arrays
        .asList("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR");

    protected boolean isValidIp(String ip) {
        return Strings.hasText(ip) && !"unknown".equalsIgnoreCase(ip);
    }

    protected String getRemoteAddr(HttpServletRequest request) {

        String ip = null;
        for (String headerName : REMOTE_ADDR_HEADERS) {
            ip = request.getHeader(headerName);
            if (ip == null) {
                continue;
            }

            int i = ip.indexOf(',');
            if (i != -1) {
                ip = Strings.clean(ip.substring(0, i));
            }

            if (isValidIp(ip)) {
                break;
            }
        }

        if (!isValidIp(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    @Override
    public Boolean get(HttpServletRequest request, HttpServletResponse response) {

        String host = getRemoteAddr(request);

        return host != null && (host.equalsIgnoreCase("localhost") ||
                                host.equals("127.0.0.1") ||
                                host.startsWith("::1") ||
                                host.startsWith("0:0:0:0:0:0:0:1"));
    }
}
