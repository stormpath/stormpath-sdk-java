package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.servlet.form.Field;

import java.util.List;

/**
 * @since 1.0.0
 */
public interface FormFieldsFactory {
    List<Field> getFormFields();
}
