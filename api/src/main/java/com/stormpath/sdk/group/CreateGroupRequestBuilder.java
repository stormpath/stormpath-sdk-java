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
package com.stormpath.sdk.group;

/**
 * A Builder to construct {@link com.stormpath.sdk.group.CreateGroupRequest}s.
 *
 * @see com.stormpath.sdk.application.Application#createGroup(com.stormpath.sdk.group.CreateGroupRequest)
 * @since 0.9
 */
public interface CreateGroupRequestBuilder {

    /**
     * Ensures that after a Group is created, the Group's {@link Group#getCustomData() customData} is also
     * retrieved in the same successful creation response. This enhances performance by leveraging a single
     * request to retrieve multiple related resources you know you will use.
     *
     * @return the builder instance for method chaining.
     */
    CreateGroupRequestBuilder withCustomData();

    /**
     * Creates a new {@code CreateGroupRequest} instance based on the current builder state.
     *
     * @return a new {@code CreateGroupRequest} instance based on the current builder state.
     */
    CreateGroupRequest build();
}
