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

/**
 * @since 1.0.RC4
 */
@ConfigurationProperties(prefix = "stormpath.web.handlerMapping")
public class StormpathHandlerMappingProperties {

    //By default, we want the the RequestMappingHandlerMapping to take precedence over this HandlerMapping: this
    //allows app developers to override any of the Stormpath default controllers by creating their own
    //@Controller class at the same URI path.
    //Spring Boot sets the default RequestMappingHandlerMapping's order to be zero, so we'll add a little
    //lower numbers have higher precedence):
    private int order = 10;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
