package com.stormpath.sdk.challenge.google;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.factor.sms.SmsFactor;

// todo mehrshad
public interface GoogleAuthenticatorChallenge<T extends SmsFactor>  extends Challenge<T>{

}
