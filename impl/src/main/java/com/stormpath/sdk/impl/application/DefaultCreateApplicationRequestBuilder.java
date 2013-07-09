package com.stormpath.sdk.impl.application;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.CreateApplicationRequest;
import com.stormpath.sdk.application.CreateApplicationRequestBuilder;

/**
 * @since 0.8
 */
public class DefaultCreateApplicationRequestBuilder implements CreateApplicationRequestBuilder {

    private Application application;
    private boolean createDirectory;
    private String directoryName;

    public DefaultCreateApplicationRequestBuilder(Application application) {
        this.application = application;
    }

    @Override
    public CreateApplicationRequestBuilder createDirectory() {
        this.createDirectory = true;
        return this;
    }

    @Override
    public CreateApplicationRequestBuilder createDirectoryNamed(String directoryName) {
        if (directoryName != null) {
            this.createDirectory = true;
        }
        this.directoryName = directoryName;
        return this;
    }

    public CreateApplicationRequest build() {
        if (createDirectory) {
            return new CreateApplicationAndDirectoryRequest(application, directoryName);
        }
        return new DefaultCreateApplicationRequest(application);
    }
}
