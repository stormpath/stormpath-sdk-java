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
package com.stormpath.sdk.servlet.i18n;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @since 1.0.RC3
 */
public class DefaultMessageSource implements MessageSource {

    public static final String BUNDLE_BASE_NAME = "com.stormpath.sdk.servlet.i18n";

    @Override
    public String getMessage(String key, Locale locale) {
        return getMessage(key, locale, new Object[]{});
    }

    @Override
    public String getMessage(String key, Locale locale, Object... args) {
        ResourceBundle bundle = getBundle(locale);

        try {
            /* To enable accents and other UTF-8 characters inside properties file we need to read the value in ISO-8858-1
               which is the default encoding for properties files according to the Java documentation and re-encode it in UTF-8 */
            String msg = new String(bundle.getString(key).getBytes("ISO-8859-1"), "UTF-8");
            return MessageFormat.format(msg, args);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        } catch (UnsupportedEncodingException e) {
            /* Should not happen since properties are always encoded in ISO-8850-1 thus is supported and UTF-8 is supported
               by the JVM in all platforms */
            throw new IllegalStateException("Couldn't load property from resource bundle", e);
        }
    }

    protected ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }
}
