/*
 * Copyright 2014 Stormpath, Inc.
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
package com.stormpath.sdk.resource;

import com.stormpath.sdk.error.Error;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.lang.Strings;

/**
 * @since 0.2
 */
public class ResourceException extends RuntimeException implements Error {

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
     * @since 0.9.2
     */
    private static String buildExceptionMessage(Error error) {
        Assert.notNull(error, "Error argument cannot be null.");
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP ").append(error.getStatus());

        String requestId = error.getRequestId();

        if (Strings.hasText(requestId)) {
            sb.append(", RequestId: ").append(error.getRequestId());
        }

        sb.append(", Stormpath ").append(error.getCode())
                .append(" (").append(error.getMoreInfo()).append("): ")
                .append(error.getDeveloperMessage());
        return sb.toString();
    }

    public ResourceException(Error error) {
        super(buildExceptionMessage(error));
        this.error = error;
    }

    @Override
    public int getStatus() {
        return error.getStatus();
    }

    /**
     * Get the Stormpath Error Code
     * Check http://docs.stormpath.com/errors/ for the list of Stormpath Error Codes
     * @return the code of the error
     */
    @Override
    public int getCode() {
        return error.getCode();
    }

    @Override
    public String getDeveloperMessage() {
        return error.getDeveloperMessage();
    }

    /**
     * More information about the error is described in the Stormpath Error Codes documentation
     * Check http://docs.stormpath.com/errors/ for the list of Stormpath Error Codes
     * @return the URI to the error documentation
     */
    @Override
    public String getMoreInfo() {
        return error.getMoreInfo();
    }

    @Override
    public String getRequestId() {
        return error.getRequestId();
    }

    public Error getStormpathError() {
        return this.error;
    }

    /**
     * Returns the underlying REST {@link Error} returned from the Stormpath API server.
     * <p/>
     * Because this class's {@link #getMessage() getMessage()} value returns a developer-friendly message to help you
     * debug when you see stack traces, you might want to acquire the underlying {@code Error} to show an end-user
     * the simpler end-user appropriate error message.  The end-user error message is non-technical in nature - as a
     * convenience, you can show this message directly to your application end-users.
     * <p/>
     * For example:
     * <pre>
     * try {
     *
     *     //something that causes a ResourceException
     *
     * } catch (ResourceException re) {
     *
     *     String endUserMessage = re.getError().getMessage();
     *
     *     warningDialog.setText(endUserMessage);
     * }
     * </pre>
     *
     * @return the underlying REST {@link Error} resource representation returned from the Stormpath API server.
     * @since 0.10
     *
        public Error getError() {
        return this.error;
    }*/
}
