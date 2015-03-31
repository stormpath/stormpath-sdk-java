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

import com.stormpath.sdk.resource.Resource;
import com.stormpath.sdk.resource.Saveable;

/**
 * This resource provides operations to customize all the internal components (e.g. sender, subject, html body, etc) of emails
 * that will be eventually used and sent by Stormpath in different scenarios (e.g. account creation verification, reset password, etc)
 *
 * @since 1.0.RC4
 */
public interface EmailTemplate<T extends EmailTemplate> extends Resource, Saveable {

    /**
     * Returns the internal name used to identify this email template. This text will not be sent to the recipients.
     *
     * @return the internal name used to identify this email template.
     */
    String getName();

    /**
     * Specifies the internal name used to identify this email template. This text will not be sent to the recipients.
     *
     * @param name the internal name used to identify this email template. This text will not be sent to the recipients.
     * @return this instance for method chaining.
     */
    T setName(String name);

    /**
     * Returns the internal text used to describe this email template. This text will not be sent to the recipients.
     *
     * @return the internal text used to describe this email template.
     */
    String getDescription();

    /**
     * Specifies the internal text used to describe this email template. This text will not be sent to the recipients.
     *
     * @param description the internal text used to describe this email template. This text will not be sent to the recipients.
     * @return this instance for method chaining.
     */
    T setDescription(String description);

    /**
     * Returns the name (e.g. John Doe) that will be used in the <b>from</b> field of the email.
     *
     * @return the name that will be used in the <b>from</b> field of the email.
     */
    String getFromName();

    /**
     * Specifies the name (e.g. John Doe) that will be used in the <b>from</b> field of the email.
     *
     * @param fromName the name that will be used in the <b>from</b> field of the email.
     * @return this instance for method chaining.
     */
    T setFromName(String fromName);

    /**
     * Returns the sender email address. The receiver of the email will see it as being sent from this address.
     *
     * @return the sender email address.
     */
    String getFromEmailAddress();

    /**
     * Specifies the sender email address. The receiver of the email will see it as being sent from this address.
     *
     * @param fromEmailAddress the sender email address.
     * @return this instance for method chaining.
     */
    T setFromEmailAddress(String fromEmailAddress);

    /**
     * Returns the subject of the email.
     *
     * @return the subject of the email.
     */
    String getSubject();

    /**
     * Specifies the subject of the email.
     *
     * @param subject the subject of the email.
     * @return this instance for method chaining.
     */
    T setSubject(String subject);

    /**
     * Returns the body to be used in unformatted email messages (plain text).
     *
     * @return the body of plain text emails.
     */
    String getTextBody();

    /**
     * Specifies the body to be used in unformatted email messages (plain text). This text will be sent when the <code>mimeType</code>
     * has been set to {@link MimeType#PLAIN_TEXT}.
     *
     * @param textBody the body to be used in unformatted email messages.
     * @return this instance for method chaining.
     * @see #setMimeType(MimeType)
     */
    T setTextBody(String textBody);

    /**
     * Returns the body to be used in HTML email messages.
     *
     * @return the body of HTML emails.
     */
    String getHtmlBody();

    /**
     * Specifies the body to be used in HTML email messages. This text will be sent when the <code>mimeType</code>
     * has been set to {@link MimeType#HTML}.
     *
     * @param htmlBody the body to be used in HTML email messages.
     * @return this instance for method chaining.
     * @see #setMimeType(MimeType)
     */
    T setHtmlBody(String htmlBody);

    /**
     * Describes the type of message the user will receive in his inbox.
     *
     * @return the type of message the user will receive in his inbox
     */
    MimeType getMimeType();

    /**
     * Specifies the kind of message that the user will receive in his inbox.
     * <ul>
     *     <li>{@link MimeType#HTML}: the text contained in the <code>htmlBody</code> property will be sent</li>
     *     <li>{@link MimeType#PLAIN_TEXT}: the text contained in the <code>textBody</code> property will be sent</li>
     * </ul>
     *
     * @param mimeType defines the type of message that the user will receive in his inbox.
     * @return this instance for method chaining.
     * @see
     */
    T setMimeType(MimeType mimeType);

}
