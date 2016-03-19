package ch.avocado.share.helper;

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
 * Servlet Filter implementation class AuthentificationFilter
 */
@WebFilter(description = "Check authentification")
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
		boolean authenticated = true;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		if(!request.isSecure()) {
			// TODO redirect to HTTPS://
		} else {
			// Check if user is authenticated
		}
		if(authenticated) {
			// pass the request along the filter chain
			chain.doFilter(request, response);
		} else {
			httpResponse.sendRedirect(getLoginUrl((HttpServletRequest) request));
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
