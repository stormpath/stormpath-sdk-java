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
package com.stormpath.spring.boot.autoconfigure;

import com.stormpath.sdk.lang.Assert;

/**
 * Configuration properties common to Stormpath-specific {@link com.stormpath.sdk.servlet.mvc.Controller}
 * implementations that render views.
 *
 * @since 1.0.RC4
 */
public abstract class ViewControllerProperties extends ControllerProperties {

    private String nextUri;
    private String view;

    public ViewControllerProperties(String defaultUri, String defaultNextUri, String view) {
        super(defaultUri);
        Assert.hasText(defaultNextUri, "defaultNextUri cannot be null or empty.");
        this.nextUri = defaultNextUri;
        Assert.hasText(view, "view cannot be null or empty.");
        this.view = view;
    }

    public String getNextUri() {
        return nextUri;
    }

    public void setNextUri(String nextUri) {
        Assert.hasText(nextUri, "nextUri cannot be null or empty.");
        this.nextUri = nextUri;
    }

    /**
     * Returns the view name of the view to be rendered. Note that for filename-based view resolvers, this view name is
     * usually relative to a view resolver prefix and suffix.  For example:
     *
     * <pre>
     * prefix: classpath:/templates/
     * suffix: .html
     * </pre>
     *
     * With a {@code view} value of {@code stormpath/foo}, the page template then is expected to be at {@code
     * classpath:/templates/stormpath/foo.html}.
     */
    public String getView() {
        return view;
    }

    public void setView(String view) {
        Assert.hasText(view, "view cannnot be null or empty.");
        this.view = view;
    }
}
