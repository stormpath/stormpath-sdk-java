package com.stormpath.sdk.factor;


// todo: mehrshad

public interface CreateFactorRequest<T extends Factor, R extends FactorOptions>{

    T getFactor();

    boolean hasFactorOptions();

    boolean isCreateChallenge();

    R getFactorOptions() throws IllegalStateException;

}
