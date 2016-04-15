package ch.avocado.share.servlet.resources.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The view configuration should contain everything needed to produce
 * a response of any kind.
 */
public class ViewConfig {
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final View view;

    /**
     * @param view The required view.
     * @param request The request. This can be used to forward the request to another handler.
     * @param response The response into which the result should be written.
     */
    public ViewConfig(View view, HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.view = view;
    }

    /**
     * @return The response into which the result should be written.
     */
    public HttpServletResponse getResponse() {
        return response;
    }

    /**
     * @return The request. This can be used to forward the request to another handler.
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * @return The view type.
     */
    public View getView() {
        return view;
    }
}
