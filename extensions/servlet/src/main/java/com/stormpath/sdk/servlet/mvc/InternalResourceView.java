package com.stormpath.sdk.servlet.mvc;

import com.stormpath.sdk.lang.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 1.0.RC8
 */
public class InternalResourceView implements View {

    private String path;

    @SuppressWarnings("unused")
    public InternalResourceView() {
    }

    public InternalResourceView(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, ViewModel vm) throws Exception {

        Map<String, ?> model = vm.getModel();

        setAttributes(request, model);

        request.getRequestDispatcher(getPath()).forward(request, response);
    }

    protected void setAttributes(HttpServletRequest request, Map<String, ?> model) {

        if (Collections.isEmpty(model)) {
            return;
        }

        for (String key : model.keySet()) {
            Object value = model.get(key);
            if (value != null) {
                request.setAttribute(key, value);
            }
        }
    }
}
