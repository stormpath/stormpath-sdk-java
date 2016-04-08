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
package com.stormpath.sdk.idsite;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.resource.ResourceException;

/**
 * An abstract checked exception denoting an error in IDSite.
 * <p>
 * This is a generic {@link Exception} that specific IDSite exception implementations representing an actual ID Site error can extend.
 * </p>
 *
 * @see InvalidIDSiteTokenException
 * @see IDSiteSessionTimeoutException
 *
 * @since 1.0.RC5
 */
public abstract class IDSiteException extends ResourceException {

    public IDSiteException(Error error) {
        super(error);
    }

}
