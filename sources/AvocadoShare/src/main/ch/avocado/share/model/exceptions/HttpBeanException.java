package ch.avocado.share.model.exceptions;

import javax.xml.ws.http.HTTPException;

/**
 * Created by coffeemakr on 22.03.16.
 */

public class HttpBeanException extends Exception{
    private final String description;
    private final int statusCode;

    public HttpBeanException(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
