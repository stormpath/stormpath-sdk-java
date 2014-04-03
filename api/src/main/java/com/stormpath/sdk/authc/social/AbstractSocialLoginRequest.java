package com.stormpath.sdk.authc.social;

public abstract class AbstractSocialLoginRequest implements SocialLoginRequest {

    private final String tokenValue;

    private final TokenType tokenType;

    private final SocialLoginProvider provider;

    protected AbstractSocialLoginRequest(SocialLoginProvider provider, TokenType tokenType, String tokenValue) {
        this.provider = provider;
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    @Override
    public String getTokenType() {
        return this.tokenType.getTypeName();
    }

    @Override
    public String getTokenValue() {
        return this.tokenValue;
    }

    @Override
    public SocialLoginProvider getProvider(){
        return this.provider;
    }

    protected enum TokenType {

        CODE("code"),
        ACCESS_TOKEN("accessToken");

        private final String tokenTypeName;

        private TokenType(String tokenTypeName) {
            this.tokenTypeName = tokenTypeName;
        }

        public String getTypeName() {
            return this.tokenTypeName;
        }

    }

}