package com.stormpath.sdk.mail;

/**
 *
 */
public interface EmailService {

    /**
     * Sends an email based on an EmailRequest based on a template.
     * <p>Implementation note: the {@code template} argument is typically used as template name,
     * but this is up to the implementation.</p>
     * @param request email request info
     * @param template template to be used by the EmailService implementation
     */
    void sendEmail(EmailRequest request, String template);

    void sendValidationEmail(EmailRequest request);

    void sendResetEmail(EmailRequest request);
}
