package com.stormpath.sdk.challenge;

import com.stormpath.sdk.lang.Classes;

import java.lang.reflect.Constructor;

/**
 * Created by mehrshadrafiei on 9/8/16.
 */
public final class Challenges {
    private static final Class<CreateChallengeRequestBuilder> BUILDER_CLASS =
            Classes.forName("com.stormpath.sdk.impl.challenge.DefaultCreateChallengeRequestBuilder");

    //prevent instantiation
    private Challenges() {
    }

    public static ChallengeOptions<ChallengeOptions> options() {
        return (ChallengeOptions) Classes.newInstance("com.stormpath.sdk.impl.challenge.DefaultChallengeOptions");
    }

    public static CreateChallengeRequestBuilder newCreateRequestFor(Challenge challenge) {
        Constructor ctor = Classes.getConstructor(BUILDER_CLASS, Challenge.class);
        return (CreateChallengeRequestBuilder) Classes.instantiate(ctor, challenge);
    }
}
