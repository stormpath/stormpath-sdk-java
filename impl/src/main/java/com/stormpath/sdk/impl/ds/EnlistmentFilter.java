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
package com.stormpath.sdk.impl.ds;

import com.stormpath.sdk.impl.resource.*;
import com.stormpath.sdk.impl.util.*;
import com.stormpath.sdk.lang.*;

import java.util.*;

/**
 * Fix for https://github.com/stormpath/stormpath-sdk-java/issues/47. Data map is now shared among all Resource
 * instances referencing the same {@code href}.
 *
 * @since 1.0.RC3
 */
public class EnlistmentFilter implements Filter {

    private final Map<String, Enlistment> hrefMapStore;

    public EnlistmentFilter() {
        this.hrefMapStore = new SoftHashMap<String, Enlistment>();
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request, FilterChain chain) {

        ResourceDataResult result = chain.filter(request);

        Map<String,Object> data = result.getData();

        if (request.getAction() == ResourceAction.DELETE) {
            hrefMapStore.remove(result.getUri().getAbsolutePath());
        } else if (AbstractInstanceResource.isInstanceResource(data)) {
            data = toEnlistment(data);
            result = new DefaultResourceDataResult(result.getAction(), result.getUri(), result.getResourceClass(), data);
        }

        return result;
    }

    @SuppressWarnings({ "SuspiciousMethodCalls", "unchecked" })
    private Enlistment toEnlistment(final Map<String, ?> data) {

        Assert.notEmpty(data, "data cannot be null or empty.");
        String href = (String)data.get("href");
        Assert.hasText(href, "href cannot be null or empty.");

        Map modified = new LinkedHashMap<String, Object>(data.size());

        //since 1.0.RC4.3 - need to recursively add enlistments if the data is expanded:
        for(Object o : data.entrySet()) {
            Map.Entry entry = (Map.Entry)o;
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map && AbstractInstanceResource.isInstanceResource((Map<String, ?>) value)) {
                value = toEnlistment((Map<String, ?>)value);
            }
            modified.put(key, value);
        }

        Enlistment enlistment;
        if (this.hrefMapStore.containsKey(href)) {
            enlistment = this.hrefMapStore.get(href);
            enlistment.setProperties((Map<String, Object>) modified);
        } else {
            enlistment = new Enlistment((Map<String, Object>) modified);
            this.hrefMapStore.put(href, enlistment);
        }

        return enlistment;
    }
}
