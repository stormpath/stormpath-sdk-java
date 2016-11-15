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

package com.stormpath.sdk.servlet.mvc

import com.stormpath.sdk.account.VerificationEmailRequest
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.lang.Assert
import com.stormpath.sdk.servlet.filter.ContentNegotiationResolver
import com.stormpath.sdk.servlet.form.DefaultField
import com.stormpath.sdk.servlet.form.Field
import com.stormpath.sdk.servlet.form.Form
import com.stormpath.sdk.servlet.http.MediaType
import com.stormpath.sdk.servlet.http.Resolver
import com.stormpath.sdk.servlet.http.authc.AccountStoreResolver
import com.stormpath.sdk.servlet.i18n.MessageSource
import org.easymock.IArgumentMatcher
import org.testng.annotations.Test

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static com.stormpath.sdk.servlet.mvc.VerificationEmailRequestMatcher.containsLogin
import static org.easymock.EasyMock.*

/**
 * @since 1.2.0
 */
public class VerifyControllerTest {

    @Test
    public void testLoginFieldFavoredOverEmail() {
        VerifyController verifyController = new VerifyController();

        HttpServletRequest request = createNiceMock(HttpServletRequest)
        HttpServletResponse response = createNiceMock(HttpServletResponse)

        Field login = DefaultField.builder().setName("login").setEnabled(true).build()
        Field email = DefaultField.builder().setName("email").setEnabled(true).build()
        List<Field> formFields = new ArrayList<>()
        formFields.add(login)
        formFields.add(email)
        verifyController.setFormFields(formFields)

        ContentNegotiationResolver contentNegotiationResolver = createNiceMock(ContentNegotiationResolver)
        verifyController.setContentNegotiationResolver(contentNegotiationResolver)

        RequestFieldValueResolver requestFieldValueResolver = createNiceMock(RequestFieldValueResolver)
        verifyController.setFieldValueResolver(requestFieldValueResolver)

        Application application = createNiceMock(Application)

        AccountStoreResolver accountStoreResolver = createNiceMock(AccountStoreResolver)
        verifyController.setAccountStoreResolver(accountStoreResolver)

        Resolver<Locale> localeResolver = createNiceMock(Resolver)
        verifyController.setLocaleResolver(localeResolver)

        MessageSource messageSource = createNiceMock(MessageSource)
        verifyController.setMessageSource(messageSource)

        expect(request.getParameterMap()).andReturn(new HashMap<String, String[]>())
        expect(request.getContentLength()).andReturn(0)

        expect(contentNegotiationResolver.getContentType(anyObject(HttpServletRequest), anyObject(HttpServletResponse), anyObject(List))).andReturn(MediaType.APPLICATION_JSON).times(2)
        expect(requestFieldValueResolver.getAllFields(request)).andReturn(new HashMap<String, Object>())
        expect(requestFieldValueResolver.getValue(eq(request), eq("login"))).andReturn("username")
        expect(requestFieldValueResolver.getValue(request, "email")).andReturn(null)

        expect(request.getAttribute(Application.class.getName())).andReturn(application)

        expect(request.setAttribute(eq("form"), anyObject(Form)))
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn(null)

        expect(application.sendVerificationEmail(containsLogin("username")))

        replay(request, response, contentNegotiationResolver, requestFieldValueResolver, accountStoreResolver, localeResolver, messageSource)

        verifyController.doPost(request, response)

        verify(request, response, contentNegotiationResolver, requestFieldValueResolver, accountStoreResolver, localeResolver, messageSource)
    }

    @Test
    public void testFallBackToEmailIfLoginFieldMissing() {
        VerifyController verifyController = new VerifyController();

        HttpServletRequest request = createNiceMock(HttpServletRequest)
        HttpServletResponse response = createNiceMock(HttpServletResponse)

        Field login = DefaultField.builder().setName("login").setEnabled(true).build()
        Field email = DefaultField.builder().setName("email").setEnabled(true).build()
        List<Field> formFields = new ArrayList<>()
        formFields.add(login)
        formFields.add(email)
        verifyController.setFormFields(formFields)

        ContentNegotiationResolver contentNegotiationResolver = createNiceMock(ContentNegotiationResolver)
        verifyController.setContentNegotiationResolver(contentNegotiationResolver)

        RequestFieldValueResolver requestFieldValueResolver = createNiceMock(RequestFieldValueResolver)
        verifyController.setFieldValueResolver(requestFieldValueResolver)

        Application application = createNiceMock(Application)

        AccountStoreResolver accountStoreResolver = createNiceMock(AccountStoreResolver)
        verifyController.setAccountStoreResolver(accountStoreResolver)

        Resolver<Locale> localeResolver = createNiceMock(Resolver)
        verifyController.setLocaleResolver(localeResolver)

        MessageSource messageSource = createNiceMock(MessageSource)
        verifyController.setMessageSource(messageSource)

        expect(request.getParameterMap()).andReturn(new HashMap<String, String[]>())
        expect(request.getContentLength()).andReturn(0)

        expect(contentNegotiationResolver.getContentType(anyObject(HttpServletRequest), anyObject(HttpServletResponse), anyObject(List))).andReturn(MediaType.APPLICATION_JSON).times(2)
        expect(requestFieldValueResolver.getAllFields(request)).andReturn(new HashMap<String, Object>())
        expect(requestFieldValueResolver.getValue(request, "login")).andReturn(null)
        expect(requestFieldValueResolver.getValue(request, "email")).andReturn("test@test.com")
        expect(request.setAttribute(eq("form"), anyObject(Form)))
        expect(accountStoreResolver.getAccountStore(request, response)).andReturn(null)


        expect(request.getAttribute(Application.class.getName())).andReturn(application)

        expect(application.sendVerificationEmail(containsLogin("test@test.com")))

        replay(request, response, contentNegotiationResolver, requestFieldValueResolver, accountStoreResolver, localeResolver, messageSource)

        verifyController.doPost(request, response)

        verify(request, response, contentNegotiationResolver, requestFieldValueResolver, accountStoreResolver, localeResolver, messageSource)
    }
}


class VerificationEmailRequestMatcher implements IArgumentMatcher {

    private String login;

    public VerificationEmailRequestMatcher(String login) {
        Assert.notNull(login, "login must not be null")
        this.login = login;
    }

    public static VerificationEmailRequest containsLogin(String login) {
        reportMatcher(new VerificationEmailRequestMatcher(login));
        return null;
    }

    public void appendTo(StringBuffer buffer) {
        buffer.append("Expected login value '" + login + "' not found in VerificationEmailRequest")
    }

    public boolean matches(Object object) {
        if (!(object instanceof VerificationEmailRequest)) {
            return false;
        }
        VerificationEmailRequest verificationEmailRequest = (VerificationEmailRequest) object;
        return verificationEmailRequest != null && login.equals(verificationEmailRequest.getLogin());
    }

}