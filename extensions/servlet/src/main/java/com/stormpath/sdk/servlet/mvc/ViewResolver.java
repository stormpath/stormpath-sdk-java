package com.stormpath.sdk.servlet.mvc;

import javax.servlet.http.HttpServletRequest;

/**
 * 1.0.RC8
 */
public interface ViewResolver {

    View getView(ViewModel model, HttpServletRequest request);

}
