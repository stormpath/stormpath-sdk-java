/*
 * Copyright 2013 Stormpath, Inc.
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
package com.stormpath.sdk.query;

import java.io.Serializable;

/**
 * A {@code Criterion} is an individual condition (restriction) to apply to a {@link Criteria} query.
 *
 * @since 0.8
 */
public interface Criterion extends Serializable {

    /**
     * Returns the Criterion's String representation (i.e. a query clause).
     *
     * @return the Criterion's String representation (i.e. a query clause).
     */
    @Override
    String toString();
}
