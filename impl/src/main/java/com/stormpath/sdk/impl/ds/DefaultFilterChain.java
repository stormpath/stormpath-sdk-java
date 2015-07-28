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

import com.stormpath.sdk.lang.*;
import org.slf4j.*;

import java.util.*;

public class DefaultFilterChain implements FilterChain {

    private static final Logger log = LoggerFactory.getLogger(DefaultFilterChain.class);

    private List<Filter> filters;
    private int index = 0;
    private final FilterChain COMPLETION_HANDLER;

    public DefaultFilterChain(List<Filter> filters, FilterChain completionHandler) {
        this.filters = filters;
        this.index = 0;
        Assert.notNull(completionHandler, "completionHandler cannot be null.");
        this.COMPLETION_HANDLER = completionHandler;
    }

    @Override
    public ResourceDataResult filter(ResourceDataRequest request) {
        if (this.filters == null || this.filters.size() == this.index) {
            //we've reached the end of the wrapped chain, so invoke the original one:
            if (log.isTraceEnabled()) {
                log.trace("Invoking completion handler.");
            }

            return this.COMPLETION_HANDLER.filter(request);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Invoking wrapped filter at index [" + this.index + "]");
            }
            return this.filters.get(this.index++).filter(request, this);
        }
    }
}
