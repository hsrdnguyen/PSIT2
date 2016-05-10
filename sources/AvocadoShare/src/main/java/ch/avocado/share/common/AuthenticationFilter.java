package ch.avocado.share.common;

import ch.avocado.share.controller.UserSession;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Use the confiuration name {@value CONFIG_EXCLUDE} to specify
 * a pattern of exluded urls.
 */
public class AuthenticationFilter implements Filter{

    /**
     * The configuration entry for this filter.
     */
    private static final String CONFIG_EXCLUDE = "exclude";

    private Pattern excludePattern = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludeConfig = filterConfig.getInitParameter(CONFIG_EXCLUDE);
        if(excludeConfig != null) {
            excludePattern = Pattern.compile(excludeConfig);
        }
    }

    private boolean isExcluded(UrlHelper urlHelper) {
        if(excludePattern == null) {
            return false;
        }
        Matcher matcher = excludePattern.matcher(urlHelper.getPathAndQueryWithoutBase());
        return matcher.matches();
    }

    private void redirectToLogin(UrlHelper urlHelper, HttpServletResponse response) throws IOException {
        String url = urlHelper.getLoginUrlWithRedirect();
        response.sendRedirect(url);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean blocked = false;
        if(!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("Can only handle HTTP.");
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        UrlHelper urlHelper = new UrlHelper(httpServletRequest);
        if(!isExcluded(urlHelper)) {
            UserSession userSession = new UserSession(httpServletRequest);
            if(!userSession.isAuthenticated()) {
                blocked = true;
            }
        }
        if(!blocked) {
            chain.doFilter(request, response);
        }else {
            redirectToLogin(urlHelper, httpServletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
