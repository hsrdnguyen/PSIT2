package ch.avocado.share.common;

import ch.avocado.share.model.exceptions.HttpServletException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Helper for sending http responses.
 */
public class ResponseHelper {
    public static final String EXCEPTION_ATTRIBUTE = "ch.avocado.share.servlet.resources.base.ExtendedHttpServlet.excpetion";

    static public void sendErrorFromHttpBeanException(HttpServletException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setAttribute(EXCEPTION_ATTRIBUTE, e);
        response.sendError(e.getStatusCode().getCode(), e.getMessage());
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

    public static void sendJsonErrorFromException(HttpServletException e, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(e.getStatusCode().getCode());
        PrintWriter writer = response.getWriter();
        writer.print("{\"errors\": [");
        writer.print("\"status\": ");
        writer.print(e.getStatusCode().getCode());
        writer.print(",\"title\":");
        writer.print("\"" + e.getStatusCode().getMessage() + "\"");
        writer.print(",\"detail\":");
        writer.print("\"" + e.getMessage()+ "\"");
        writer.print("]}");
        writer.close();
    }

    public static void sendJsonData(String data, HttpServletResponse response) throws IOException {
        PrintWriter printWriter = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        printWriter.print("{\"data\": " + data + "}");
    }
}
