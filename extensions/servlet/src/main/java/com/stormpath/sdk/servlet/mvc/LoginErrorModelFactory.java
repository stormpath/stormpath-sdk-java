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
package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.config.Config;
import com.stormpath.sdk.servlet.config.ConfigResolver;
import com.stormpath.sdk.servlet.form.Form;
import com.stormpath.sdk.servlet.i18n.DefaultMessageSource;
import com.stormpath.sdk.servlet.i18n.MessageSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates the error model on a failed login attempt.
 *
 * @since 1.0.RC7
 */
public class LoginErrorModelFactory implements ErrorModelFactory {

    private static final Logger log = LoggerFactory.getLogger(LoginErrorModelFactory.class);
    public static final String MESSAGE_SOURCE = "stormpath.web.message.source";
    private static final String  INVALID_LOGIN_MESSAGE = "stormpath.web.login.form.errors.invalidLogin";

    public List<String> toErrors(HttpServletRequest request, Form form, Exception e) {
        if (e != null) {
            log.debug("Unable to login user.", e);
            List<String> errors = new ArrayList<String>(1);
            errors.add(getInvalidLoginMessage(request));
            return errors;
        }
        return null;
    }

    private String getInvalidLoginMessage(HttpServletRequest request) {
        MessageSource messageSource = null;
        try {
            Config config = getConfig(request);
            messageSource = config.getInstance(MESSAGE_SOURCE);
        }
        catch (ServletException se){
            messageSource = new DefaultMessageSource();
        } catch (IllegalArgumentException iae){
            //When using Spring Boot the Config is not available
            messageSource = new DefaultMessageSource();
        }

        return messageSource.getMessage(INVALID_LOGIN_MESSAGE, request.getLocale());
    }

    private Config getConfig(HttpServletRequest request){
        return ConfigResolver.INSTANCE.getConfig(request.getServletContext());
    }
}
