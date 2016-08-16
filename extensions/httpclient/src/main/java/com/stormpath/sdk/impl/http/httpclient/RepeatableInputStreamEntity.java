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
package com.stormpath.sdk.impl.http.httpclient;

import com.stormpath.sdk.http.MediaType;
import com.stormpath.sdk.http.Request;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.InputStreamEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Custom implementation of {@link org.apache.http.HttpEntity HttpEntity} that delegates to an
 * {@link InputStreamEntity}, with the one notable difference, that if
 * the underlying InputStream supports being reset, this RequestEntity will
 * report that it is repeatable and will reset the stream on all subsequent
 * attempts to write out the request.
 *
 * @since 0.1
 */
class RepeatableInputStreamEntity extends BasicHttpEntity {

    /**
     * True if the request entity hasn't been written out yet
     */
    private boolean firstAttempt = true;

    /**
     * The underlying InputStreamRequestEntity being delegated to
     */
    private InputStreamEntity inputStreamEntity;

    /**
     * The InputStream containing the content to write out
     */
    private InputStream content;

    /**
     * Creates a new RepeatableInputStreamEntity using the information
     * from the specified request. If the input stream containing the request's
     * contents is repeatable, then this RequestEntity will report as being
     * repeatable.
     *
     * @param request The details of the request being written out (content type,
     *                content length, and content).
     */
    RepeatableInputStreamEntity(Request request) {
        setChunked(false);

        MediaType contentType = request.getHeaders().getContentType();
        long contentLength = request.getHeaders().getContentLength();
        InputStream body = request.getBody();

        this.inputStreamEntity = new InputStreamEntity(body, contentLength);
        this.inputStreamEntity.setContentType(contentType.getType());
        this.content = body;

        setContent(content);
        setContentType(contentType.getType());
        setContentLength(contentLength);
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    /**
     * Returns true if the underlying InputStream supports marking/reseting or
     * if the underlying InputStreamRequestEntity is repeatable (i.e. its
     * entire contents will be buffered in memory and can be repeated).
     */
    @Override
    public boolean isRepeatable() {
        return content.markSupported() || inputStreamEntity.isRepeatable();
    }

    /**
     * Resets the underlying InputStream if this isn't the first attempt to
     * write out the request, otherwise simply delegates to
     * InputStreamRequestEntity to write out the data.
     */
    @Override
    public void writeTo(OutputStream output) throws IOException {
        if (!firstAttempt && isRepeatable()) content.reset();
        firstAttempt = false;
        inputStreamEntity.writeTo(output);
    }

}
