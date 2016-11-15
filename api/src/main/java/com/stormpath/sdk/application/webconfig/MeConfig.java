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
package com.stormpath.sdk.application.webconfig;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.resource.ResourceException;

/**
 * Exposes the configurable properties of the <code>/me</code> route.
 * <p/>
 * For example, to expand the {@link Account#getCustomData() customData} and the {@link Account#getGroups() groups} of an
 * {@link Account accounts} retrieved via the <code>/me</code> route, both expand properties need to be enabled.
 * <pre>
 *    ...
 *    ApplicationWebConfig webConfig = application.getWebConfig();
 *    webConfig.getMe().getExpansions().setCustomData(true).setApiKeys(true);
 *    webConfig.save();
 *    ..
 * </pre>
 *
 * @since 1.2.0
 */
public interface MeConfig extends WebFeatureConfig<MeConfig> {

    /**
     * Returns the {@link MeExpansionConfig expand} configurable properties associated to this {@link MeConfig meConfig}.
     *
     * @return the {@link MeExpansionConfig expand} configurable properties associated to this {@link MeConfig meConfig}.
     */
    MeExpansionConfig getExpansions();

    /**
     * Overriding to note that {@code this} configuration doesn't allow to set the {@code enable} flag to {@code null}.
     *
     * @param enabled {@code boolean} value to enable or disable a web features.
     * @throws ResourceException when set to {@code null}.
     */
    @Override
    MeConfig setEnabled(Boolean enabled);
}
