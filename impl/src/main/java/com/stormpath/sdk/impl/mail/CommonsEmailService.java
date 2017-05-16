package com.stormpath.sdk.impl.mail;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.impl.error.DefaultError;
import com.stormpath.sdk.impl.io.ResourceFactory;
import com.stormpath.sdk.impl.mail.template.EmailTemplate;
import com.stormpath.sdk.mail.EmailRequest;
import com.stormpath.sdk.mail.EmailService;
import com.stormpath.sdk.mail.MimeType;
import com.stormpath.sdk.mail.config.EmailServiceConfig;
import com.stormpath.sdk.impl.mail.template.DefaultTemplateRenderRequest;
import com.stormpath.sdk.mail.templates.TemplateRenderRequest;
import com.stormpath.sdk.mail.templates.TemplateRenderer;
import com.stormpath.sdk.lang.Strings;
import com.stormpath.sdk.resource.ResourceException;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.stormpath.sdk.mail.MimeType.*;

/**
 */
public class CommonsEmailService implements EmailService {

    private static final String FROM_NAME = "fromName";
    private static final String FROM_EMAIL_ADDRESS = "fromEmailAddress";
    private static final String TO_NAME = "toName";
    private static final String TO_EMAIL_ADDRESS = "toEmailAddress";
    private static final String SUBJECT = "subject";
    private static final String APPLICATION_BASE_URL = "url";
    private static final String EXPIRE_WINDOW_HOURS = "expirationWindow";

    private final Logger log = LoggerFactory.getLogger(CommonsEmailService.class);

    private final TemplateRenderer templateRenderer;
    private final EmailServiceConfig config;
    private final ResourceFactory resourceFactory;

    public CommonsEmailService(EmailServiceConfig config, TemplateRenderer templateRenderer, ResourceFactory resourceFactory) {

        this.config = config;
        this.templateRenderer = templateRenderer;
        this.resourceFactory = resourceFactory;
    }

    @Override
    public void sendValidationEmail(EmailRequest request) {
        sendEmail(config.getValidationTemplateConfig(), request);
    }

    @Override
    public void sendResetEmail(EmailRequest request) {
        sendEmail(config.getResetPasswordTemplateConfig(), request);
    }

    private void sendEmail(String template, EmailRequest request) {

        try (InputStream inputStream = resourceFactory.createResource(template).getInputStream()) {

            Map<String, Object> context = new LinkedHashMap<>();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            EmailTemplate emailTemplate = objectMapper.readValue(inputStream, EmailTemplate.class);

            String url = emailTemplate.getDefaultModel().get("linkBaseUrl") + "?sptoken=" + request.getToken();

            context.put(APPLICATION_BASE_URL, url);

            context.put(FROM_NAME, emailTemplate.getFromName());
            context.put(FROM_EMAIL_ADDRESS, emailTemplate.getFromName());
            context.put(SUBJECT, emailTemplate.getSubject());

            context.put(EXPIRE_WINDOW_HOURS, config.getTokenExpirationHours());

            context.put(TO_EMAIL_ADDRESS, request.getToAddress());
            context.put(TO_NAME, request.getToDisplayName());

            try {
                Email email;
                MimeType mimeType = MimeType.fromString(emailTemplate.getMimeType());

                if (PLAIN_TEXT == mimeType) {

                    SimpleEmail simpleEmail = new SimpleEmail();
                    simpleEmail.setMsg(renderEmail(emailTemplate.getTextBody(), context).toString());
                    email = simpleEmail;
                } else if (HTML == mimeType) {

                    HtmlEmail htmlEmail = new HtmlEmail();
                    htmlEmail.setHtmlMsg(renderEmail(emailTemplate.getHtmlBody(), context).toString());
                    email = htmlEmail;
                } else { // both
                    HtmlEmail htmlEmail = new HtmlEmail();
                    htmlEmail.setHtmlMsg(renderEmail(emailTemplate.getHtmlBody(), context).toString());
                    htmlEmail.setTextMsg(renderEmail(emailTemplate.getTextBody(), context).toString());
                    email = htmlEmail;
                }

                email.setFrom(emailTemplate.getFromEmailAddress(), emailTemplate.getFromName());
                email.addTo(request.getToAddress(), request.getToDisplayName());
                email.setSubject(emailTemplate.getSubject());

                // now send it away
                sendEmail(email);
            } catch (EmailException e) {

                String message = "Failed to send email.";
                log.warn(message, e);

                DefaultError error = new DefaultError(null);
                error.setMessage(message);
                error.setDeveloperMessage(e.getMessage());

                throw new ResourceException(error);
            }
        } catch (IOException e) {
            String message = "Failed to parse email template.";
            log.warn(message, e);

            DefaultError error = new DefaultError(null);
            error.setStatus(500);
            error.setMessage(message);
            error.setDeveloperMessage(e.getMessage());

            throw new ResourceException(error);
        }
    }

    protected void sendEmail(Email email) throws EmailException {

        email.setHostName(config.getHostName());
        email.setSmtpPort(config.getPort());
        email.setStartTLSEnabled(config.isTLS());

        if (Strings.hasText(config.getUsername())) {
            email.setAuthentication(config.getUsername(), config.getPassword());
        }

        if (config.isSSL()) {
            email.setSSLOnConnect(config.isSSL());
            email.setSslSmtpPort(Integer.toString(config.getPort()));
            email.setSSLCheckServerIdentity(config.isSSLCheckServerIdentity());
        }

        email.send();
    }

    private CharSequence renderEmail(String templateBody, Map<String, Object> context) {

        TemplateRenderRequest request = new DefaultTemplateRenderRequest();
        request.setTemplate(templateBody);
        request.setContext(context);
        return templateRenderer.render(request);
    }
}
