package ch.avocado.share.common;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filter to hide included files or other files not meant to be
 * run directly by the user.
 */
public class HiddenFilesFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.sendError(HttpStatusCode.NOT_FOUND.getCode());
    }

    @Override
    public void destroy() {
    }
}
