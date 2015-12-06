package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.form.Form;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @since 1.0.RC7
 */
public interface ErrorModelFactory {

    List<String> toErrors(HttpServletRequest request, Form form, Exception e);
}
