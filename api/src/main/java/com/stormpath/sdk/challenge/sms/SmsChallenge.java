package com.stormpath.sdk.challenge.sms;

import com.stormpath.sdk.challenge.Challenge;
import com.stormpath.sdk.factor.Factor;

// todo mehrshad
public interface SmsChallenge<T extends Factor, R extends Enum>  extends Challenge<T,R>{
    /**
     * Returns the message associated with this challenge.
     * The message contains a code sent to the user to be sent back
     * for authentication.
     *
     * @return message associated with this challenge
     */
    String getMessage();

    /**
     * Sets the message associated with this challenge.
     * This is ONLY to be used upon creation of a challenge if users want to overwrite the
     * default message used in Stormpath.
     *
     *
     * @param message the message associated with this challenge. Message hast to contain a the macro
     *                '${code}'. This would be replaced with the code sent out within the message.
     * @return this instance for method chaining.
     */
    Challenge setMessage(String message);
}
