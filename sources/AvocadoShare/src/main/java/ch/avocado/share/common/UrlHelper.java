package ch.avocado.share.common;


import ch.avocado.share.model.data.File;
import ch.avocado.share.servlet.DownloadServlet;
import ch.avocado.share.servlet.LoginServlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by coffeemakr on 26.04.16.
 */
public class UrlHelper {

    private final HttpServletRequest request;

    public UrlHelper(HttpServletRequest request) {
        if(request == null) throw new IllegalArgumentException("request is null");
        this.request = request;
    }

    public String getBase() {
        return request.getServletContext().getContextPath();
    }

    public String getDownloadUrl(File file) {
        return getBase() + DownloadServlet.getDownloadUrl(file);
    }

    private String encodeUrlParameter(String name, String value) {
        return Encoder.forUrl(name) + "=" + Encoder.forUrl(value);
    }

    public String getCurrentUrl() {
        String currentUrl = (String) request.getAttribute("javax.servlet.error.request_uri");
        if(currentUrl == null) {
            currentUrl = (String) request.getAttribute("javax.servlet.forward.request_uri");
        }
        if(currentUrl == null) {
            currentUrl = request.getRequestURI();
        }
        String query = request.getQueryString();
        if(query != null) {
            currentUrl += "?" + query;
        }
        return currentUrl;
    }

    public String getPathAndQuery() {
        String path = request.getRequestURI();
        String query = request.getQueryString();
        if(query != null) {
            path += "?" + query;
        }
        return path;
    }

    public String getLoginUrl() {
        return this.getBase() + "/login";
    }

    public String getLoginUrlWithRedirect() {
        String currentUrl = getPathAndQuery();
        return getLoginUrl() + "?" + encodeUrlParameter(LoginServlet.FIELD_REDIRECT_TO, currentUrl);
    }
}
