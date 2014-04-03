package com.stormpath.sdk.authc.social;

public class GoogleLoginRequest extends AbstractSocialLoginRequest {

    private GoogleLoginRequest(TokenType tokenType, String tokenValue) {
        super(SocialLoginProvider.GOOGLE, tokenType, tokenValue);
    }

    public static class Builder extends OAuthInfo.Builder<Builder> {

        private String code;

        public Builder setCode(String code) {
            this.code = code;
            return this;
        }

        public SocialLoginRequest build() {
            //Exception here if both code and accessToken were set
            if (this.code != null) {
                return new GoogleLoginRequest(TokenType.CODE, code);
            }
            if (super.accessToken != null) {
                return new GoogleLoginRequest(TokenType.ACCESS_TOKEN, accessToken);
            }
            return null; //Exception here
        }
    }
}