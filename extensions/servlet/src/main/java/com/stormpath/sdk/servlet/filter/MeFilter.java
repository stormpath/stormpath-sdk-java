package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.MeController;

import javax.servlet.ServletException;

/**
 * @since 1.0.0
 */
public class MeFilter extends ControllerFilter {

    @Override
    protected void onInit() throws ServletException {

        MeController controller = new MeController(getConfig().getMeExpandedProperties());

        setController(controller);

        super.onInit();
    }
}
