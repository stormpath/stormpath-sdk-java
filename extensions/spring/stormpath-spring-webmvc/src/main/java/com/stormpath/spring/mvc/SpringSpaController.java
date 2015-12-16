package com.stormpath.spring.mvc;

import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.http.MediaType;
import com.stormpath.sdk.servlet.http.UserAgent;
import com.stormpath.sdk.servlet.http.UserAgents;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SpringSpaController extends AbstractController {

    private final Controller delegate;

    private final Controller spaViewController;

    private final String jsonView;

    private final List<MediaType> producedMediaTypes;

    public SpringSpaController(Controller delegate, Controller spaViewController, String jsonView, List<MediaType> producedMediaTypes) {
        Assert.notNull(delegate, "Delegate controller cannot be null.");
        Assert.notNull(spaViewController, "SPA view controller cannot be null.");
        Assert.notEmpty(producedMediaTypes, "produced media types cannot be null or empty.");
        Assert.hasText(jsonView, "jsonView cannot be null or empty.");
        this.delegate = delegate;
        this.spaViewController = spaViewController;
        this.jsonView = jsonView;
        this.producedMediaTypes = producedMediaTypes;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Controller controller = delegate;

        String method = request.getMethod();

        boolean json = renderJson(request);

        if (HttpMethod.GET.name().equalsIgnoreCase(method) && !json) {
            //SPA mode = html5 mode = always render the SPA view, regardless of the request URI:
            controller = spaViewController;
        }

        ModelAndView mav = controller.handleRequest(request, response);

        if (json) {
            mav.setViewName(jsonView);
        }

        return mav;
    }

    private boolean renderJson(HttpServletRequest request) {

        if (producedMediaTypes.size() == 1) {
            //only produce one media type, so we return that kind no matter what is accepted:
            if (MediaType.APPLICATION_JSON.includes(producedMediaTypes.iterator().next())) {
                return true;
            }
        } else {
            //more than one type is produced, so return based on UA preference:
            UserAgent ua = UserAgents.get(request);
            return ua.isJsonPreferred();
        }

        return false;
    }
}
