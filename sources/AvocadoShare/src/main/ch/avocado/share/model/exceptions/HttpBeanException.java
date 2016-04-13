package ch.avocado.share.model.exceptions;

import ch.avocado.share.common.HttpStatusCode;

import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.util.Map;

/**
 * Created by coffeemakr on 22.03.16.
 */

public class HttpBeanException extends Exception{
    private final String description;
    private final int statusCode;
    //private final Map<String, String> headers;


    public HttpBeanException(int statusCode, String description) {
        super(description);
        this.statusCode = statusCode;
        this.description = description;
        //this.headers = null;
    }

    public HttpBeanException(HttpStatusCode statusCode, String description) {
        this(statusCode.getCode(), description);
    }

    public String getDescription() {
        return this.description;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
