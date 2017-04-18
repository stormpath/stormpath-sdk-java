package com.stormpath.sdk.application;

/**
 * Marks an Application as supporting OAuth and adds required methods for handling tokens.
 */
public interface OAuthApplication extends Application, OAuthAuthenticator {

}
