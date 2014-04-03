package com.stormpath.sdk.authc.social;

public interface OAuthInfo {

    public String getAccessToken();

    public class Builder<T extends Builder<T>> {

        protected String accessToken;

        public T setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return (T) this;
        }
    }
}
