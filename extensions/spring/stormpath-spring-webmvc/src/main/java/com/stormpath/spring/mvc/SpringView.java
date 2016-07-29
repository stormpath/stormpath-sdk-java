package com.stormpath.spring.mvc;

import com.stormpath.sdk.lang.Assert;
import com.stormpath.sdk.servlet.mvc.ViewModel;
import com.stormpath.sdk.servlet.util.RedirectUrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @since 1.0.0
 */
public class SpringView implements com.stormpath.sdk.servlet.mvc.View {

    private static final Logger log = LoggerFactory.getLogger(SpringView.class);

    private final LocaleResolver localeResolver;
    private final List<ViewResolver> viewResolvers;
    private final HandlerInterceptor handlerInterceptor; //hacky

    public SpringView(Collection<ViewResolver> viewResolvers, LocaleResolver localeResolver, HandlerInterceptor templateHandlerInterceptor) {

        Assert.notEmpty(viewResolvers, "viewResolvers cannot be null or empty.");
        Assert.notNull(localeResolver, "localeResolver cannot be null.");
        Assert.notNull(templateHandlerInterceptor, "templateHandlerInterceptor cannot be null.");
        this.handlerInterceptor = templateHandlerInterceptor;

        List<ViewResolver> l = new ArrayList<>(viewResolvers);
        //it is important to keep view resolvers sorted based on priority.  Spring's DispatcherServlet does this
        //so we need to do the same:
        AnnotationAwareOrderComparator.sort(l);

        this.viewResolvers = l;
        this.localeResolver = localeResolver;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, ViewModel vm) throws Exception {
        ModelAndView mav = convert(vm, request);
        handlerInterceptor.postHandle(request, response, null, mav);
        render(mav, request, response);
    }

    private ModelAndView convert(ViewModel vm, HttpServletRequest request) {

        String viewName = vm.getViewName();
        Assert.hasText(viewName, "ViewModel must contain a viewName.");

        /*
        if (log.isDebugEnabled()) {
            String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
            log.debug("Returning view name '{}' for lookup path [{}]", viewName, lookupPath);
        }
        */

        Map<String, ?> model = vm.getModel();

        boolean redirect = vm.isRedirect();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(RequestContextUtils.getInputFlashMap(request));
        modelAndView.addAllObjects(model);

        if (redirect) {
            //Fix to redirect to correct context path: https://github.com/stormpath/stormpath-sdk-java/issues/210
            String targetUrl = new RedirectUrlBuilder(request).setUrl(viewName).setContextRelative(true).build();
            modelAndView.setView(new RedirectView(targetUrl));
        } else {
            modelAndView.setViewName(viewName);
        }

        return modelAndView;
    }

    /**
     * Render the given ModelAndView.
     * <p>This is the last stage in handling a request. It may involve resolving the view by name.
     *
     * @param mav      the ModelAndView to render
     * @param request  current HTTP servlet request
     * @param response current HTTP servlet response
     * @throws ServletException if view is missing or cannot be resolved
     * @throws Exception        if there's a problem rendering the view
     */
    private void render(ModelAndView mav, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Determine locale for request and apply it to the response.
        Locale locale = this.localeResolver.resolveLocale(request);
        response.setLocale(locale);

        View view;

        Map<String, Object> model = mav.getModelMap();

        if (mav.isReference()) {
            // We need to resolve the view name.
            view = resolveViewName(mav.getViewName(), model, locale, request);
            if (view == null) {
                throw new ServletException("Could not resolve view with name '" + mav.getViewName() + "'");
            }
        } else {
            // No need to lookup: the ModelAndView object contains the actual View object.
            view = mav.getView();
            if (view == null) {
                throw new ServletException("ModelAndView [" + mav + "] neither contains a view name nor a View object.");
            }
        }
        // Delegate to the View object for rendering.
        log.debug("Rendering Spring view [{}]", view);
        try {
            view.render(model, request, response);
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug("Error rendering Spring view [" + view + "].", ex);
            }
            throw ex;
        }
    }

    /**
     * Resolve the given view name into a View object (to be rendered).
     * <p>The default implementations asks all ViewResolvers of this dispatcher.
     * Can be overridden for custom resolution strategies, potentially based on
     * specific model attributes or request parameters.
     *
     * @param viewName the name of the view to resolve
     * @param model    the model to be passed to the view
     * @param locale   the current locale
     * @param request  current HTTP servlet request
     * @return the View object, or {@code null} if none found
     * @throws Exception if the view cannot be resolved
     *                   (typically in case of problems creating an actual View object)
     * @see ViewResolver#resolveViewName
     */
    private View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request)
        throws Exception {

        for (ViewResolver viewResolver : viewResolvers) {
            View view = viewResolver.resolveViewName(viewName, locale);
            if (view != null) {
                return view;
            }
        }
        return null;
    }
}
