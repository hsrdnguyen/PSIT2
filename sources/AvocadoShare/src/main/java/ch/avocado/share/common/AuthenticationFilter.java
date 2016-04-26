package ch.avocado.share.common;

import ch.avocado.share.controller.UserSession;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationFilter implements Filter{

    private final String CONFIG_EXCLUDE = "exclude";

    private Pattern excludePattern = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String excludeConfig = filterConfig.getInitParameter(CONFIG_EXCLUDE);
        if(excludeConfig != null) {
            excludePattern = Pattern.compile(excludeConfig);
        }
    }

    private String getPathAndQuery(HttpServletRequest request) {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        if(query != null) {
            path += "?" + query;
        }
        return path;
    }

    private boolean isExluded(HttpServletRequest request) {
        if(excludePattern == null) {
            return false;
        }
        if(request == null) {
            throw new IllegalArgumentException("request is null");
        }
        Matcher matcher = excludePattern.matcher(getPathAndQuery(request));
        System.out.println(getPathAndQuery(request));
        return matcher.matches();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean blocked = false;
        if(!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            throw new ServletException("Can only handle HTTP.");
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if(!isExluded(httpServletRequest)) {
            UserSession userSession = new UserSession(httpServletRequest);
            if(!userSession.isAuthenticated()) {
                httpServletResponse.sendError(HttpStatusCode.UNAUTHORIZED.getCode(), "Sie müssen sich anmelden");
                blocked = true;
            }
        }else {
            System.out.println("Excluded.");
        }
        if(!blocked) {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {

    }
}
