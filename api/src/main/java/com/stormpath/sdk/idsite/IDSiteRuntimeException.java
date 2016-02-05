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
 * A sub-class of {@link ResourceException} representing an IDSite error.
 * <p>
 * This is a generic {@link RuntimeException} encapsulating all the possible IDSite errors. It is not required to be explicitly catch or throw. However, for
 * those interested in re-working the specific error, this class offers the {@link #rethrow()} method. When invoked, this method will throw a new <a
 * href="https://en.wikipedia.org/wiki/Exception_handling#Checked_exceptions">checked exception</a> describing the specific IDSite error. It can be one of subclasses of {@link IDSiteException}:
 * <ul>
 *     <li>{@link com.stormpath.sdk.idsite.InvalidIDSiteTokenException InvalidIDSiteTokenException}: indicating that the token is invalid. Reasons could be: expired token,
 *     the issued at time (iat) is after the current time, the specified organization name key does not exist in your Stormpath Tenant or because the specified organization is disabled.</li>
 *     <li>{@link com.stormpath.sdk.idsite.IDSiteSessionTimeoutException IDSiteSessionTimeoutException}: indicating that the session on ID Site has timed out.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.RC5
 */
public class IDSiteRuntimeException extends ResourceException {

    public IDSiteRuntimeException(Error error) {
        super(error);
    }

    /**
     * Converts this generic IDSite exception into one of the following corresponding checked exceptions: InvalidIDSiteTokenException, IDSiteSessionTimeoutException.
     *
     * @throws InvalidIDSiteTokenException
     * @throws IDSiteSessionTimeoutException
     */
    public void rethrow() throws InvalidIDSiteTokenException, IDSiteSessionTimeoutException {
        Error error = this.getStormpathError();

        if (error.getCode() == 10011 || error.getCode() == 10012 || error.getCode() == 11001 || error.getCode() == 11002 || error.getCode() == 11003 || error.getCode() == 11005 ) {
            throw new InvalidIDSiteTokenException(error);
        }

        if (this.getStormpathError().getCode() == 12001) {
            throw new IDSiteSessionTimeoutException(error);
        }

        if (this.getStormpathError().getCode() == 11005) {
            throw new IDSiteSessionTimeoutException(error);
        }

        throw new IllegalStateException("error type is unrecognized: " + error.getCode());
    }

}
