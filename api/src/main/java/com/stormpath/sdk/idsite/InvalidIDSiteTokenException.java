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
 * A sub-class of {@link IDSiteException} representing a specific IDSite error.
 * <p>
 * This is a checked {@link Exception} indicating that the token is invalid. Reasons for this could be:
 * <ul>
 *  <li>expired token,</li>
 *  <li>the issued at time (iat) is after the current time</li>
 *  <li>the specified organization name key does not exist in your Stormpath Tenant, or</li>
 *  <li>or because the specified organization is disabled.</li>
 * </ul>
 * </p>
 * <p>
 * The following information received from IDSite's Error will be available: <code>code</code>, <code>developerMessage</code>, <code>message</code>, <code>moreInfo</code>, <code>status</code>.
 * </p>
 *
 * @since 1.0.RC5
 */
public class InvalidIDSiteTokenException extends ResourceException {

    public InvalidIDSiteTokenException(Error error)  {
        super(error);
    }

}
