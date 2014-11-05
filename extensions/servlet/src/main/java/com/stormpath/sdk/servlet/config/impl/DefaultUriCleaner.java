/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.config.impl;

import com.stormpath.sdk.servlet.config.UriCleaner;

public class DefaultUriCleaner implements UriCleaner {

    @Override
    public String clean(String uri) {

        //remove any query:
        int i = uri.indexOf('?');
        if (i != -1) {
            uri = uri.substring(0, i);
        }

        //remove any uri params:
        i = uri.indexOf(';');
        if (i != -1) {
            uri = uri.substring(0, i);
        }

        //remove any uri fragment:

        i = uri.indexOf('#');
        if (i != -1) {
            uri = uri.substring(0, i);
        }

        return uri;
    }
}
