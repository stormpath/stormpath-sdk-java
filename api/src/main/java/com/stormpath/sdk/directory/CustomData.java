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
package com.stormpath.sdk.directory;

import com.stormpath.sdk.resource.Deletable;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

import java.util.Date;
import java.util.Map;

/**
 * A CustomData is a Map resource within an {@link com.stormpath.sdk.account.Account} or a
 * {@link com.stormpath.sdk.group.Group}, that allows you to specify whatever name/value pairs
 * you wish.
 *
 * @since 0.9
 */
public interface CustomData extends Resource, Saveable, Deletable, Map<String, Object> {

    /**
     * Returns the customData's created date.
     *
     * @return the customData's created date.
     */
    Date getCreatedAt();

    /**
     * Returns the customData's last modification date
     *
     * @return the customData's last modification date
     */
    Date getModifiedAt();
}
