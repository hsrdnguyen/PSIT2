package ch.avocado.share.common;

import ch.avocado.share.controller.UserSession;
import ch.avocado.share.model.data.User;
import ch.avocado.share.model.exceptions.ServiceNotFoundException;
import ch.avocado.share.service.ISecurityHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class AuthenticationFilter
 */
@WebFilter(description = "Check authentication")
public class AuthenticationFilter implements Filter {
	
	/**
	 * @see Filter#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * This method if used to generate the URL where the user can
	 * login and migth be redirected to the previously accessed 
	 * URL (from request).
	 * @param request The original unauthenticated request
	 * @return The url to which the user can be redirected
	 */
	private String getLoginUrl(HttpServletRequest request) {
		String loginUrl = request.getContextPath() + "/login";
		String requestedUrl = request.getRequestURI();
		if (request.getQueryString() != null) {
			requestedUrl += "?" + request.getQueryString();
		}
		try {
			requestedUrl = URLEncoder.encode(requestedUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			requestedUrl = null;
		}
		if(requestedUrl != null && requestedUrl.length() > 0) {
			loginUrl = loginUrl + "?redirect=" + requestedUrl;
		}
		return loginUrl;
	}
	
	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        boolean hasAccess = true;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		UserSession userSession = new UserSession(httpServletRequest);
        ISecurityHandler securityHandler;
        try {
            securityHandler = ServiceLocator.getService(ISecurityHandler.class);
        } catch (ServiceNotFoundException e) {

        }
        User user = userSession.getUser();
        request.setAttribute("session", userSession);
        if(!request.isSecure()) {
            // TODO redirect to HTTPS://
        }

        if(user == null && httpServletRequest.getMethod() != "GET") {
            hasAccess = false;
        }

		if(hasAccess) {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		} else {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	@Override
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
