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
package com.stormpath.sdk.challenge;

import com.stormpath.sdk.resource.CollectionResource;

/**
 * A {@link CollectionResource} containing {@link Challenge} instances.
 *
 * @param <T> a subclass of {@link Challenge} specifying the type of elements in this {@code ChallengeList}.
 *
 * @since 1.1.0
 */
public interface ChallengeList<T extends Challenge> extends CollectionResource<T> {
}
