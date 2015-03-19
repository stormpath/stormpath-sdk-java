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
package com.stormpath.spring.context;

import com.stormpath.sdk.lang.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

public class CompositeMessageSource implements MessageSource {

    private static final Logger log = LoggerFactory.getLogger(CompositeMessageSource.class);

    final MessageSource[] messageSources;

    public CompositeMessageSource(MessageSource... sources) {
        Assert.notEmpty(sources, "MessageSources array cannot be null or empty.");
        this.messageSources = sources;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {

        for(MessageSource messageSource : messageSources) {
            String value = messageSource.getMessage(code, args, null, locale);
            if (value != null) {
                return value;
            }
        }

        return defaultMessage;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {

        for(MessageSource messageSource : messageSources) {

            String value = null;

            try {
                value = messageSource.getMessage(code, args, locale);
            } catch (NoSuchMessageException ignored) {
                String msg = "No message found under code '{}' for locale '{}' using MessageSource {}.";
                log.debug(msg, code, locale, messageSource);
            }

            if (value != null) {
                return value;
            }
        }

        if (locale != null) {
            throw new NoSuchMessageException(code, locale);
        }

        throw new NoSuchMessageException(code);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {

        for(MessageSource messageSource : messageSources) {

            String value = null;

            try {
                value = messageSource.getMessage(resolvable, locale);
            } catch (NoSuchMessageException ignored) {
                String msg = "No message found via resolvable '{}' for locale '{}' using MessageSource {}.";
                log.debug(msg, resolvable, locale, messageSource);
            }

            if (value != null) {
                return value;
            }
        }

        String[] codes = resolvable.getCodes();
        if (codes == null) {
            codes = new String[0];
        }

        throw new NoSuchMessageException(codes.length > 0 ? codes[codes.length - 1] : null, locale);
    }
}
