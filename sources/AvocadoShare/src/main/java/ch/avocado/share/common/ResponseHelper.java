package ch.avocado.share.common;

import ch.avocado.share.model.exceptions.HttpServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Helper for sending http responses.
 */
public class ResponseHelper {
    public static final String EXCEPTION_ATTRIBUTE = "ch.avocado.share.servlet.resources.base.ExtendedHttpServlet.excpetion";

    static public void sendErrorFromHttpBeanException(HttpServletException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setAttribute(EXCEPTION_ATTRIBUTE, e);
        response.sendError(e.getStatusCode().getCode(), e.getDescription());
    }

    public static void redirectToOrigin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UrlHelper urlHelper = new UrlHelper(req);
        String referer = urlHelper.getReferrer();
        if(referer == null) {
            resp.sendRedirect(urlHelper.getNoRefererPage());
        } else {
            resp.sendRedirect(referer);
        }
    }
}
