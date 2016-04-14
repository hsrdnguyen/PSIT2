package ch.avocado.share.servlet.resources.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by coffeemakr on 14.04.16.
 */
class ViewConfig {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final View view;

    public ViewConfig(View view, HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.view = view;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public View getView() {
        return view;
    }
}
