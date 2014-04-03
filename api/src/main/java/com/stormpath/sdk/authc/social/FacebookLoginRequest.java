package com.stormpath.sdk.authc.social;

public class FacebookLoginRequest extends AbstractSocialLoginRequest {

    private FacebookLoginRequest(TokenType tokenType, String tokenValue) {
        super(SocialLoginProvider.FACEBOOK, tokenType, tokenValue);
    }

    public static class Builder extends OAuthInfo.Builder<Builder> {

        public SocialLoginRequest build() {
            if (super.accessToken != null) {
                new FacebookLoginRequest(TokenType.ACCESS_TOKEN, accessToken);
            }
            return null;//Exception here
        }
    }

}