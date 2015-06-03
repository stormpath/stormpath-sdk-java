/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.resource;

import java.util.Date;

/**
 * Interface to be implemented by {@link Resource Resources} that contain and can be retrieved using the {@code Resource#createdAt} and {@code Resource#modifiedAt} fields as part of the query {@link com.stormpath.sdk.query.Criteria Criteria}.
 * For example: {@link com.stormpath.sdk.account.Account Account}, {@link com.stormpath.sdk.application.Application Application},
 * {@link com.stormpath.sdk.directory.Directory Directory} and {@link com.stormpath.sdk.group.Group Group}.
 *
 * @since 1.0.RC4.3
 */
public interface Auditable {

    /**
     * Returns the Resource's created date.
     *
     * @return the Resource's created date.
     * @since 1.0
     */
    Date getCreatedAt();

    /**
     * Returns the Resource's last modification date.
     *
     * @return the Resource's last modification date.
     * @since 1.0
     */
    Date getModifiedAt();
}
