package com.stormpath.sdk.servlet.filter;

import com.stormpath.sdk.servlet.filter.mvc.ControllerFilter;
import com.stormpath.sdk.servlet.mvc.MeController;

import javax.servlet.ServletException;

/**
 * Created by mzumbado on 4/18/16.
 */
public class MeFilter extends ControllerFilter {


    @Override
    protected void onInit() throws ServletException {

        MeController controller = new MeController();
//        controller.setUri(getConfig().getLoginUrl());
//        controller.setView("stormpath/login");
//        controller.init();

        setController(controller);

        super.onInit();
    }
}
