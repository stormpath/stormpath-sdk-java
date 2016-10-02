package com.stormpath.sdk.impl.factor.google;

import com.stormpath.sdk.factor.CreateFactorRequest;
import com.stormpath.sdk.factor.google.CreateGoogleAuthenticatorFactorRequestBuilder;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactorOptions;
import com.stormpath.sdk.impl.factor.AbstractCreateFactorRequestBuilder;

/**
 * Created by mehrshadrafiei on 9/28/16.
 */
    public class DefaultCreateGoogleAuthenticatorFactorRequestBuilder extends AbstractCreateFactorRequestBuilder<GoogleAuthenticatorFactor, GoogleAuthenticatorFactorOptions> implements CreateGoogleAuthenticatorFactorRequestBuilder<GoogleAuthenticatorFactor, GoogleAuthenticatorFactorOptions> {


    private boolean createChallenge;

    public DefaultCreateGoogleAuthenticatorFactorRequestBuilder(GoogleAuthenticatorFactor factor) {
        super(factor);
    }

    @Override
    public DefaultCreateGoogleAuthenticatorFactorRequestBuilder createChallenge() {
        this.createChallenge = true;
        return this;
    }

    @Override
    public CreateFactorRequest<GoogleAuthenticatorFactor, GoogleAuthenticatorFactorOptions> build() {
        return new DefaultGoogleAuthenticatorCreateFactorRequest(factor, options, createChallenge);
    }
}
