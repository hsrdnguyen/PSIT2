package ch.avocado.share.common;


import ch.avocado.share.model.data.*;
import ch.avocado.share.servlet.AvatarServlet;
import ch.avocado.share.servlet.DownloadServlet;
import ch.avocado.share.servlet.LoginServlet;
import ch.avocado.share.servlet.resources.base.ResourceServlet;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by coffeemakr on 26.04.16.
 */
public class UrlHelper {

    private final HttpServletRequest request;

    public UrlHelper(HttpServletRequest request) {
        if(request == null) throw new NullPointerException("request is null");
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

    public String getPathAndQueryWithoutBase() {
        String contextPath = getBase();
        String path = getPathAndQuery();
        if(!path.startsWith(contextPath)) {
            throw new RuntimeException("path doesn't start with base");
        } else {
            return path.substring(contextPath.length());
        }
    }

    public String getLoginUrl() {
        return this.getBase() + "/login";
    }

    public String getLoginUrlWithRedirect() {
        String currentUrl = getPathAndQuery();
        return getLoginUrl() + "?" + encodeUrlParameter(LoginServlet.FIELD_REDIRECT_TO, currentUrl);
    }

    public String getAvatarUrl(String userId) {
        return getBase() + "/avatar?" + encodeUrlParameter(AvatarServlet.PARAMETER_USER_ID, userId);
    }

    public String getReferrer() {
        String referer = request.getHeader("Referer");
        if(referer != null) {
            // TODO: muellcy1 add check for referer validity.
            return referer;
        }
        return null;
    }

    public String getNoRefererPage() {
        return getBase() + "/noreferer.jsp";
    }

    public String getCategoryServletUrl() {
        return getBase() + "/category";
    }

    public String getRatingUrl() {
        return getBase() + "/rating";
    }

    public String getUrlFor(File file) {
        if(file == null) throw new NullPointerException("file is null");
        if(file.getId() == null) throw new IllegalArgumentException("file id is null");
        return getBase() + "/file?" + encodeUrlParameter("id", file.getId());
    }
    public String getUrlFor(Module module) {
        if(module == null) throw new NullPointerException("module is null");
        if(module.getId() == null) throw new IllegalArgumentException("module id is null");
        return getBase() + "/module?" + encodeUrlParameter("id", module.getId());

    }
    public String getUrlFor(Group group) {
        if(group == null) throw new NullPointerException("group is null");
        if(group.getId() == null) throw new IllegalArgumentException("group id is null");
        return getBase() + "/group?" + encodeUrlParameter("id", group.getId());


    }
    public String getUrlFor(User user) {
        if(user == null) throw new NullPointerException("user is null");
        if(user.getId() == null) throw new IllegalArgumentException("user id is null");
        return getBase() + "/user?" + encodeUrlParameter("id", user.getId());
    }

    public String getUrlFor(AccessControlObjectBase object) {
        if(object == null) throw new NullPointerException("object is null");
        if(object.getId() == null) throw new IllegalArgumentException("object's id is null");
        if(object instanceof File) {
            return getUrlFor((File) object);
        } else if(object instanceof Group) {
            return getUrlFor((Group) object);
        } else if(object instanceof  Module) {
            return getUrlFor((Module) object);
        } else if(object instanceof User) {
            return getUrlFor((User) object);
        } else {
            throw new IllegalArgumentException("object has unknown class");
        }
    }
}
