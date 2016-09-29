package com.stormpath.sdk.impl.factor.google;

import com.stormpath.sdk.factor.CreateFactorRequest;
import com.stormpath.sdk.factor.FactorOptions;
import com.stormpath.sdk.factor.google.CreateGoogleAuthenticatorFactorRequestBuilder;
import com.stormpath.sdk.factor.google.GoogleAuthenticatorFactor;

/**
 * Created by mehrshadrafiei on 9/28/16.
 */
public class DefaultCreateGoogleAuthenticatorFactorRequestBuilder implements CreateGoogleAuthenticatorFactorRequestBuilder {

    @Override
    public CreateGoogleAuthenticatorFactorRequestBuilder withResponseOptions(FactorOptions options) throws IllegalArgumentException {
        return null;
    }

    @Override
    public CreateGoogleAuthenticatorFactorRequestBuilder createChallenge() {
        return null;
    }

    @Override
    public CreateFactorRequest<GoogleAuthenticatorFactor, FactorOptions> build() {
        return null;
    }
}
