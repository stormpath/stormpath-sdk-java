package com.stormpath.sdk.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 1.0.RC8
 */
public interface View {

    void render(HttpServletRequest request, HttpServletResponse response, ViewModel model) throws Exception;
}
