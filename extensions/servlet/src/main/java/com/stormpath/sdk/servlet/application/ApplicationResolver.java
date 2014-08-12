package com.stormpath.sdk.servlet.application;

import com.stormpath.sdk.application.Application;

import javax.servlet.ServletContext;

public interface ApplicationResolver {

    Application getApplication(ServletContext servletContext);
}
