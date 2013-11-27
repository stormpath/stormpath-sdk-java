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
 * Represents an attempt to create a new {@link com.stormpath.sdk.group.Group} record in Stormpath.
 *
 * @see com.stormpath.sdk.application.Application#createGroup(com.stormpath.sdk.group.CreateGroupRequest)
 * @since 0.9
 */
public interface CreateGroupRequest {

    /**
     * Returns the Group instance for which a new record will be created in Stormpath.
     *
     * @return the Group instance for which a new record will be created in Stormpath.
     */
    Group getGroup();

    /**
     * Returns {@code true} if the the request reflects that the CreateGroupRequest (POST) message will be send
     * with URL query parameters to retrieve the group's references as part of Stormpath's response upon successful
     * group creation.
     * <p/>
     * You should always invoke this method to see if it is safe to invoke the
     * {@link #getGroupOptions()} method.
     *
     * @return {@code true} if the the request reflects that the CreateGroupRequest (POST) message will be send with
     *         URL query parameters to retrieve the expanded references in the Stormpath's response upon successful
     *         group creation.
     */
    boolean isGroupOptionsSpecified();

    /**
     * Returns the {@code GroupOptions} to be used in the CreateGroupRequest s to retrieve the group's
     * references as part of Stormpath's response upon successful group creation.
     * <p/>
     * Always call the {@link #isGroupOptionsSpecified()} method first to see if this value has been
     * configured.  Attempting to call this method when it has not been configured will result in an
     * {@link IllegalStateException}
     *
     * @return {@link GroupOptions} to be used in the CreateGroupRequest s to retrieve the group's
     *         references as part of Stormpath's response upon successful group creation.
     * @throws IllegalStateException if this method is called but {@link #isGroupOptionsSpecified()} is {@code false}.
     */
    GroupOptions getGroupOptions() throws IllegalStateException;
}
