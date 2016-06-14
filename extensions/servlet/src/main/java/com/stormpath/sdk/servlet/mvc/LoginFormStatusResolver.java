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

import javax.servlet.http.HttpServletRequest;

/**
 * Interface for resolving a status key to a full i18n message, according to the spec
 * <p>
 * Spec:
 * <p>
 * The user may be redirected to this page from another workflow. The redirect will append a query parameter that tells you the context of the redirect. The query parameter name will be status. For each status, the login page should render the appropriate message above the login form:
 * <p>
 * ?status=unverified - the user has successfully registered, but their account is unverified. Message to show:
 * Your account verification email has been sent! Before you can log into your account, you need to activate your account by clicking the link we sent to your inbox. Didn't get the email? <a href="#{stormpath.web.verifyEmail.uri}">Click Here</a>
 * ?status=verified - the user has successfully verified their account and can now login. Message to show:
 * Your Account Has Been Verified. You may now login.
 * ?status=created - the user has successfully registered, and email verification is disabled so the user may login immediately. Message to show:
 * Your Account Has Been Created. You may now login.
 * ?status=forgot - the user has submitted the forgot password form and we have sent them an email with a password reset link. Message to show:
 * Password Reset Requested. If an account exists for the email provided, you will receive an email shortly.
 * ?status=reset - the user has finished the password reset workflow and has set a new password for their account. They may now login with their new password. Message to show:
 * Password Reset Successfully. You can now login with your new password.
 *
 * @since 1.0.0
 */
public interface LoginFormStatusResolver {

    String getStatusMessage(HttpServletRequest request, String status);
}
