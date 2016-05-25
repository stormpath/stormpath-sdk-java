package com.stormpath.sdk.servlet.mvc;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class ErrorModel {
    private final int status;
    private final String message;

    public static Builder builder() {
        return new Builder();
    }

    public ErrorModel(Builder builder) {
        this.status = builder.status;
        this.message = builder.message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("status", status);
        result.put("message", message);
        return result;
    }


    public static class Builder {
        private int status = 400;
        private String message;

        public Builder setStatus(int status) {
            this.status = status;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public ErrorModel build() {
            return new ErrorModel(this);
        }
    }
}
