package com.stormpath.sdk.impl.mail.template;

import java.util.Map;

/**
 *
 */
public class EmailTemplate {

    private String mimeType;
    private String fromEmailAddress;
    private String fromName;
    private String subject;
    private Map<String, String> defaultModel;
    private String htmlBody;
    private String textBody;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    public void setFromEmailAddress(String fromEmailAddress) {
        this.fromEmailAddress = fromEmailAddress;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, String> getDefaultModel() {
        return defaultModel;
    }

    public void setDefaultModel(Map<String, String> defaultModel) {
        this.defaultModel = defaultModel;
    }

    public String getHtmlBody() {
        return htmlBody;
    }

    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }

    public String getTextBody() {
        return textBody;
    }

    public void setTextBody(String textBody) {
        this.textBody = textBody;
    }
}
