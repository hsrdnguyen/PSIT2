package ch.avocado.share.common;

import ch.avocado.share.model.exceptions.HttpBeanException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by coffeemakr on 03.05.16.
 */
public class ResponseHelper {
    static public void sendErrorFromHttpBeanException(HttpBeanException e, HttpServletResponse response) throws IOException {
        response.sendError(e.getStatusCode(), e.getDescription());

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
