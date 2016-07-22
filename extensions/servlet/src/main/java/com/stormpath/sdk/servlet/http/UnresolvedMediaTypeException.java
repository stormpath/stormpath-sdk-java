/*
 * Copyright 2016 Stormpath, Inc.
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
package com.stormpath.sdk.servlet.http;

import com.stormpath.sdk.lang.Strings;

import java.text.MessageFormat;
import java.util.List;

/**
 * Exception thrown from {@link com.stormpath.sdk.servlet.filter.DefaultContentNegotiationResolver DefaultContentNegotiationResolver}
 * in case of failure to resolve a media type meeting the request's Accept header and the produces media types.
 *
 * @since 1.0.0
 */
public class UnresolvedMediaTypeException extends Exception {

    public UnresolvedMediaTypeException(List<MediaType> acceptedMediaTypes, List<MediaType> producesMediaTypes, String message) {
        super(MessageFormat.format("No MediaType could be resolved for this request ({0})" +
                " and the configured producesMediaTypes settings ({1}). {2}",
                Strings.collectionToCommaDelimitedString(acceptedMediaTypes),
                Strings.collectionToCommaDelimitedString(producesMediaTypes),
                message));
    }
}
