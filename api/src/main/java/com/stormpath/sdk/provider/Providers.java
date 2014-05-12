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
package com.stormpath.sdk.provider;

import com.stormpath.sdk.lang.Classes;

/**
 * Static utility/helper methods serving Provider-specific {@link ProviderRequestFactory}s. For example, to
 * construct an account request:
 * <pre>
 * ProviderAccountResult result = application.getAccount(<b>Providers.GOOGLE.account()
 *     .setAccessToken("ya29.1.AADtN_VdFRceSUrSpQWlh4m38igAjOLxCsTx1B7HRWzWoD4RsphiTheV66_56Mhi")
 *     .build()</b>);</b>
 * </pre>
 * or, to create a new Provider-based {@link com.stormpath.sdk.directory.Directory}:
 * <pre>
 * Directory directory = client.instantiate(Directory.class);
 * directory.setName("My Facebook Directory");
 * ...
 * directory = tenant.createDirectory(Directories.newCreateRequestFor(directory)
 *     .forProvider(<b>Providers.FACEBOOK.builder()
 *                  .setClientId("624508218317020")
 *                  .setClientSecret("d0ad961d45fgc0210c0c7d67e8f1w800")
 *                  .build()</b>)
 *     .build());
 * </pre>
 *
 * @since 1.0.beta
 */
public final class Providers {

    //prevent instantiation
    private Providers() {
    }

    /**
     * Returns a new {@link GoogleRequestFactory} instance, used to construct Google requests, like Google Directory creation.
     *
     * @return a new {@link GoogleRequestFactory} instance, used to construct Google requests, like Google Directory creation.
     */
    public static final GoogleRequestFactory GOOGLE = (GoogleRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultGoogleRequestFactory");

    /**
     * Returns a new {@link FacebookRequestFactory} instance, used to construct Facebook requests, like Facebook Account creation and retrieval.
     *
     * @return a new {@link FacebookRequestFactory} instance, used to construct Facebook requests, like Facebook Account creation and retrieval.
     */
    public static final FacebookRequestFactory FACEBOOK = (FacebookRequestFactory) Classes.newInstance("com.stormpath.sdk.impl.provider.DefaultFacebookRequestFactory");

}

