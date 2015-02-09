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
package com.stormpath.sdk.mail;

import com.stormpath.sdk.lang.Assert;

/**
 * @since 1.0.0
 */
public enum MimeType {

    /**
     * Plain text
     */
    PLAIN_TEXT("text/plain"),

    /**
     * HTML format
     */
    HTML("text/html");

    private final String mimeType;

    private MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String value() {
       return this.mimeType;
    }

    public static MimeType fromString(String mimeTypeString) {
        Assert.hasText(mimeTypeString, "mimeTypeString cannot be null or empty");
        for (MimeType mimeType : MimeType.values()) {
            if (mimeTypeString.equalsIgnoreCase(mimeType.value())) {
                return mimeType;
            }
        }
        return null;
    }


}
