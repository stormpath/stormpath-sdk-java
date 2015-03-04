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

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stormpath.web.head")
public class StormpathHeadTemplateProperties {

    private String view = "stormpath/head";
    private String fragmentSelector = "head";

    /**
     * Returns the view name of the {@code head} template which contains a fragment usable in other page templates'
     * html &lt;head&gt; element.
     * <p/>
     * Note that for filename-based view resolvers, this view name is usually relative to a
     * view resolver prefix and suffix.  For example:
     *
     * <pre>
     * prefix: classpath:/templates/
     * suffix: .html
     * </pre>
     *
     * With a {@code view} value of {@code stormpath/head}, the head template then is expected to be at
     * {@code classpath:/templates/stormpath/head.html}.
     *
     * @see #getFragmentSelector()
     */
    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    /**
     * Returns the name of the fragment within the {@link #view view} template that may be included in other
     * page templates. For example, given a {@code head} template file with the following contents:
     *
     * <pre>
     * &lt;html&gt;
     *   &lt;head th:fragment=&quot;<b><code>content</code></b>&quot;&gt;
     *     ...
     *   &lt;/head&gt;
     *   &lt;body&gt;
     *   &lt;/body&gt;
     * &lt;/html&gt;
     * </pre>
     *
     * The string literal {@code content} is the dom selector value that identifies a fragment which can be
     * {@code include}d or {@code replace}d in other page templates.  Other templates can then reference the fragment
     * by the dom selector name:
     *
     * <pre>
     * &lt;html xmlns:th=&quot;http://www.thymeleaf.org&quot;&gt;
     * &lt;head&gt;
     *   &lt;title&gt;Whatever&lt;/title&gt;
     *   &lt;!--/&#42;/ &lt;th:block th:include=&quot;head :: <b><code>content</code></b>"/&gt; /&#42;/--&gt;
     * &lt;/head&gt;
     * &lt;body&gt;
     *    ...
     * &lt;/body&gt;
     * &lt;/html&gt;
     * </pre>
     *
     * @see #getView()
     */
    public String getFragmentSelector() {
        return fragmentSelector;
    }

    public void setFragmentSelector(String fragmentSelector) {
        this.fragmentSelector = fragmentSelector;
    }
}
