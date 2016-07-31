/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.config.filter;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.IdSiteLogoutController;

/**
 * @since 1.0.0
 */
public class IdSiteLogoutFilterFactory extends ControllerFilterFactory<IdSiteLogoutController> {

    @Override
    protected IdSiteLogoutController newController() {
        return new IdSiteLogoutController();
    }

    @Override
    protected void configure(IdSiteLogoutController c, Config config) {
        c.setServerUriResolver(getConfig().getServerUriResolver());
        c.setIdSiteResultUri(getConfig().getCallbackUri());
        c.setIdSiteOrganizationResolver(getConfig().getIdSiteOrganizationResolver());
        c.setNextUri(getConfig().getLogoutConfig().getNextUri());

        ControllerFilter filter = new ControllerFilter();
        filter.setProducedMediaTypes(config.getProducedMediaTypes());
        filter.setController(c);
    }
}
