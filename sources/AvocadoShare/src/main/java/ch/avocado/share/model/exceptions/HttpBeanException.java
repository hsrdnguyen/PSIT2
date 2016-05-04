package ch.avocado.share.model.exceptions;

import ch.avocado.share.common.HttpStatusCode;
import ch.avocado.share.common.constants.ErrorMessageConstants;
import ch.avocado.share.service.exceptions.*;

import static ch.avocado.share.common.HttpStatusCode.*;

/**
 * Created by coffeemakr on 22.03.16.
 */

public class HttpBeanException extends Exception{
    private final String description;
    private final int statusCode;
    private final Throwable originalCause;
    //private final Map<String, String> headers;

    public HttpBeanException(DataHandlerException e) {
        this(INTERNAL_SERVER_ERROR, ErrorMessageConstants.DATAHANDLER_EXPCEPTION, e);
    }

    public HttpBeanException(int statusCode, String description, Throwable originalCause) {
        super(description);
        this.statusCode = statusCode;
        this.description = description;
        this.originalCause = originalCause;
    }

    public HttpBeanException(int statusCode, String description) {
        this(statusCode, description, null);
    }

    public HttpBeanException(HttpStatusCode statusCode, String description) {
        this(statusCode.getCode(), description, null);
    }

    public HttpBeanException(HttpStatusCode statusCode, String description, Throwable originalCause) {
        this(statusCode.getCode(), description, originalCause);
    }

    public HttpBeanException(ServiceException e) {
        if(e instanceof ObjectNotFoundException) {
            statusCode = NOT_FOUND.getCode();
            description  = ErrorMessageConstants.OBJECT_NOT_FOUND;
        } else {
            statusCode = INTERNAL_SERVER_ERROR.getCode();
            if(e instanceof DataHandlerException){
                description = ErrorMessageConstants.DATAHANDLER_EXPCEPTION;
            } else if(e instanceof ServiceNotFoundException) {
                description = ErrorMessageConstants.SERVICE_NOT_FOUND + ((ServiceNotFoundException) e).getService();
            } else if(e instanceof FileStorageException) {
                description = ErrorMessageConstants.FILE_STORAGE_EXCEPTION;
            } else {
                description = ErrorMessageConstants.UNKNOWN_SERVICE_EXCEPTION;
            }
        }
        originalCause = e;
    }

    public HttpBeanException(AccessDeniedException e) {
        this(HttpStatusCode.FORBIDDEN, ErrorMessageConstants.ACCESS_DENIED, e);
    }


    public String getDescription() {
        return this.description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Throwable getOriginalCause() {
        return originalCause;
    }
}
