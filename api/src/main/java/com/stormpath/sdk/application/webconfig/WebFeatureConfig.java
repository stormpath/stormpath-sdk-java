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

/**
 * WebFeatureConfig exposes configurable properties for WebFeatures.
 *
 * @since 1.2.0
 */
public interface WebFeatureConfig<T extends WebFeatureConfig<T>> {

    /**
     * Returns whether a web feature is enabled ({@link Boolean#TRUE true}), disabled ({@link Boolean#FALSE false}) or
     * to do the default application behavior ({@code null}).
     *
     * @return {@code boolean} value that indicates if a WebFeature is enabled or disabled.
     */
    Boolean isEnabled();

    /**
     * Sets the boolean value to enable or disable a web feature.
     *
     * @param enabled {@code boolean} value to enable or disable a web features.
     */
    T setEnabled(Boolean enabled);
}
