/*
* Copyright 2015 Stormpath, Inc.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.stormpath.sdk.directory;

import com.stormpath.sdk.mail.EmailStatus;
import com.stormpath.sdk.mail.ModeledEmailTemplateList;
import com.stormpath.sdk.mail.UnmodeledEmailTemplateList;
import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * A AccountCreationPolicy resource is used to configure different aspects and actions relevant to the {@link Account}-creation process.
 *
 * @since 1.0.RC4.5
 */
public interface AccountCreationPolicy extends Resource, Saveable {

    /**
     * Returns the {@link ModeledEmailTemplateList} collection that contains the 'verification email'
     * templates that can be used for sending the ‘verification email’ when new accounts are created.
     *
     * @return the {@link ModeledEmailTemplateList} collection that contains the 'verification email' templates
     * that can be used for sending the 'verification email' when new accounts are created.
     */
    ModeledEmailTemplateList getAccountVerificationEmailTemplates();

    /**
     * Returns the {@link com.stormpath.sdk.mail.UnmodeledEmailTemplateList} collection that contains the ‘verification success email’
     * templates that can be used for sending the ‘verification success email’ for new accounts.
     *
     * @return the {@link com.stormpath.sdk.mail.UnmodeledEmailTemplateList} collection that contains the ‘verification success email’
     * templates that can be used for sending the ‘verification success email’ for new accounts.
     */
    UnmodeledEmailTemplateList getAccountVerificationSuccessEmailTemplates();

    /**
     * Returns the {@link com.stormpath.sdk.mail.UnmodeledEmailTemplateList} collection that contains the ‘welcome email’
     * templates that can be used for sending a 'welcome email' to a newly registered account.
     *
     * @return the {@link com.stormpath.sdk.mail.UnmodeledEmailTemplateList} collection that contains the ‘welcome email’
     * templates that can be used for sending a 'welcome email' to a newly registered account.
     */
    UnmodeledEmailTemplateList getWelcomeEmailTemplates();

    /**
     * Returns the Account Verification Email Status
     * <p/>
     * An {@link EmailStatus#DISABLED disabled} indicates that the Account verification Workflow is <code>disabled</code>.
     * Therefore, the account verification email will not be sent to the newly created account.
     *
     * @return the Account verification Email status.
     */
    EmailStatus getVerificationEmailStatus();

    /**
     * Specifies whether the Account Verification Workflow is enabled or disabled for the parent directory.
     * When {@link EmailStatus#DISABLED disabled}, the account verification email will not be sent to a newly created account.
     *
     * @param accountVerificationEmailStatus the status of the Account Verification Email Workflow
     * @return this instance for method chaining.
     */
    AccountCreationPolicy setVerificationEmailStatus(EmailStatus accountVerificationEmailStatus);

    /**
     * Returns the Account Verification Success Email Status
     *
     * An {@link EmailStatus#DISABLED disabled} indicates that the Account verification Success Workflow is <code>disabled</code>.
     * Therefore, the account verification success email will not be sent when the email for a newly created account is successfully verified.
     *
     * @return the Account verification Success Email status.
     */
    EmailStatus getVerificationSuccessEmailStatus();

    /**
     * Specifies whether the Account Verification Success Workflow is enabled or disabled for the parent directory.
     * When {@link EmailStatus#DISABLED disabled}, the account verification email will not be sent when the email for a newly created account is successfully verified.
     *
     * @param accountVerificationSuccessEmailStatus the status of the Account Verification Success Email Workflow
     * @return this instance for method chaining.
     */
    AccountCreationPolicy setVerificationSuccessEmailStatus(EmailStatus accountVerificationSuccessEmailStatus);

    /**
     * Returns the Welcome Email Status
     *
     * An {@link EmailStatus#DISABLED disabled} indicates that the Welcome Workflow is <code>disabled</code>.
     * Therefore, a welcome email will not be sent when a new account is created.
     *
     * @return the Account verification Success Email status.
     */
    EmailStatus getWelcomeEmailStatus();

    /**
     * Specifies whether the Welcome Email Workflow is enabled or disabled for the parent directory.
     * When {@link EmailStatus#DISABLED disabled}, the welcome email will not be sent for a newly created account.
     *
     * @param welcomeEmailStatus the status of the Welcome Email Workflow
     * @return this instance for method chaining.
     */
    AccountCreationPolicy setWelcomeEmailStatus(EmailStatus welcomeEmailStatus);

}