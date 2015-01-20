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
package com.stormpath.sdk.servlet.http.impl;

import com.stormpath.sdk.lang.Assert;

import java.io.Serializable;

/**
 * @since 1.0.RC3
 */
public class ResourceReference implements Serializable {

    private final String resourceClassName;
    private final String href;

    public ResourceReference(String resourceClassName, String href) {
        Assert.hasText(resourceClassName, "resourceClassName cannot be null or empty.");
        Assert.hasText(href, "href cannot be null or empty.");
        this.resourceClassName = resourceClassName;
        this.href = href;
    }

    public String getResourceClassName() {
        return resourceClassName;
    }

    public String getHref() {
        return href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceReference)) {
            return false;
        }

        ResourceReference that = (ResourceReference) o;

        return href.equals(that.href) && resourceClassName.equals(that.resourceClassName);
    }

    @Override
    public int hashCode() {
        int result = href.hashCode();
        result = 31 * result + resourceClassName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ResourceReference{" +
               "resourceClassName='" + resourceClassName + '\'' +
               ", href='" + href + '\'' +
               '}';
    }
}
