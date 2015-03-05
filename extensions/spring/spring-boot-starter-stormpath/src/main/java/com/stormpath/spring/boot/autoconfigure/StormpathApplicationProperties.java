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

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * @since 1.0.RC4
 */
@ConfigurationProperties(prefix = "stormpath.application")
public class StormpathApplicationProperties {

    private static final String APP_HREF_ERROR =
        "\n\nUnable to find a 'stormpath.application.href' property value in any known spring boot " +
        "configuration location as documented here:\n\n" +
        "http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-external-config\n\n" +
        "This property is required required when you have more than one application registered in Stormpath.\n\n";

    private String href;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Application resolveApplication(Client client) {

        if (StringUtils.hasText(href)) {
            return client.getResource(href, Application.class);
        }

        //otherwise no href configured - try to find an application:

        Application single = null;

        for (Application app : client.getApplications()) {
            if (app.getName().equalsIgnoreCase("Stormpath")) { //ignore the admin app
                continue;
            }
            if (single != null) {
                //there is more than one application in the tenant, and we can't infer which one should be used
                //for this particular application.  Let them know:
                throw new IllegalStateException(APP_HREF_ERROR);
            }
            single = app;
        }

        return single;
    }
}
