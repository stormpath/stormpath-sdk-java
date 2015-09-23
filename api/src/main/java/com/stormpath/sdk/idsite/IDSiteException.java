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
import com.stormpath.sdk.lang.Assert;

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
public abstract class IDSiteException extends Exception implements com.stormpath.sdk.error.Error {

    private final Error error;

    /**
     * Ensures the message used for the exception (i.e. exception.getMessage()) reports the {@code developerMessage}
     * returned by the Stormpath API Server.  The regular Stormpath response body {@code message} field is targeted
     * at applicadtion end-users that could very likely be non-technical.  Since an exception should be helpful to
     * developers, it is better to show a more technical message.
     * <p/>
     * Added as a fix for <a href="https://github.com/stormpath/stormpath-sdk-java/issues/28">Issue #28</a>.
     *
     * @param error the response Error. Cannot be null.
     * @return {@code error.getDeveloperMessage()}
     */
    private static String buildExceptionMessage(Error error) {
        Assert.notNull(error, "Error argument cannot be null.");
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP ").append(error.getStatus())
                .append(", Stormpath ").append(error.getCode())
                .append(" (").append(error.getMoreInfo()).append("): ")
                .append(error.getDeveloperMessage());
        return sb.toString();
    }

    public IDSiteException(Error error) {
        super(buildExceptionMessage(error));
        this.error = error;
    }

    @Override
    public int getStatus() {
        return error.getStatus();
    }

    @Override
    public int getCode() {
        return error.getCode();
    }

    @Override
    public String getDeveloperMessage() {
        return error.getDeveloperMessage();
    }

    @Override
    public String getMoreInfo() {
        return error.getMoreInfo();
    }

    public Error getStormpathError() {
        return this.error;
    }

}
