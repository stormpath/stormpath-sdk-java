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
package com.stormpath.sdk.directory;

import com.stormpath.sdk.mail.*;
import com.stormpath.sdk.resource.*;

/**
 * A PasswordPolicy resource is used to configure different aspects of the restrictions on passwords for accounts associated
 * with the parent {@link Directory}.
 *
 * @since 1.0.RC4
 */
public interface PasswordPolicy extends Resource, Saveable {

    /**
     * Return the time-to-live (in hours) for the reset password token.
     *
     * @return the time-to-live (in hours) for the reset password token.
     */
    int getResetTokenTtlHours();

    /**
     * Specifies the amount of time (in hours) that a reset token will remain valid. Once that time has passed, the token
     * gets invalidated.
     * <p/>
     * Defaults to 24 hrs. Must be a positive integer, less than 169 (i.e. 0 < resetTokenTtl < 169). 168 hours equals 7 days.
     *
     * @param resetTokenTtl how long (in hours) a reset token can remain active until it is used.
     * @return this instance for method chaining.
     */
    PasswordPolicy setResetTokenTtlHours(int resetTokenTtl);

    /**
     * Returns the Reset Email's status.
     * <p/>
     * An {@link EmailStatus#DISABLED disabled} indicates that the Reset Password Workflow is <code>disabled</code>. Therefore, the reset
     * email will not be sent to the account trying to reset its password.
     *
     * @return the Reset Email's status.
     */
    EmailStatus getResetEmailStatus();

    /**
     * Specifies whether the Reset Email Workflow is enabled or disabled for the parent directory. When {@link EmailStatus#DISABLED disabled},
     * the reset email will not be sent to the account trying to reset its password.
     *
     * @param status the status of the Reset Email Workflow
     * @return this instance for method chaining.
     */
    PasswordPolicy setResetEmailStatus(EmailStatus status);

    /**
     * Returns the Reset Success Email's status.
     * <p/>
     * An {@link EmailStatus#DISABLED disabled} indicates that the Reset Success Password Workflow is <code>disabled</code>. Therefore, the reset
     * success email will not be sent to the account after resetting its password.
     *
     * @return the Reset Success Email's status.
     */
    EmailStatus getResetSuccessEmailStatus();

    /**
     * Specifies whether the Reset Success Email Workflow is enabled or disabled for the parent directory. When {@link EmailStatus#DISABLED disabled},
     * the success email will not be sent to the account after resetting its password.
     *
     * @param status the status of the Reset Success Email Workflow
     * @return this instance for method chaining.
     */
    PasswordPolicy setResetSuccessEmailStatus(EmailStatus status);

    /**
     * Return the {@link PasswordStrength} resource to configure the password strength policy.
     *
     * @return the {@link PasswordStrength} resource to configure the password strength policy.
     */
    PasswordStrength getStrength();

    /**
     * Returns the {@link com.stormpath.sdk.mail.ModeledEmailTemplateList} collection holding the set of email templates to be used when requesting a password reset operation.
     *
     * @return the {@link com.stormpath.sdk.mail.ModeledEmailTemplateList} collection holding the set of email templates to be used when requesting a password reset operation.
     */
    ModeledEmailTemplateList getResetEmailTemplates();

    /**
     * Returns the {@link com.stormpath.sdk.mail.ModeledEmailTemplateList} collection holding the set of email templates available when notifying about a successful password reset operation.
     *
     * @return the {@link com.stormpath.sdk.mail.ModeledEmailTemplateList} collection holding the set of email templates available when notifying about a successful password reset operation.
     */
    UnmodeledEmailTemplateList getResetSuccessEmailTemplates();

}
