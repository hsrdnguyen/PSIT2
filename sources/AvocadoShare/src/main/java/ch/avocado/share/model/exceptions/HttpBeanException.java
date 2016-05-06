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
    private final HttpStatusCode statusCode;
    
    public HttpBeanException(DataHandlerException e) {
        this(INTERNAL_SERVER_ERROR, ErrorMessageConstants.DATAHANDLER_EXPCEPTION, e);
    }

    public HttpBeanException(HttpStatusCode statusCode, String description) {
        super(description);
        this.description = description;
        this.statusCode = statusCode;
    }

    public HttpBeanException(HttpStatusCode statusCode, String description, Throwable originalCause) {
        super(description, originalCause);
        this.statusCode = statusCode;
        this.description = description;
    }

    private static String getErrorMessageFromServiceException(ServiceException e) {
        if(e instanceof ObjectNotFoundException) {
            return ErrorMessageConstants.OBJECT_NOT_FOUND;
        } else {
            if(e instanceof DataHandlerException){
                return ErrorMessageConstants.DATAHANDLER_EXPCEPTION;
            } else if(e instanceof ServiceNotFoundException) {
                return ErrorMessageConstants.SERVICE_NOT_FOUND + ((ServiceNotFoundException) e).getService();
            } else if(e instanceof FileStorageException) {
                return ErrorMessageConstants.FILE_STORAGE_EXCEPTION;
            } else {
                return ErrorMessageConstants.UNKNOWN_SERVICE_EXCEPTION;
            }
        }
    }

    private static HttpStatusCode getStatusCodeFromServiceException(ServiceException e) {
        if(e instanceof ObjectNotFoundException) {
            return NOT_FOUND;
        } else {
            return INTERNAL_SERVER_ERROR;
        }
    }

    public HttpBeanException(ServiceException e) {
        this(getStatusCodeFromServiceException(e), getErrorMessageFromServiceException(e), e);
    }

    public HttpBeanException(AccessDeniedException e) {
        this(HttpStatusCode.FORBIDDEN, ErrorMessageConstants.ACCESS_DENIED, e);
    }


    public String getDescription() {
        return this.description;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

}
